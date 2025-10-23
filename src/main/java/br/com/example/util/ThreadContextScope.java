package br.com.example.util;

import org.apache.logging.log4j.ThreadContext;

import java.util.HashMap;
import java.util.Map;

public class ThreadContextScope implements AutoCloseable {

    private  Map<String,String> baseContext;

    public ThreadContextScope(Map<String,String> contextMap) {
        baseContext = new HashMap<>(contextMap);
        ThreadContext.putAll(contextMap);
    }

    @Override
    public void close(){
        ThreadContext.clearMap();
        ThreadContext.putAll(baseContext);
    }

    public static ThreadContextScope createNew(){
        return new ThreadContextScope(ThreadContextScope.getCurrentContextMap());
    }

    public static void put(String key, String value){
        ThreadContext.put(key,value);
    }

    public static void putAll(Map<String,String> map){
        ThreadContext.putAll(map);
    }

    public static Map<String,String> getCurrentContextMap(){
        return new HashMap<>(ThreadContext.getContext());
    }


    public static void clearMap(){
        ThreadContext.clearMap();
    }
}
