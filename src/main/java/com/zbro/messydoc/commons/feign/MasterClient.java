package com.zbro.messydoc.commons.feign;

import com.zbro.messydoc.worker.WorkerEntitySingleton;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "master", url = "${masterHost}",configuration = ClientConfig.class)
public interface MasterClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/v1/worker")
    boolean post(@RequestBody WorkerEntitySingleton body);

//    keepalive
    @RequestMapping(method = RequestMethod.PUT, value = "/api/v1/worker/{workerId}")
    boolean put(@PathVariable("workerId") String workerId, @RequestBody WorkerEntitySingleton body);

}
