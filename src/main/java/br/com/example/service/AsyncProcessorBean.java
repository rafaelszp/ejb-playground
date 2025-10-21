package br.com.example.service;

import br.com.example.model.Task;
import br.com.example.util.ThreadContextScope;
import org.apache.logging.log4j.LogManager;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;


@Stateless
public class AsyncProcessorBean implements AsyncProcessor {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AsyncProcessorBean.class);

    @Override
    @Asynchronous
    public Future<Long> processAsync(Map<String, String> contextMap, Task task) {

        try(ThreadContextScope contextScope = new ThreadContextScope(contextMap)) {

            ThreadLocalRandom random = ThreadLocalRandom.current();
            long processId = random.nextLong(0,10000);
            simulateProcessing(processId,task);
            AsyncResult<Long> asyncResult = new AsyncResult<>(processId);
            return asyncResult;
        }
    }

    private void simulateProcessing(long processId,Task task) {
        try {
            ThreadContextScope.put("processId", String.valueOf(processId));
            logger.info("->Iniciando processamento: "+task.toJson());
            Thread.sleep(ThreadLocalRandom.current().nextLong(500)); // Simula um processamento demorado
            logger.info("==Processamento finalizado"+task.toJson());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Processing was interrupted", e);
        }
    }

}
