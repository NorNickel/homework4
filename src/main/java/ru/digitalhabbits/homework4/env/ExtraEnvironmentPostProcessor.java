package ru.digitalhabbits.homework4.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;


public class ExtraEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String EXTRA_PROPERTIES_PATH = "config";

    private static final String EXTRA_PROPERTIES_LOCATION_PATTERN =
            String.format("classpath:%s/*.properties", EXTRA_PROPERTIES_PATH);

    private final PropertySourceLoader loader = new PropertiesPropertySourceLoader();


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        Resource[] propertyResources = getResources(EXTRA_PROPERTIES_PATH, EXTRA_PROPERTIES_LOCATION_PATTERN);

        for (Resource propertyResource : propertyResources) {
            PropertySource<?> propertySource = loadProperties(propertyResource);
            environment.getPropertySources().addLast(propertySource);
        }
    }


    private Resource[] getResources(String path, String locationPattern) {
        try {
            return new PathMatchingResourcePatternResolver().getResources(locationPattern);
        }
        catch (IOException e) {
            throw new RuntimeException("Error when loading properties from " + path, e);
        }
    }


    private PropertySource<?> loadProperties(Resource path) {
        if (!path.exists()) {
            throw new IllegalArgumentException("Resource " + path + " does not exist");
        }
        try {
            return this.loader.load(path.getFilename(), path).get(0);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to load properties configuration from " + path, ex);
        }
    }

}
