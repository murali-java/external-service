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
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.bpm.engine.variable.value.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class OrderTopicSubscriber {
    private static final Logger LOG = LoggerFactory.getLogger(OrderTopicSubscriber.class);
    @Autowired
    private ExternalTaskClient externalTaskClient;

    @Autowired
    private OrderHandler orderHandler;

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe(){
        externalTaskClient.subscribe("order")
                .lockDuration(6000)
                .handler((externalTask, externalTaskService) -> CompletableFuture.runAsync(() -> externalHandler(externalTask, externalTaskService))
                ).open();
        LOG.info("Order topic is subscribed");
    }

    private void externalHandler(ExternalTask externalTask, ExternalTaskService externalTaskService){
        LOG.info("=============================");
        try{
            String id = externalTask.getId();
            LOG.info("ID: {} === retries: {}, expiration: {}, msg: {}, priority: {}", id, externalTask.getRetries(), externalTask.getLockExpirationTime()
            , externalTask.getErrorMessage(), externalTask.getPriority());
            OrderDto dto = externalTask.getVariable("input");


            ObjectValue tval = externalTask.getVariableTyped("input");

            LOG.info("ID: {} === Got input from engine: {}", dto.toString(), id);
            orderHandler.handleOrderTask(id);

            Order order = new Order();

            order.setId("2");
            order.setName("two");
            String value = new ObjectMapper().writeValueAsString(order);
            VariableMap variables = Variables.createVariables().putValue("output", ClientValues.jsonValue(value));
            Map<String, Object> map = new HashMap<>();
            //map.put("output", value);
            externalTaskService.complete(externalTask, map);
            LOG.info("ID: {} === Completed", id);
        }catch(Exception ex){
            //ex.printStackTrace();
            if(externalTask.getRetries() == null){
                ((ExternalTaskImpl)externalTask).setRetries(3);
                externalTaskService.handleFailure(externalTask, externalTask.getId(), "Error", externalTask.getRetries(), 6000L);
            }else if(externalTask.getRetries() == 0){
                externalTaskService.handleBpmnError(externalTask, "THROWS");
            }else{
                ((ExternalTaskImpl)externalTask).setRetries(externalTask.getRetries() - 1);
                externalTaskService.handleFailure(externalTask, externalTask.getId(), "Error", externalTask.getRetries(), 6000L);
            }
        }
    }
}
