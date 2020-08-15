package com.doopp.agar.server.configuration;

import com.doopp.agar.util.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;

@Configuration
@Import({
        RedisConfiguration.class
})
// @EnableAspectJAutoProxy(exposeProxy=true)
@ComponentScan(
    basePackages = {"com.doopp.agar"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class})
    }
)
public class ApplicationConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        final PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new FileSystemResource(System.getProperty("applicationPropertiesConfig")));
        return configurer;
    }

    @Bean
    public static IdWorker idWorker(@Value("${agar-server.idWorker.workerId}") Long workerId, @Value("${agar-server.idWorker.dataCenterId}") Long dataCenterId) {
        return new IdWorker(workerId, dataCenterId);
    }
}
