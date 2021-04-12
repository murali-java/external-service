package com.ext.task.externalservice.config;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.backoff.ExponentialBackoffStrategy;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
public class ExternalTaskConfig {

    @Autowired
    Environment environment;

    @Bean
    public ExternalTaskClient externalTaskClient(){
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(1000)
                .lockDuration(20000)
                .defaultSerializationFormat(Variables.SerializationDataFormats.JAVA.getName())
                .maxTasks(10)
                .build();
        return client;
    }

    @Bean
    public String test(){

        List<String> list = environment.getProperty("retry.codes", List.class);
        list.contains(String.valueOf(1));
        return "test";
    }
}
