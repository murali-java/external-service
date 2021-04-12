package com.ext.task.externalservice.subscriber;

import com.ext.task.externalservice.domain.Order;
import com.ext.task.externalservice.domain.OrderDto;
import com.ext.task.externalservice.handler.OrderHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.client.task.impl.ExternalTaskImpl;
import org.camunda.bpm.client.variable.ClientValues;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.impl.value.ObjectValueImpl;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class RetrySubscriber {
    private static final Logger LOG = LoggerFactory.getLogger(RetrySubscriber.class);
    @Autowired
    private ExternalTaskClient externalTaskClient;

    @Autowired
    private OrderHandler orderHandler;

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe(){
        externalTaskClient.subscribe("retry")
                .lockDuration(6000)
                .handler((externalTask, externalTaskService) -> CompletableFuture.runAsync(() -> externalHandler(externalTask, externalTaskService))
                ).open();
        LOG.info("Order topic is subscribed");
    }

    private void externalHandler(ExternalTask externalTask, ExternalTaskService externalTaskService){
        LOG.info("============================= {}", externalTask.getId());

        ObjectValue customerDataValue = Variables.objectValue(new OrderDto())
                .serializationDataFormat(Variables.SerializationDataFormats.JAVA)
                .create();

        Map<String, Object> variables = Variables.createVariables().putValueTyped("calculated value", customerDataValue);
        externalTaskService.complete(externalTask, variables, variables);
    }
}
