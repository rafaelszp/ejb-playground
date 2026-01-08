package br.com.example.service;

import br.com.example.util.ExecutionTimer;

import javax.ejb.Stateless;
import java.util.concurrent.ThreadLocalRandom;

@Stateless
public class UtilitaryService {

    public void execute(){
        try(AutoCloseable t = ExecutionTimer.measure()) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Thread.sleep(random.nextInt(500));
        } catch (Exception e) {
        }
    }
}
