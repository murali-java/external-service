package com.ext.task.externalservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OrderHandler.class);

    public void handleOrderTask(String id){
        LOG.info("ID: {} Order handler started", id);
        for(int i=0; i< 5; i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOG.info("ID: {} {}) Executing Order", id, i);

        }
        LOG.info("ID: {}Order handler completed", id);
        throw new RuntimeException();
    }
}
