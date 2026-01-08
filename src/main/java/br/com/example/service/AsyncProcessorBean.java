package br.com.example.service;

import br.com.example.model.Task;
import br.com.example.util.Tracer;
import br.com.example.util.ThreadContextScope;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import org.apache.logging.log4j.LogManager;

import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;


@Stateless
public class AsyncProcessorBean implements AsyncProcessor {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AsyncProcessorBean.class);

    @EJB
    UtilitaryService utilitaryService;

    private LongCounter tasksRunCounter;

    @PostConstruct
    private void init(){
        Meter meter = GlobalOpenTelemetry.getMeter("ejb-playground");
        this.tasksRunCounter = meter.counterBuilder("tasks_run_total")
                .setDescription("Tasks run count")
                .setUnit("1") // '1' significa que é uma contagem
                .build();
    }

    @Override
    @Asynchronous
    public Future<Long> processAsync(Map<String, String> contextMap, Task task){
        Tracer.start();
        try(ThreadContextScope contextScope = new ThreadContextScope(contextMap)) {

            ThreadLocalRandom random = ThreadLocalRandom.current();
            long processId = random.nextLong(0,10000);
            simulateProcessing(processId,task);
            AsyncResult<Long> asyncResult = new AsyncResult<>(processId);
            return asyncResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            Tracer.stop();
            logger.info("AsyncProcessorBean::processAsync"+ Tracer.getSummary());
        }
    }

    private void simulateProcessing(long processId,Task task) {
        try(AutoCloseable t = Tracer.measure()){
            ThreadContextScope.put("processId", String.valueOf(processId));
            logger.info("->Iniciando processamento: "+task.toJson());
            Thread.sleep(ThreadLocalRandom.current().nextLong(500)); // Simula um processamento demorado
            logger.info("==Processamento finalizado"+task.toJson());
            utilitaryService.execute();;
            tasksRunCounter.add(1);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            logger.error("Processing was interrupted", e);
        }
    }

}
