package com.zbro.messydoc.master;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class WorkerListConfig {

    @Bean
    @Scope("singleton")
    Map<String, WorkerEntity> workerCollection(){
        return  new LinkedHashMap<>();
    }

}
