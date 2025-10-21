package br.com.example.util;

import java.nio.file.Paths;

public class Config {
    public static final String TRUSTSTORE_FILE = "cacerts.jks";

    public static String getTrustStorePath() {
        return Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts").toString();
    }
}
