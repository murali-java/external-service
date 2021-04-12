package com.ext.task.externalservice.listener;

import com.ext.task.externalservice.domain.Order;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OutputListener implements ExecutionListener {
    private static final Logger LOG = LoggerFactory.getLogger(OutputListener.class);
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
       /* Order order = (Order) delegateExecution.getVariable("output");
        LOG.info("output is : {}", order.toString());*/

        String order = (String) delegateExecution.getVariable("output");
        LOG.info("output is : {}", order);
    }
}
