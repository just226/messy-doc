package com.zbro.messydoc.worker;

import com.zbro.messydoc.commons.event.MessyDocEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.UUID;

@Component
@Slf4j
public class WorkerInitializer implements ApplicationRunner {

    @Autowired
    WorkerEntitySingleton workerProfile;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String dogName;
        if(args.getOptionValues("dogname")!= null){
            dogName = args.getOptionValues("dogname").get(0);
        }else {
            dogName = InetAddress.getLocalHost().getHostName();
            log.info("no dogname found, use default hostname {}",dogName);
            log.info("you can specify the worker name with --dogname=xxx");
        }
        String advertisedlistener = "localhost";
        if(args.getOptionValues("mode").get(0).equals("unified")){
            advertisedlistener = "localhost";
        }else if(args.getOptionValues("advertisedlistener") != null){
            advertisedlistener = args.getOptionValues("advertisedlistener").get(0);
        }else {
            log.error("you must specify the advertisedlistener with --advertisedlistener=xxx");
            log.error("note that the listener is used by master, it must be a master reachable address or domain name");
            System.exit(-1);
        }

        workerProfile.setId(UUID.randomUUID().toString());
        workerProfile.setName(dogName);
//        InetAddress address = InetAddress.getLocalHost();
        workerProfile.setAdvertisedListener(advertisedlistener);
        workerProfile.setStatus("idle");
        workerProfile.setTtl(10);
        workerProfile.setPath(new HashSet<>());
        eventPublisher.publishEvent(new MessyDocEvent("regist"));


    }
}
