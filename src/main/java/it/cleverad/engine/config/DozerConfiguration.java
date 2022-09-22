package it.cleverad.engine.config;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DozerConfiguration {

    @Bean
    public Mapper beanMapper() {
        return DozerBeanMapperBuilder.create().build();
    }

}