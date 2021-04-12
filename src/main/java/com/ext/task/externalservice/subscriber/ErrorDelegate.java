package com.ext.task.externalservice.subscriber;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ErrorDelegate implements JavaDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorDelegate.class);
    public void execute(DelegateExecution execution){
        LOG.error("====== in error =====");
    }
}
