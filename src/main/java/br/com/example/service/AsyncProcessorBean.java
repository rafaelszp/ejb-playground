package br.com.example.service;

import br.com.example.ejb.MainEJB;
import br.com.example.model.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;


@Stateless
public class AsyncProcessorBean implements AsyncProcessor {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AsyncProcessorBean.class);

    @Override
    @Asynchronous
    public Future<Long> processAsync(Map<String, String> contextMap, Task task) {

        try {
            ThreadContext.getContext().putAll(contextMap);
            ThreadContext.put("taskId", task.getId());
            ThreadContext.put("taskDescription", task.getDescription());
            Random random = new SecureRandom();
            long processId = random.nextLong();
            simulateProcessing(processId,task);
            AsyncResult<Long> asyncResult = new AsyncResult<>(processId);
            return asyncResult;
        } finally {
            ThreadContext.getContext().clear();
        }
    }

    private void simulateProcessing(long processId,Task task) {
        try {
            ThreadContext.put("processId", String.valueOf(processId));
            logger.info("->Iniciando processamento: "+task.toJson());
            Thread.sleep(2000); // Simula um processamento demorado
            logger.info("==Processamento finalizado"+task.toJson());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Processing was interrupted", e);
        }
    }

}
