package br.com.example.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class ExecutionTimer {

    private static final ThreadLocal<Deque<Stopwatch>> STACK = ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<Stopwatch> ROOT = new ThreadLocal<>();

    // --- ABORDAGEM A: Try-with-resources ---
    // Com nome explícito
    /**Exemplo:
    * <pre>
    *  try(AutoCloseable timer = ExecutionTimer.measure()) {
            List<Task> tasks = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Task task = new Task();
                task.setId(i + "");
                task.setDescription("Task " + i);
                tasks.add(task);
            }
            return tasks;
        }
        </pre>
    * */
    public static AutoCloseable measure(String name) {
        start(name);
        return ExecutionTimer::stop; // Chama stop() ao fechar o bloco
    }

    // Com nome automático (pega o nome do método)
    public static AutoCloseable measure() {
        start(getCallerMethodName());
        return ExecutionTimer::stop;
    }

    // --- ABORDAGEM B: Manual Start/Stop ---
    // Inicia um nó filho (ou raiz) com nome automático

    /**
     * Exemplo:
     * <pre>
     ExecutionTimer.start();
     ...
     ExecutionTimer.stop();
     </pre>
     * */
    public static void start() {
        start(getCallerMethodName());
    }

    // Inicia com nome explícito
    public static void start(String name) {
        Stopwatch node = Stopwatch.create(name);
        Deque<Stopwatch> stack = STACK.get();

        if (stack.isEmpty()) {
            // Se a pilha está vazia, este é o novo Pai de Todos
            // CUIDADO: Se esquecer de dar getSummary() antes, sobrescreve o anterior.
            ROOT.set(node);
        } else {
            // Se já tem gente rodando, vira filho do atual
            stack.peek().addChild(node);
        }

        stack.push(node);
    }

    // Para o cronômetro atual (topo da pilha)
    public static void stop() {
        Deque<Stopwatch> stack = STACK.get();
        if (!stack.isEmpty()) {
            Stopwatch node = stack.pop();
            node.stop();

            // Não limpamos o ROOT aqui. Deixamos ele vivo para o getSummary().
        }
    }

    public static String getSummary() {
        Stopwatch root = ROOT.get();

        // Garante a limpeza para evitar Memory Leak no servidor
        ROOT.remove();
        STACK.remove();

        if (root != null) {
            return root.getReport();
        }
        return "Nenhum timer registrado nesta thread.";
    }

    private static String getCallerMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            StackTraceElement caller = stackTrace[3];
            String className = caller.getClassName();
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
            return simpleClassName + "#" + caller.getMethodName();
        }
        return "unknown";
    }
}