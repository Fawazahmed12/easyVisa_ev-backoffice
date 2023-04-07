package com.easyvisa.questionnaire.config;

import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.neo4j.repository.support.Neo4jRepositoryFactoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

public class EasyVisaNeo4jRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends Neo4jRepositoryFactoryBean {
    public EasyVisaNeo4jRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Autowired
    @Qualifier("neo4jSession")
    public void setSession(Session session) {
        super.setSession(session);
    }

}
