package it.cleverad.engine.persistence.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "it.cleverad.engine.persistence.repository.service",
        entityManagerFactoryRef = "serviceEntityManagerFactory",
        transactionManagerRef = "serviceTransactionManager"
)
public class ServiceJpaConfiguration {

    @Bean("serviceDataSourceProperties")
    @ConfigurationProperties("spring.datasource.service")
    public DataSourceProperties serviceDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean("serviceDataSource")
    //@ConfigurationProperties("spring.datasource.service.configuration")
    public DataSource serviceDataSource(@Qualifier("serviceDataSourceProperties") DataSourceProperties dd) {
        return dd.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }


    @Primary
    @Bean("serviceEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean serviceEntityManagerFactory(@Qualifier("serviceDataSource") DataSource serviceDataSource,
                                                                              EntityManagerFactoryBuilder builder) {
        return builder.dataSource(serviceDataSource).packages("it.cleverad.engine.persistence.model.service").build();
    }

    @Primary
    @Bean("serviceTransactionManager")
    public PlatformTransactionManager serviceTransactionManager(@Qualifier("serviceEntityManagerFactory") LocalContainerEntityManagerFactoryBean serviceEntityManagerFactory) {
        return new JpaTransactionManager(serviceEntityManagerFactory.getObject());
    }


    @Bean
    public ImplicitNamingStrategy implicit() {
        return new ImplicitNamingStrategyLegacyJpaImpl();
    }

}
