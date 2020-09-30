package edu.csye.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.csye.model.User;
import edu.csye.repository.UserRepositoryFunctions;

@Service
public class MyUserDetailService implements UserDetailsService{
	
	@Autowired
	private UserRepositoryFunctions repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.getUserByEmail(username);
		System.out.println("***********************");
		System.out.println(user.getEmail_address() +" "+ user.getPassword());
		System.out.println("***********************");
		if(user==null)
			throw new UsernameNotFoundException("User not found");
		return new UserPrincipal(user);
	}

}
