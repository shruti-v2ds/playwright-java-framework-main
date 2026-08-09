package framework.config;

import org.aeonbits.owner.ConfigFactory;

public class ConfigManager {
    private static final FrameworkConfig config =
            ConfigFactory.create(FrameworkConfig.class, System.getProperties());

    public static FrameworkConfig getConfig() {
        return config;
    }
}
