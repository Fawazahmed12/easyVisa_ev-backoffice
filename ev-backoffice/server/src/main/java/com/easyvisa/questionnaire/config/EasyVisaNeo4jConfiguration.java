package com.easyvisa.questionnaire.config;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

@Configuration
@EnableNeo4jRepositories(sessionFactoryRef = "getSessionFactory", transactionManagerRef = "graphTransactionManager",
        basePackages = "com.easyvisa.questionnaire.repositories", repositoryFactoryBeanClass = EasyVisaNeo4jRepositoryFactoryBean.class)
public class EasyVisaNeo4jConfiguration {

    @Value("${spring.data.neo4j.uri}")
    String neo4jURL;

    @Value("${spring.data.neo4j.username}")
    String neo4jUser;

    @Value("${spring.data.neo4j.password}")
    String neo4jPass;

    @Bean(name = "neo4jConfiguration")
    public org.neo4j.ogm.config.Configuration neo4jConfiguration() {
        return new org.neo4j.ogm.config.Configuration.Builder()
                .uri(neo4jURL)
                .credentials(neo4jUser, neo4jPass)
                .build();
//        configuration.driverConfiguration().setURI(neo4jURL)
//                .setCredentials(neo4jUser, neo4jPass);
//        return configuration;
    }

    @Bean(name = "getSessionFactory")
    public SessionFactory graphSessionFactory() {
        return new SessionFactory(neo4jConfiguration(), "com.easyvisa.questionnaire.model");
    }

    @Bean(name = "graphTransactionManager")
    public Neo4jTransactionManager graphTransactionManager(@Qualifier("getSessionFactory") SessionFactory sessionFactory) {
        return new Neo4jTransactionManager(sessionFactory);
    }

    @Bean(name = "neo4jSession")
    public org.neo4j.ogm.session.Session neo4jSession() {
        return graphTransactionManager(graphSessionFactory()).getSessionFactory().openSession();
    }
}
