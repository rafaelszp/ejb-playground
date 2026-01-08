package br.com.example.service;

import br.com.example.util.Tracer;

import javax.ejb.Stateless;
import java.util.concurrent.ThreadLocalRandom;

@Stateless
public class UtilitaryService {

    public void execute(){
        try(AutoCloseable t = Tracer.measure()) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Thread.sleep(random.nextInt(500));
        } catch (Exception e) {
        }
    }
}
