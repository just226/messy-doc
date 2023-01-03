package com.zbro.messydoc.master;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class WorkerEntity {
    private String id;
    private String name;
    private String advertisedListener;
    private Set<String> paths;
    private String status;
    private int ttl;

}
