package br.com.example.ejb;

import br.com.example.model.Task;
import br.com.example.service.AsyncProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless // Define este componente como um EJB sem estado
public class MainEJBService implements MainEJB {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MainEJB.class);

    @EJB
    AsyncProcessor processor;

    @Override
    public void execute() {

        try {
            Map<String, String> baseContext = ThreadContext.getContext();
            baseContext.put("_modulename","ejb-playground");

            List<Future<Long>> listOfFuture = new ArrayList<Future<Long>>();
            List<Task> tasks = getTasks();
            for (Task task : tasks) {
                Map<String, String> contextMap = new HashMap<>(baseContext); //executando copia defensiva do mapa
                contextMap.put("taskId", task.getId());
                contextMap.put("taskDescription", task.getDescription());
                Future<Long> future = processor.processAsync(contextMap, task);
                listOfFuture.add(future);
            }
//            for (Future<Long> future : listOfFuture) {
//                logger.info(String.format("Get future: %s", future.get()));
//
//            }
            CompletableFuture[] futures = listOfFuture.stream()
                    .map(future -> CompletableFuture.supplyAsync(() -> {
                        try {
                            Long result = future.get(30, TimeUnit.SECONDS); //NBão funciona no Glassfish
                            logger.info(String.format("Get future: %s", result));
                            return result;
                        } catch (Exception e) {
                            logger.error("Error getting future result", e);
                            return null;
                        }
                    }))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(futures).join();
            ThreadContext.putAll(baseContext); //restaurando contexto base da thread que foi limpo no processor
            logger.info(String.format("All futures completed: %d", tasks.size()));

        } catch (Exception e) {
            logger.error("Deu pane", e);
        } finally {
            ThreadContext.clearMap();
        }

    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Task task = new Task();
            task.setId(i + "");
            task.setDescription("Task " + i);
            tasks.add(task);
        }
        return tasks;
    }
}