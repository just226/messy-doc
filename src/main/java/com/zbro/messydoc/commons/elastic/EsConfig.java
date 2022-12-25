package com.zbro.messydoc.commons.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

@Configuration
public class EsConfig {
    @Value(value = "${elastic.host}")
    private String esHost;

    @Bean
    RestClient restClient(){
        return RestClient.builder(
                new HttpHost(esHost, 9200))
                .setRequestConfigCallback(builder
                        -> builder.setConnectionRequestTimeout(10 * 60 * 1000).setSocketTimeout(10 * 60 * 1000))
                .build();
    }
    @Bean
    ElasticsearchTransport transport() {
        return new RestClientTransport(
                restClient(), new JacksonJsonpMapper());
    }

    @Bean
    ElasticsearchClient elasticsearchClient(){
        return new ElasticsearchClient(transport());
    }

    @Bean
    ElasticsearchTemplate elasticsearchTemplate(){
        return new  ElasticsearchTemplate(elasticsearchClient(), new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext()) );
    }



}
