package com.zbro.messydoc.commons.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.Transport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.configuration.SSLContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

import javax.net.ssl.SSLContext;
import java.io.File;


@Configuration
public class EsConfig {
    @Value(value = "${elastic.host}")
    private String esHost;


    @Bean
    RestClient restClient() throws Exception{

        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials("elastic", "abcd1234")
        );

            File certFile = new File("ca.crt");

            SSLContext sslContext = TransportUtils
                    .sslContextFromHttpCaCrt(certFile);

//        String fingerPrint = "18:0C:72:1C:E7:E2:CB:2F:7A:7F:F1:F1:05:FA:C7:FA:28:C5:16:CE:E8:5A:4F:53:3E:BB:58:D7:D4:11:B2:6F";//
//        SSLContext sslContext = TransportUtils
//                .sslContextFromCaFingerprint(fingerPrint);

        return RestClient.builder(
                new HttpHost(esHost, 9200,"https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(credsProv)
                )
                .setRequestConfigCallback(builder
                        -> builder.setConnectionRequestTimeout(10 * 60 * 1000).setSocketTimeout(10 * 60 * 1000))
                .build();
    }
    @Bean
    ElasticsearchTransport transport() throws Exception{
        return new RestClientTransport(
                restClient(), new JacksonJsonpMapper());
    }

    @Bean
    ElasticsearchClient elasticsearchClient() throws Exception{
        return new ElasticsearchClient(transport());
    }

    @Bean
    ElasticsearchTemplate elasticsearchTemplate() throws Exception{
        return new  ElasticsearchTemplate(elasticsearchClient(), new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext()) );
    }



}
