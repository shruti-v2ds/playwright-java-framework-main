package framework.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "classpath:config.properties",
        "system:properties",
        "system:env"
})
public interface FrameworkConfig extends Config {

    @Key("browser")
    String browser();

    @Key("baseUrl")
    String baseUrl();

    @Key("headless")
    boolean headless();

    @Key("defaultTimeout")
    @DefaultValue("10000")
    int defaultTimeout();

    @Key("username")
    @DefaultValue("")
    String username();

    @Key("password")
    @DefaultValue("")
    String password();

    @Key("otpApiBaseUrl")
    @DefaultValue("https://app-backend-a8h9.onrender.com")
    String otpApiBaseUrl();

    @Key("testMobile")
    @DefaultValue("1234567890")
    String testMobile();
}
