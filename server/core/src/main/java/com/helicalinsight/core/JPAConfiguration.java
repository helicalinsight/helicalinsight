package com.helicalinsight.core;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Configuration
@EnableTransactionManagement
public class JPAConfiguration {
	
	private final EntityManagerFactory emf;
	
	public JPAConfiguration() {
		emf=Persistence.createEntityManagerFactory("helicalInsight-pu");
	}
	
	@Bean
	@Qualifier(value = "entityManager")
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return emf.createEntityManager();
    }
}
