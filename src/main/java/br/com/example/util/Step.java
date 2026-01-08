package br.com.example.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Step {
    private final String name;
    private final long startTime;
    private long elapsedTime;
    private final List<Step> children = new ArrayList<>();

    private Step(String name) {
        this.name = name;
        this.startTime = System.nanoTime();
    }

    public static Step create(String name) {
        return new Step(name);
    }

    public void stop() {
        if (elapsedTime == 0) {
            this.elapsedTime = System.nanoTime() - startTime;
        }
    }

    public void addChild(Step child) {
        this.children.add(child);
    }

    public String getReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(" - Execution Summary: ");
        printNode(sb, 0);
        sb.append("}");
        return sb.toString();
    }

    private void printNode(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) sb.append("  ");

        sb.append("-> ").append(name)
                .append(": ").append(formatDuration(elapsedTime));

        for (Step child : children) {
            child.printNode(sb, level + 1);
        }
    }

    private String formatDuration(long nanos) {
        long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
        if(millis >= 1000) {
            return String.format("%2.3fs", ((double)millis)/1000.0);
        }
        return (millis == 0 ? nanos + "ns" : millis + "ms");
    }
}