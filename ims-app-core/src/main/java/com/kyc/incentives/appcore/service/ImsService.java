package com.kyc.incentives.appcore.service;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.mapping.PrimaryKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyc.incentives.AppUser;
import com.kyc.incentives.AppUser_;
import com.kyc.incentives.BaseModel;
import com.kyc.incentives.BaseModel_;
import com.kyc.incentives.Setting;
import com.kyc.incentives.Setting_;
import com.kyc.incentives.enums.SettingValues;


public class ImsService {
	
	protected Logger log = LoggerFactory.getLogger(getClass()); 
	
	protected EntityManager getEntityManager(){
		return PersistenceManager.IMS_INSTANCE.getEntityManager();
	}
	
	protected EntityManager getKycEntityManager(){
		return PersistenceManager.KYC_INSTANCE.getEntityManager();
	}
	
	
	
	protected Session getKycSession() {
		EntityManager entityManager = getKycEntityManager();
		Session session = null;
		
		try {

			SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
			session = sessionFactory.openSession();

		} catch (PersistenceException e) {
			log.error("", e);
		}

		return session;
	}

	protected void closeSession(Session session) {
		if (session != null && session.isOpen()){
			session.close();
		}
	}
	
	protected void closeEntityManager(EntityManager entityManager){
		if (entityManager != null && entityManager.isOpen()) {
			entityManager.close();
		}
	}
	
