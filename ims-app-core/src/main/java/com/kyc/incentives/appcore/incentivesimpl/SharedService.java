/**
 * 
 */
package com.kyc.incentives.appcore.incentivesimpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.kyc.incentives.ImsRole;
import com.kyc.incentives.ImsRole_;
import com.kyc.incentives.appcore.service.ImsService;
import com.sf.biocapture.entity.KycDealer;
import com.sf.biocapture.entity.NodeAssignment;

/**
 * @author dawuzi
 *
 */
public class SharedService extends ImsService {

	/**
	 * static Singleton instance
	 */
	private static SharedService instance;

	/**
	 * Private constructor for singleton
	 */
	private SharedService() {
	}

	/**
	 * Static getter method for retrieving the singleton instance
	 */
	public static SharedService getInstance() {
		if (instance == null) {
			instance = new SharedService();
		}
		return instance;
	}
	
	/**
	 * @param role
	 * @return
	 */
	public ImsRole getRoleByName(String name) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<ImsRole> criteriaQuery = criteriaBuilder.createQuery(ImsRole.class);
		Root<ImsRole> root = criteriaQuery.from(ImsRole.class);
		
		Predicate nameCondition = criteriaBuilder.equal(root.get(ImsRole_.name), name);
		
		criteriaQuery.select(root);
		criteriaQuery.where(nameCondition);
		
		return getSingleResult(entityManager, criteriaQuery);
	}
	
	/**
	 * @param name
	 * @param code
	 * @return
	 */
	public ImsRole getRoleByNameAndCode(String name, String code) {
		EntityManager entityManager = getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<ImsRole> criteriaQuery = criteriaBuilder.createQuery(ImsRole.class);
		Root<ImsRole> root = criteriaQuery.from(ImsRole.class);
		
		Predicate nameCondition = criteriaBuilder.equal(root.get(ImsRole_.name), name);
		Predicate codeCondition = criteriaBuilder.equal(root.get(ImsRole_.code), code);
		
		criteriaQuery.select(root);
		criteriaQuery.where(nameCondition, codeCondition);
		
		return getSingleResult(entityManager, criteriaQuery);
	}
	
	/**
	 * @return
	 */
	public List<KycDealer> getAllNodeAssignmentDealers() {
		
		EntityManager entityManager = getKycEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<KycDealer> criteriaQuery = criteriaBuilder.createQuery(KycDealer.class);
		Root<NodeAssignment> root = criteriaQuery.from(NodeAssignment.class);
		
		criteriaQuery.select(root.get("assignedDealer")).distinct(true);
		
		return getResultList(entityManager, criteriaQuery);
	}
}
