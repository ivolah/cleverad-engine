package it.cleverad.engine.persistence.configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TrackingDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource.tracking")
    public DataSourceProperties trackingDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource trackingDataSource() {
        return trackingDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }
}
