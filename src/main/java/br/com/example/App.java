package br.com.example;

import br.com.example.ejb.MainEJB;
import br.com.example.util.Config;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {

  public static void main(String[] args) throws Exception {


    System.out.println("Verificando existencia do caminho: "+Config.getTrustStorePath());
    boolean exists = Files.exists(Paths.get(Config.getTrustStorePath()));
    if(!exists){
      throw new FileNotFoundException("Arquivo de truststore não encontrado no caminho: "+Config.getTrustStorePath());
    }

    System.out.println("Iniciando a aplicação com TomEE Embedded...");

    try (final EJBContainer container = EJBContainer.createEJBContainer()) {

      System.out.println("Container EJB iniciado com sucesso.");
      final Context context = container.getContext();

      final String jndiName = "java:global/ejb-playground/MainEJBService!br.com.example.ejb.MainEJB";
      System.out.println("Procurando EJB com o nome JNDI: " + jndiName);

      MainEJB tester = (MainEJB) context.lookup(jndiName);
      tester.execute();


      System.out.println("\n-----------------------------------------------------");
      System.out.println("Resultado da chamada ao EJB: ");
      System.out.println("-----------------------------------------------------\n\n\n");

//      printIntegrationBatches(integrationBatches);


      System.out.println("-----------------------------------------------------\n\n\n");
    } catch (Exception e) {
      System.err.println("Ocorreu um erro ao executar a aplicação.");
      e.printStackTrace();
    }

    System.out.println("Aplicação finalizada.");

  }




}
