package Hooks;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import framework.core.DriverFactory;

import java.util.HashMap;
import java.util.Map;

public class TestContext {

    private final Map<String, Object> scenarioData =
            new HashMap<>();

    public Page getPage() {
        return DriverFactory.getPage();
    }

    public BrowserContext getBrowserContext() {
        return DriverFactory.getContext();
    }

    public void set(String key, Object value) {
        scenarioData.put(key, value);
    }

    public Object get(String key) {
        return scenarioData.get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(scenarioData.get(key));
    }

    public boolean contains(String key) {
        return scenarioData.containsKey(key);
    }

    public void remove(String key) {
        scenarioData.remove(key);
    }

    public void clear() {
        scenarioData.clear();
    }
}