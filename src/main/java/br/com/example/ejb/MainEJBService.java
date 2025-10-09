package br.com.example.ejb;

import br.com.example.model.Task;
import br.com.example.service.AsyncProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Stateless // Define este componente como um EJB sem estado
public class MainEJBService implements MainEJB {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MainEJB.class);

    @EJB
    AsyncProcessor processor;

    @Override
    public void execute() {

        try {
            Map<String, String> contextMap = ThreadContext.getContext();

            List<Future<Long>> listOfFuture = new ArrayList<Future<Long>>();
            List<Task> tasks = getTasks();
            for (Task task : tasks) {
                Future<Long> future = processor.processAsync(contextMap, task);
                listOfFuture.add(future);
            }
            for (Future<Long> future : listOfFuture) {
                future.get();
            }
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