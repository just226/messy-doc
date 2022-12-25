package com.zbro.messydoc.master.listener;

import com.zbro.messydoc.master.WorkerEntity;
import com.zbro.messydoc.commons.event.MessyDocEvent;
import com.zbro.messydoc.commons.feign.WorkerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableAsync
@Slf4j
public class MasterListener {

    @Autowired
    WorkerClient workerClient;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    Map<String, WorkerEntity> workerEntityMap;

    private boolean enableHealthCheck = false;

    private final long healthCheckInterval = 10000;


    @Async
    @EventListener(condition = "#event.eventName eq 'healthCheck'")
    public void healthCheck(MessyDocEvent event) throws Exception{
        if(enableHealthCheck){
           log.info("health check is running");
        }else {
            enableHealthCheck = true;
            while (true){
                try{
                    if(workerEntityMap.isEmpty()) {
                        enableHealthCheck = false;
                        log.info("process health stopped");
                        eventPublisher.publishEvent(new MessyDocEvent("wait"));
                        break;
                    }
                    log.debug("process health check for {} worker(s)",workerEntityMap.size());
                    for(WorkerEntity worker:workerEntityMap.values()){
                        if(worker.getTtl() == 0){
                            workerEntityMap.remove(worker.getId());
                            log.info("remove worker {}",worker.getId());
                            break;
                        }else {
                            worker.setTtl(worker.getTtl()-1);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                Thread.sleep(healthCheckInterval);
            }
        }
    }

    @Async
    @EventListener(condition = "#event.eventName eq 'wait'")
    public void wait(MessyDocEvent event) throws Exception{
       log.info("wait for worker");
    }

    @Async
    @EventListener(condition = "#event.eventName eq 'shutdown'")
    public void shutdown(MessyDocEvent event) throws Exception{
//        System.exit(200);
        Runtime.getRuntime().halt(200);
    }


}
