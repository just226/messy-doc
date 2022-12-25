package com.zbro.messydoc.master.controller;

import com.zbro.messydoc.master.WorkerEntity;
import com.zbro.messydoc.commons.feign.WorkerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class MasterController {

    @Autowired
    Map<String, WorkerEntity> workersMap;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    WorkerClient workerClient;

    @GetMapping("workerList")
    public String startWorker(Model model){
        model.addAttribute("workerList",new ArrayList<>(workersMap.values()));
        return "workerList";
    }

    @GetMapping("dogList")
    public String dogList(@RequestParam("id") String id, Model model){

        String url = "http://" + workersMap.get(id).getAdvertisedListener() + ":13399";
        URI uri = URI.create(url);
        List<String[]> list = workerClient.dogList(uri);

        model.addAttribute("taskList",list);
        model.addAttribute("id",id);

        return "dogList";
    }



}
