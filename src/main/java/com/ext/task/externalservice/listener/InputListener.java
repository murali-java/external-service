package com.ext.task.externalservice.listener;

import com.ext.task.externalservice.domain.OrderDto;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InputListener implements ExecutionListener {

    private static final Logger LOG = LoggerFactory.getLogger(InputListener.class);
    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        OrderDto input = new OrderDto();
        input.setId("1");
        input.setName("one");
        delegateExecution.setVariable("input", input);
        LOG.info("input is set: {}", input.toString());
    }
}
