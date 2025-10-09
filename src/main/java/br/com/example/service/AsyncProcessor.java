package br.com.example.service;

import br.com.example.model.Task;

import java.util.Map;
import java.util.concurrent.Future;

public interface AsyncProcessor {


    public Future<Long> processAsync(Map<String,String> contextMap, Task task);

}
