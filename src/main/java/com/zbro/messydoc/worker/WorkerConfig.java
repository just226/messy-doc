package com.zbro.messydoc.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@EnableScheduling
@Configuration
public class WorkerConfig {

    @Bean
    @Scope("singleton")
    public Map<String, DogTaskRecord> dogTaskHolder(){
        return new LinkedHashMap<>();
    }

}
