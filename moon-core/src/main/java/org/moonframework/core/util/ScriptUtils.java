package org.moonframework.core.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by quzile on 2016/8/31.
 */
public class ScriptUtils {

    public static Object invokeDefault(String script, Object... args) {
        return invoke(script, "method", args);
    }

    public static Object invoke(String script, String name, Object... args) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            // evaluate script
            engine.eval(script);

            // javax.script.Invocable is an optional interface.
            // Check whether your script engine implements or not!
            // Note that the JavaScript engine implements Invocable interface.
            Invocable inv = (Invocable) engine;

            // invoke the global function named "hello"
            return inv.invokeFunction(name, args);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

}
