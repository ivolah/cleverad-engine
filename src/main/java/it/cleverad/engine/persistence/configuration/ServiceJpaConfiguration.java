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
@EnableJpaRepositories(basePackages = "it.cleverad.engine.persistence.model.service", entityManagerFactoryRef = "serviceEntityManagerFactory", transactionManagerRef = "serviceTransactionManager")
public class ServiceJpaConfiguration {

    @Bean
    public LocalContainerEntityManagerFactoryBean serviceEntityManagerFactory(@Qualifier("serviceDataSource") DataSource dataSource, EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSource).packages("it.cleverad.engine.persistence.model.service").build();
    }

    @Bean
    public PlatformTransactionManager serviceTransactionManager(@Qualifier("serviceEntityManagerFactory") LocalContainerEntityManagerFactoryBean serviceEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(serviceEntityManagerFactory.getObject()));
    }

}
