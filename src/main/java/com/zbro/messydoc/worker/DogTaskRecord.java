package com.zbro.messydoc.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DogTaskRecord{
    String dogName;
    String path;
    ScheduledFuture<?> task;
}