	/**
	 * Fetch persisted entity instance by it {@link PrimaryKey}.
	 * 
	 * @param clazz
	 * @param pk
	 * @return persisted entity instance
	 */
	public <T> Object getByPk(Class<T> clazz, long pk){

		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root<T> root = criteriaQuery.from(clazz);

		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), pk)));

		try {
			return entityManager.createQuery(criteriaQuery).getSingleResult();
		} catch (Exception e) {
			log.error("No " + clazz.getCanonicalName() + " exists with the pk:" + pk);
		} finally {
			closeEntityManager(entityManager);
		}

		return null;
	}

	/**
	 * Fetches all persisted entity instance by class.
	 * 
	 * @param clazz
	 * @return List of all class entities
	 */
	public <T> List<T> getListByClass(Class<T> clazz){

		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root<T> root = criteriaQuery.from(clazz);

		criteriaQuery.select(root);

		try {
			return entityManager.createQuery(criteriaQuery).getResultList();
		} catch (Exception e) {
			log.error("No " + clazz.getCanonicalName() + " exists ", e);
		} finally {
			closeEntityManager(entityManager);
		}

		return null;
	}

	/**
	 * Fetches all persisted entity instance by class and orders List by {@link Property}.
	 * 
	 * @param clazz
	 * @param property
	 * @return ordered list of Class entities
	 */
	public <T> List<T> getListByClassOrderByProperty(Class<T> clazz, 
			String property){

		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root<T> root = criteriaQuery.from(clazz);

		criteriaQuery.select(root);
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get(property)));

		try {
			return entityManager.createQuery(criteriaQuery).getResultList();
		} catch (Exception e) {
			log.error("No " + clazz.getCanonicalName() + " exists or the property:" + property + " is invalid");
		} finally {
			closeEntityManager(entityManager);
		}

		return null;
	}

	/**
	 * Fetch persisted entity by {@link PrimaryKey} and load {@link JoinColumn} eagerly
	 * @param clazz
	 * @param pk
	 * @param properties
	 * @return persisted entity instance
	 */
	public <T> Object getByPkWithEagerLoading(Class<T> clazz, long pk, String ...properties){

		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root<T> root = criteriaQuery.from(clazz);

		for (String property : properties){
			root.fetch(property, JoinType.LEFT);
		}

		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.equal(root.get("id"), pk)));

		try {
			return entityManager.createQuery(criteriaQuery).getSingleResult();
		} catch (Exception e) {
			log.error("No " + clazz.getCanonicalName() + " exists with the pk:" + pk + " or property arguments don't exist for entity");
		} finally {
			closeEntityManager(entityManager);
		}

		return null;
	}

	/**
	 * Fetches all persisted entity instance by class, load specified properties eagerly.
	 *  
	 * @param clazz
	 * @param properties
	 * @return {@link List}
	 */
	public <T> List<T> getListByClassWithEagerProperties(Class<T> clazz, String ... properties){

		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root<T> root = criteriaQuery.from(clazz);

		criteriaQuery.select(root);

		for (String property : properties){
			root.fetch(property, JoinType.LEFT);
		}

		try {
			return entityManager.createQuery(criteriaQuery).getResultList();
		} catch (Exception e) {
			log.error("No " + clazz.getCanonicalName() + " exists or one of the specified properties is invalid");
		} finally {
			closeEntityManager(entityManager);
		}

		return null;
	}

	/**
	 * Execute SQL statement.
	 * 
	 * @param statement
	 */
	protected void runSqlScript(String statement){

		EntityManager entityManager = getEntityManager();
		entityManager.getTransaction().begin();
		
		Query query = entityManager.createNativeQuery(statement);
		try {
			query.executeUpdate();
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			closeEntityManager(entityManager);
		}
	}

	/**
	 * Persist entity and add entity instance to {@link EntityManager}.
	 * 
	 * @param entity
	 * @return persisted entity instance
	 */
	public <T> Object create(T entity){

		EntityManager entityManager = getEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			entityManager.persist(entity);
			entityManager.getTransaction().commit();
			return entity;
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			closeEntityManager(entityManager);
		}
	}

	public <T> Collection<T> createBulk(Collection<T> entities){

		EntityManager entityManager = getEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			for (T t : entities) {
				entityManager.persist(t);
			}
			entityManager.getTransaction().commit();
			return entities;
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			closeEntityManager(entityManager);
		}
	}

	/**
	 * Merge the state of the given entity into the current {@link PersistenceContext}.
	 * 
	 * @param entity
	 * @return the managed instance that the state was merged to
	 */
	public <T> Object update(T entity){

		EntityManager entityManager = getEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
			return entity;
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			closeEntityManager(entityManager);
		}
	}

	public <T> Collection<T> updateBulk(Collection<T> entities){

		EntityManager entityManager = getEntityManager();
		entityManager.getTransaction().begin();
		
		try {
			for (T t : entities) {
				entityManager.merge(t);
			}
			entityManager.getTransaction().commit();
			return entities;
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			closeEntityManager(entityManager);
		}
	}

	/**
	 * Remove the entity instance.
	 * 
	 * @param entity
	 */
	public <T> void delete(T entity){
		
		EntityManager entityManager = getEntityManager();
		entityManager.getTransaction().begin();

		try {
			
			entityManager.remove(entity);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			closeEntityManager(entityManager);
		}
	}
	
	
	public <T> void deleteBulk(Collection<T> entity){
		
		EntityManager entityManager = getEntityManager();
		entityManager.getTransaction().begin();

		try {
			for (T t : entity) {
				entityManager.remove(t);
			}			
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			closeEntityManager(entityManager);
		}
	}
	
	/**
	 * Remove the entity instance.
	 * 
	 * @param entity
	 */
	public <T> void mergeAndDelete(T entity){
		
		EntityManager entityManager = getEntityManager();
		entityManager.getTransaction().begin();

		try {
			
			entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {
			closeEntityManager(entityManager);
		}
	}
	
	public <T extends BaseModel> T findById(Class<T> clazz, Long id) {

		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root<T> root = criteriaQuery.from(clazz);

		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get(BaseModel_.id), id));

		return getSingleResult(entityManager, criteriaQuery);
		
	}

    public <T extends BaseModel> List<T> findAll(Class<T> clazz) {
        return getEntityManager().createQuery("SELECT m FROM " + clazz.getSimpleName() + "  m", clazz).getResultList();
    }
    
    public <T extends BaseModel> Long countAll(Class<T> clazz) {
        return getEntityManager().createQuery("SELECT count(m.id) FROM " + clazz.getSimpleName() + "  m", Long.class).getSingleResult();
    }
    
    public String getSettingValue(SettingValues settingValues){
    	Setting setting = getSetting(settingValues);
    	if(setting != null){
    		return setting.getValue();
    	}
    	return null;
    }
    
    public Setting getSetting(SettingValues settingValues){
    	
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<Setting> criteriaQuery = criteriaBuilder.createQuery(Setting.class);
		Root<Setting> root = criteriaQuery.from(Setting.class);

		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get(Setting_.name), settingValues.name()));
		
		return getSingleResult(entityManager, criteriaQuery);
    }

	/**
	 * @param entityManager
	 * @param criteriaQuery
	 * @param entityManager2
	 * @return
	 */
	protected <T> T getSingleResult(EntityManager em, CriteriaQuery<T> criteriaQuery) {
		Class<T> resultClazz = criteriaQuery.getResultType();
		try {
			return em.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException ex) {
//        	I don't understand why this warrants an exception in the JPA standard so lets just catch it quietly, return null and move on.
        	return null;
		} catch (Exception e) {
			log.error("error retrieving record of " + resultClazz.getCanonicalName() , e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	/**
	 * @param entityManager
	 * @param criteriaQuery
	 * @param entityManager2
	 * @return
	 */
	protected <T> List<T> getResultList(EntityManager em, CriteriaQuery<T> criteriaQuery) {
		Class<T> resultClazz = criteriaQuery.getResultType();
		try {
			return em.createQuery(criteriaQuery).getResultList();
		} catch (Exception e) {
			log.error("error retrieving record of " + resultClazz.getCanonicalName() , e);
			throw e;
		} finally {
			closeEntityManager(em);
		}
	}

	/**
	 * This returns the path object that can be used in an order jpa criteria query. It accepts fields with dot notation and navigates 
	 * to the deeper fields to retrieve the path
	 * 
	 * @param sortField
	 * @return
	 */
	protected Path<?> getDotFieldPath(Root<?> root, String field) {
		
		Path<?> currentPath = root;
		String currentField = field;
		
		int currentDotIndex = currentField.indexOf('.');
		
		String targetField;
		
		while(currentDotIndex != -1){
			
			targetField = currentField.substring(0, currentDotIndex);
			
			currentPath = currentPath.get(targetField);
			currentField = currentField.substring(currentDotIndex+1);
			currentDotIndex = currentField.indexOf('.');
			
		}
		
		return currentPath.get(currentField);
	}
	
	public AppUser getAppUserByOrbitaId(Long orbitaId){
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<AppUser> criteriaQuery = criteriaBuilder.createQuery(AppUser.class);
		Root<AppUser> root = criteriaQuery.from(AppUser.class);

		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get(AppUser_.orbitaId), orbitaId));
		
		return getSingleResult(entityManager, criteriaQuery);
	}

	/**
	 * @return
	 */
	public AppUser getImsSystemAdminAppUser() {
		
		AppUser admin = new AppUser();
		
		admin.setId(1L);
		
		log.warn("XXXXXXXXXXX TEST ADMIN APP USER STILL BEING RETURNED XXXXXXXX");
		
		return admin;
	}
	
	public <T extends BaseModel> List<T> getByIds(Class<T> clazz, Collection<?> ids){
		
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root<T> root = criteriaQuery.from(clazz);
		
		Predicate idsCondition = root.get(BaseModel_.id).in(ids);
		
		criteriaQuery.select(root);
		criteriaQuery.where(idsCondition); 
		
		return getResultList(entityManager, criteriaQuery);
	}

    public <T extends BaseModel> List<T> findAllActive(Class<T> clazz) {
    	
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root<T> root = criteriaQuery.from(clazz);
		
		Predicate activeCondition = criteriaBuilder.equal(root.get("active"), Boolean.TRUE);
		
		criteriaQuery.select(root);
		criteriaQuery.where(activeCondition);
		
		return getResultList(entityManager, criteriaQuery);
    }
	
}
