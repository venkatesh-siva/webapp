package edu.csye.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import edu.csye.model.User;

public class UserRepositoryFunctionsImpl implements UserRepositoryFunctions {
	
	 @PersistenceContext
	    EntityManager entityManager;

	@Override
	public int updateUserDetails(User user) {
		String firstName = user.getFirst_name();
        String lastName = user.getLast_name();
        String email = user.getEmail_address();

        int rowsAffected = 0;
        Query query = entityManager.createNativeQuery("UPDATE user SET first_name= :firstName, last_name= :lastName  WHERE email_address = :email", User.class);



        query=  entityManager.createNativeQuery("UPDATE user u"
                        + "SET u.first_name = :firstName"
                        + "WHERE u.email_address = :email");

        rowsAffected = query.executeUpdate();
        return rowsAffected;
	}

	@Override
	public User getUserByEmail(String email) {
		Query query = entityManager.createNativeQuery("SELECT * FROM user  WHERE email_address = :email", User.class);

        query.setParameter("email", email);
        //System.out.println(query.getSingleResult());
        User user = (User) query.getResultList().stream().findFirst().orElse(null);
        return user;
	}

}
