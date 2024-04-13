package com.kuroneko.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class DatabasePropertiesListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Properties props = new Properties();
        Config config = ConfigLoader.getConfig();
        props.put("spring.datasource.username", config.getDbUser());
        props.put("spring.datasource.password", config.getDbPassword());
        environment.getPropertySources().addFirst(new PropertiesPropertySource("myProps", props));
    }
}