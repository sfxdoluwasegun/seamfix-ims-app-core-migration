package com.kyc.incentives.appcore.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum PersistenceManager {
	
	KYC_INSTANCE("kycModel_PU"),
	IMS_INSTANCE("ims-jpa"),
	;

	private EntityManagerFactory emFactory;
	private String persistenceUnitName;

	private PersistenceManager(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;

		Logger log = LoggerFactory.getLogger(getClass()); 
		try{
			emFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
		}catch(Throwable t){
			log.error(t.getMessage(), t);
		}
	}

	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}

	public EntityManager getEntityManager() {
		return emFactory.createEntityManager();
	}

}