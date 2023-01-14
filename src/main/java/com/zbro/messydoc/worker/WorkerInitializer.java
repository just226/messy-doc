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

        boolean hasAdvertisedListener = true;
        boolean hasMasterHost = true;
        String dogName;
        if(args.getOptionValues("workerName") != null){
            dogName = args.getOptionValues("workerName").get(0);
        }else {
            dogName = InetAddress.getLocalHost().getHostName();
            log.info("no workerName found, use default hostname {}",dogName);
            log.info("you can name this worker with --workerName=xxx");
        }
        String advertisedListener = "localhost";
        if(args.getOptionValues("mode").get(0).equals("unified")){
            advertisedListener = "localhost";
        }else if(args.getOptionValues("advertisedListener") != null){
            advertisedListener = args.getOptionValues("advertisedListener").get(0);
        }else {
            log.error("you must specify the advertisedListener with --advertisedListener=x.x.x.x");
            log.error("note that the listener is used by master, it must be a master reachable address or domain name");
            hasAdvertisedListener = false;
        }
        if(args.getOptionValues("masterHost") == null){
            log.error("you must specify the masterHost with --masterHost=http://x.x.x.x:13399");
            hasMasterHost = false;
        }
        if(!hasAdvertisedListener || !hasMasterHost){
            System.exit(-1);
        }

        workerProfile.setId(UUID.randomUUID().toString());
        workerProfile.setName(dogName);
//        InetAddress address = InetAddress.getLocalHost();
        workerProfile.setAdvertisedListener(advertisedListener);
        workerProfile.setStatus("idle");
        workerProfile.setTtl(10);
        workerProfile.setPaths(new HashSet<>());
        eventPublisher.publishEvent(new MessyDocEvent("regist"));


    }
}
