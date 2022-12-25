package com.zbro.messydoc.commons.feign;

import com.zbro.messydoc.worker.DogTaskRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.List;
import java.util.Map;

@FeignClient(name = "worker", configuration = ClientConfig.class)
public interface WorkerClient {

    @RequestMapping(method = RequestMethod.POST, value = "/worker/startToWork")
    String startToWork(URI uri, @RequestBody String taskName);

    @RequestMapping(method = RequestMethod.POST, value = "/worker/stopWork")
    String stopWork(URI uri, @RequestBody String taskName);

    @RequestMapping(method = RequestMethod.POST, value = "/worker/addPath")
    boolean addPath(URI uri, @RequestBody String path);

    @RequestMapping(method = RequestMethod.POST, value = "/worker/removePath")
    boolean removePath(URI uri, @RequestBody String path);

    @RequestMapping(method = RequestMethod.POST, value = "/worker/test")
    boolean test(URI uri);

    @RequestMapping(method = RequestMethod.GET, value = "/worker/dogList")
    List<String[]> dogList(URI uri);

}
