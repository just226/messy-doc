package com.zbro.messydoc.master.controller;

import com.zbro.messydoc.master.WorkerEntity;
import com.zbro.messydoc.commons.event.MessyDocEvent;
import com.zbro.messydoc.commons.feign.WorkerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@Slf4j
public class MasterRestController {

    @Autowired
    Map<String, WorkerEntity> workerEntityMap;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    WorkerClient workerClient;


    private List<String> blackList = new LinkedList<>();

    //after worker is up, it will login the master through this url
    @PostMapping("api/v1/worker")
    public boolean createWorker(@RequestBody WorkerEntity body){
        try {
            String url = "http://" + body.getAdvertisedListener() + ":13399";
            URI uri = URI.create(url);
            workerClient.test(uri);
            workerEntityMap.put(body.getId(),body);
            log.info("join new worker {}",body.getId());
            eventPublisher.publishEvent(new MessyDocEvent("healthCheck"));
            return true;
        }catch (Exception e){
            log.error(e.toString());
            if(e.toString().contains("connect timed out")){
                log.error("can't reach the worker");
            }
            return false;
        }
    }


    //after worker is logged in, it will send keepalive to the master through this url
    @PutMapping("api/v1/worker/{workerId}")
    public boolean updateWorker(@PathVariable("workerId") String workerId, @RequestBody WorkerEntity body){
        if(workerEntityMap.get(workerId) != null){
            workerEntityMap.put(workerId,body);
            return true;
        }else return false;
    }

    //used by web page controller
    @GetMapping("start")
    public String startWorker(@RequestParam("id") String id, @RequestParam("task") String taskName){
        if(workerEntityMap.get(id) != null){
            String url = "http://" + workerEntityMap.get(id).getAdvertisedListener() + ":13399";
            URI uri = URI.create(url);
            try{
                return workerClient.startToWork(uri,taskName);
            }catch (Exception e){
                log.error(e.toString());
                return "false";
            }
        }
        return "false";
    }

    //used by web page controller
    @GetMapping("stop")
    public String stopWorker(@RequestParam("id") String id, @RequestParam("taskName") String taskName){
        if(workerEntityMap.get(id) != null){
            String url = "http://" + workerEntityMap.get(id).getAdvertisedListener() + ":13399";
            URI uri = URI.create(url);
            return workerClient.stopWork(uri,taskName);
        }
        return "false";
    }
    //used by web page controller
    @PostMapping("addPath")
    public boolean addPath(@RequestParam("id") String id,@RequestParam("path") String path){

        if(path.equals("")){
            return false;
        }
        String url = "http://" + workerEntityMap.get(id).getAdvertisedListener() + ":13399";
        URI uri = URI.create(url);

        return workerClient.addPath(uri,path);
    }

    //used by web page controller
    @PostMapping("removePath")
    public boolean removePath(@RequestParam("id") String id,@RequestParam("path") String path){

        if(path.equals("")){
            return false;
        }
        String url = "http://" + workerEntityMap.get(id).getAdvertisedListener() + ":13399";
        URI uri = URI.create(url);

        return workerClient.removePath(uri,path);
    }


    //todo, used by worker
    @GetMapping("api/v1/worker/{workerId}")
    public String getWorker(){
        return "";
    }
    //todo , used by worker, delete himself when something wrong happened
    @DeleteMapping("api/v1/worker/{workerId}")
    public String deleteWorker(){
        return "";
    }


}
