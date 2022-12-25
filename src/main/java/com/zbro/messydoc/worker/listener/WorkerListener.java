package com.zbro.messydoc.worker.listener;

import com.zbro.messydoc.worker.WorkerEntitySingleton;
import com.zbro.messydoc.commons.event.MessyDocEvent;
import com.zbro.messydoc.commons.feign.MasterClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
@Slf4j
public class WorkerListener {

    @Autowired
    MasterClient masterClient;

    @Autowired
    WorkerEntitySingleton workerProfile;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    //ms
    private final long keepAliveInterval = 10000;

    @Async
    @EventListener(condition = "#event.eventName eq 'regist'")
    public void registToMaster(MessyDocEvent event) throws Exception{
        boolean isRegisted = false;
        int tTL = 5;
        while (!isRegisted){
            if(tTL == 0){
                eventPublisher.publishEvent(new MessyDocEvent("shutdown"));
                log.info("shutdown Worker");
            }else {
                try{
                    if(tTL==5){
                        log.info("try to sign up");
                    }else {
                        log.info("retry sign up left {} times",tTL);
                    }
                    if(!(isRegisted =  masterClient.post(workerProfile))){
                        tTL--;
                    };
                }catch (Exception e){
                    log.error(e.toString());
                    tTL--;
                }
            }
            Thread.sleep(1000);
        }
        log.info("Sign up to Master success");
        eventPublisher.publishEvent(new MessyDocEvent("keepalive"));
    }

    @Async
    @EventListener(condition = "#event.eventName eq 'keepalive'")
    public void keepaliveToMaster(MessyDocEvent event) throws Exception{
        int tTL = 5;
        while (true){
            try{
                if(!masterClient.put(workerProfile.getId(), workerProfile)){
                    throw new Exception("Master reject keepalive");
                }
                log.debug("worker {} is alive", workerProfile.getName());
                tTL = 5;
            }catch (Exception e){
                log.error(e.toString());
                tTL --;
                if(tTL == 0){
                    eventPublisher.publishEvent(new MessyDocEvent("regist"));
                    log.warn("lost Master");
                    break;
                }
            }
            Thread.sleep(keepAliveInterval);
        }
    }

    @Async
    @EventListener(condition = "#event.eventName eq 'update'")
    public void updateToMaster(MessyDocEvent event) throws Exception{
            try{
                if(!masterClient.put(workerProfile.getId(), workerProfile)){
                    throw new Exception("Master reject update");
                }
                log.debug("worker {} is alive", workerProfile.getName());
            }catch (Exception e){
                log.error(e.toString());
                    log.warn("update to master fail");
                }
    }

    @Async
    @EventListener(condition = "#event.eventName eq 'shutdown'")
    public void shutdown(MessyDocEvent event) throws Exception{
//        System.exit(200);
        Runtime.getRuntime().halt(-1);
    }


}
