package com.zbro.messydoc.worker;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Setter
@Component
@Scope("singleton")
public class WorkerEntitySingleton {
    private String id;
    private String name;
    private String advertisedListener;
    private Set<String> paths;
    private String status;
    private int ttl;

}
