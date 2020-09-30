package edu.csye.repository;

import edu.csye.model.User;

public interface UserRepositoryFunctions{
	
	public int updateUserDetails(User user);
	
	public User getUserByEmail(String email);
	
}
