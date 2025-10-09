package br.com.example.ejb;

import javax.ejb.Local;

@Local // Define que a interface é local, para acesso dentro da mesma JVM
public interface MainEJB {


    public void execute();

}