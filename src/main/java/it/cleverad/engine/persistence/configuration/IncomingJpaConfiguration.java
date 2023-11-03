//package it.cleverad.engine.persistence.configuration;
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(basePackages = "it.cleverad.engine.persistence.repository.incoming",
//        entityManagerFactoryRef = "incomingEntityManagerFactory",
//        transactionManagerRef = "incomingTransactionManager")
//public class IncomingJpaConfiguration {
//
//    @Bean("incomingDataSourceProperties")
//    @ConfigurationProperties("spring.datasource.incoming")
//    public DataSourceProperties incomingDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean("incomingDataSource")
//    public DataSource incomingDataSource(@Qualifier("incomingDataSourceProperties") DataSourceProperties dd) {
//        return dd.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//    }
//
//    @Bean("incomingEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean incomingEntityManagerFactory(@Qualifier("incomingDataSource") DataSource incomingDataSource, EntityManagerFactoryBuilder builder) {
//        Map<String, Object> objMap = new HashMap<>();
//        objMap.put("hibernate.dialect", "org.hibernate.dialect.MariaDB103Dialect");
//        objMap.put("hibernate.show_sql", false);
//        objMap.put("hibernate.hbm2ddl.auto", "create");
//        return builder.dataSource(incomingDataSource)
//                .packages("it.cleverad.engine.persistence.model.incoming")
//                .properties(objMap).persistenceUnit("incoming").build();
//    }
//
//    @Bean("incomingTransactionManager")
//    public PlatformTransactionManager incomingTransactionManager(@Qualifier("incomingEntityManagerFactory") LocalContainerEntityManagerFactoryBean incomingEntityManagerFactory) {
//        return new JpaTransactionManager(incomingEntityManagerFactory.getObject());
//    }
//
//
//}