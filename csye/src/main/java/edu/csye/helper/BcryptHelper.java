package edu.csye.helper;


import org.springframework.security.crypto.bcrypt.BCrypt;

import edu.csye.model.User;

public class BcryptHelper {
	
	private static int workload = 12;
	
	
	public static User bcryptUserPassword(User user) {
		user.setPassword(hashPassword(user.getPassword()));
		return user;
	}
	
	public static String hashPassword(String password_plaintext) {
		String salt = BCrypt.gensalt(workload);
		String hashed_password = BCrypt.hashpw(password_plaintext, salt);

		return(hashed_password);
	}
	
	
}
