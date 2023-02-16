package it.cleverad.engine.persistence.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "it.cleverad.engine.persistence.model.tracking", entityManagerFactoryRef = "trackingEntityManagerFactory", transactionManagerRef = "trackingTransactionManager")
public class TrackingJpaConfiguration {

    @Bean
    public LocalContainerEntityManagerFactoryBean trackingEntityManagerFactory(@Qualifier("trackingDataSource") DataSource dataSource, EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSource).packages("it.cleverad.engine.persistence.model.tracking").build();
    }

    @Bean
    public PlatformTransactionManager trackingTransactionManager(@Qualifier("trackingEntityManagerFactory") LocalContainerEntityManagerFactoryBean trackingEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(trackingEntityManagerFactory.getObject()));
    }

}
