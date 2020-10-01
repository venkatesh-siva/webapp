package edu.csye.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import edu.csye.model.User;

public class UserRepositoryFunctionsImpl implements UserRepositoryFunctions {
	
	 @PersistenceContext
	    EntityManager entityManager;

	@Override
	public User getUserByEmail(String email) {
		Query query = entityManager.createNativeQuery("SELECT * FROM user  WHERE username = :email", User.class);

        query.setParameter("email", email);
        //System.out.println(query.getSingleResult());
        User user = (User) query.getResultList().stream().findFirst().orElse(null);
        return user;
	}

}
