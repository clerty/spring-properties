package ru.digitalhabbits.homework4;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

public class PropertiesInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String CLASSPATH_PROPERTIES_LOCATION_PATTERN = "classpath:config/*.properties";
    private static final String EXTERNAL_PROPERTIES_LOCATION_PATTERN = "file:./config/*.properties";
    private static final Logger logger = getLogger(PropertiesInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

        try {
            final Resource[] classpathResources = resourcePatternResolver.getResources(CLASSPATH_PROPERTIES_LOCATION_PATTERN);
            final Resource[] externalResources = resourcePatternResolver.getResources(EXTERNAL_PROPERTIES_LOCATION_PATTERN);
            final Resource[] resources =  Stream.of(classpathResources, externalResources)
                    .flatMap(Stream::of)
                    .sorted(Comparator.comparing(Resource::getFilename, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toArray(Resource[]::new);
            for (Resource resource : resources) {
                propertySources.addLast(new ResourcePropertySource(resource));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
