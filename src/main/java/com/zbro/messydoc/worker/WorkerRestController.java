package com.zbro.messydoc.worker;

import com.zbro.messydoc.commons.event.MessyDocEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class WorkerRestController {

    @Autowired
    ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    Map<String, DogTaskRecord> dogTaskRecords;

    @Autowired
    WorkerEntitySingleton workerProfile;

//    @Autowired
//    DogTask dogTask;

    @Autowired
    NewDogTask newDogTask;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @PostMapping("worker/startToWork")
    public String startADogTask(@RequestBody String dogName){

        //check if the path is not empty
        if(!workerProfile.getPath().isEmpty()){
            log.debug("Dog {} start to work",dogName);
            if(dogTaskRecords.containsKey(dogName)){
                dogTaskRecords.get(dogName).task.cancel(true);
                dogTaskRecords.remove(dogName);
//                taskScheduler.getScheduledThreadPoolExecutor().getQueue().remove(dogTaskHolder.get(dogName));
            }
            newDogTask.setPath(workerProfile.getPath());
            dogTaskRecords.put(dogName, new DogTaskRecord(dogName,
                    Arrays.toString(workerProfile.getPath().toArray(new String[0])),
                    taskScheduler.scheduleAtFixedRate(newDogTask, Duration.ofMillis(1 * 60 * 30000))));

            workerProfile.setPath(new HashSet<String>());

            return "success";
        }else {
            return "you must specify a valid path to the dog";
        }

    }

    @PostMapping("worker/stopWork")
    public boolean stopADogTask(@RequestBody String dogName)
    {
        if(!dogTaskRecords.isEmpty()){
            log.debug("Dog {} cancel work",dogName);

            /*
              how does the cancel work
              1. if the task is running, cancel result is unpredictable, it may fails
              2. if the task is running，after cancel success, the task won't return to Queue
              3. if task is delayed in Queue, after cancel, it won't run, and then removed from the Queue，no need to remove manually
            */
            dogTaskRecords.get(dogName).task.cancel(true);
            dogTaskRecords.remove(dogName);
            if(dogTaskRecords.isEmpty()){
                workerProfile.setStatus("idle");
                eventPublisher.publishEvent(new MessyDocEvent("update"));
            }
            return true;
        }else return false;

    }

    @PostMapping("worker/test")
    public boolean test()
    {
        return true;
    }

    @GetMapping("worker/dogList")
    public List<String[]> getDogTaskRecords()
    {
        List<String[]> dogList = new ArrayList<>();
        /*
        what is delay
        1. delay is 0 : task is starting
        2. delay is negative : task is running
        3. delay is positive : task is waiting for delay to execute
         */
        dogTaskRecords.forEach((k, v)->dogList.add(new String[]{k,v.path, String.valueOf(v.task.getDelay(TimeUnit.SECONDS))}));
        return dogList;
    }

    @PostMapping("worker/addPath")
    public boolean addPath(@RequestBody String path){
        try{
            path = Paths.get(path,File.separator).toAbsolutePath().toString();
        }catch (Exception e){
            log.warn(e.toString());
            return false;
        }
        if(System.getProperty("os.name").matches("[W|w]indows.*")){
            path =  path.toLowerCase();
        }
        if(new File(path).isDirectory()){
            workerProfile.getPath().add(path);
            eventPublisher.publishEvent(new MessyDocEvent("update"));
        }else return false;
        return true;
    }

    @PostMapping("worker/removePath")
    public boolean removePath(@RequestBody String path){
        try{
            path = Paths.get(path,File.separator).toAbsolutePath().toString();
        }catch (Exception e){
            log.warn(e.toString());
            return false;
        }
        if(System.getProperty("os.name").matches("[W|w]indows.*")){
            path =  path.toLowerCase();
        }
        if(new File(path).isDirectory()){
            workerProfile.getPath().remove(path);
            eventPublisher.publishEvent(new MessyDocEvent("update"));
        }else return false;
        return true;
    }

}
