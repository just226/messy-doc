package com.zbro.messydoc.commons.feign;

import feign.okhttp.OkHttpClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class ClientConfig {
        @Bean
        public OkHttpClient client() {
            return new OkHttpClient();
    }

}
