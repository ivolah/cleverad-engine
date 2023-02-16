package it.cleverad.engine.persistence.configuration;


import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ServiceDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.service")
    public DataSourceProperties serviceDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource serviceDataSource() {
        return serviceDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }
}
