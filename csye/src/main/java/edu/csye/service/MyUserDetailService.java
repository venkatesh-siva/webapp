package edu.csye.service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import edu.csye.exception.LowPasswordStrengthException;
import edu.csye.exception.MandatoryFieldValueMissingException;
import edu.csye.exception.NoUpdateNeededException;
import edu.csye.exception.UserAlreadyExistsException;
import edu.csye.exception.UserNotAuthorizedException;
import edu.csye.helper.Base64Helper;
import edu.csye.helper.BcryptHelper;
import edu.csye.helper.DateHelper;
import edu.csye.helper.PasswordStrengthValidationHelper;
import edu.csye.model.User;
import edu.csye.repository.UserRepository;
import edu.csye.repository.UserRepositoryFunctions;

@Service
public class MyUserDetailService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRepositoryFunctions repo;
	
	public boolean checkUserAlreadyExist(String username) {
		try {
			loadUserByUsername(username);
			return true;
		}catch(UsernameNotFoundException userNotFound) {
			return false;
		}
	}
	
	public User fetchUser(String username) {
		return userRepository.getUserByEmail(username);
	}
	
	public int updateUser(User user, String auth) throws UnsupportedEncodingException, ParseException {
		if(user.getUsername()==null || user.getPassword()==null || user.getFirst_name()==null || user.getLast_name() == null || 
				user.getUsername().equals("") || user.getFirst_name().equals("") || user.getLast_name().equals("") || user.getPassword().equals(""))
			throw new MandatoryFieldValueMissingException("Please fill out all mandatory fields: username, password, first_name, last_name");
		boolean checkForUpdate = false;
		String userName = Base64Helper.getUserName(Base64Helper.convertToSting(auth));
		if(!userName.equalsIgnoreCase(user.getUsername()))
			throw new UserNotAuthorizedException("Sorry, Cannot change other users values");
		User userDataBase =  fetchUser(userName);
		
		String Firstname = userDataBase.getFirst_name();
		String lastNmae = userDataBase.getLast_name();
		String email = userDataBase.getUsername();
		String password = userDataBase.getPassword();
		
		if(!userDataBase.getFirst_name().equalsIgnoreCase(user.getFirst_name())) {
			checkForUpdate =true;
			Firstname = user.getFirst_name();
		}
		  if(!userDataBase.getLast_name().equalsIgnoreCase(user.getLast_name())) {
			  checkForUpdate = true;
			  lastNmae = user.getLast_name();
		  }
		  if(!userDataBase.getUsername().equalsIgnoreCase(user.getUsername())) {
			   checkForUpdate = true;
			   email = user.getUsername();
		   }
		  if(user.getPassword()!=null)
		  {
			  String unchangedPassword = user.getPassword();
			  if(!BCrypt.checkpw(user.getPassword(), userDataBase.getPassword())){
				  //BCrypt.checkpw(user.getPassword(), userDataBase.getPassword())
				  if(!PasswordStrengthValidationHelper.validatePassword(unchangedPassword)) {
					  throw new LowPasswordStrengthException("Your password strength is low please try a different password with 5 to 8 characters and atleaset one number, one upper case and one lower case");
				  }
				  String newPassword = BcryptHelper.bcryptUserPassword(user).getPassword();
				  checkForUpdate = true;
				  password = newPassword;
			  }
		  }
		if(!checkForUpdate) {
			throw new NoUpdateNeededException("No change in any of the values provided");
		}
			
		return userRepository.updateUser(Firstname, lastNmae, email, password, DateHelper.getTimeZoneDate());
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.getUserByEmail(username);
		if(user==null)
			throw new UsernameNotFoundException("User not found");
		return new UserPrincipal(user);
	}
	
	public User createUser(User user) throws ParseException {
		if(user.getUsername()==null || user.getPassword()==null || user.getFirst_name()==null || user.getLast_name() == null || 
				user.getUsername().equals("") || user.getFirst_name().equals("") || user.getLast_name().equals("") || user.getPassword().equals(""))
			throw new MandatoryFieldValueMissingException("Please fill out all mandatory fields: username, password, first_name, last_name");
		if(checkUserAlreadyExist(user.getUsername())) 
			throw new UserAlreadyExistsException("Username already exist, please provide a different one");
		if(PasswordStrengthValidationHelper.validatePassword(user.getPassword())) {
			user = BcryptHelper.bcryptUserPassword(user);
			 String date = DateHelper.getTimeZoneDate();
			user.setAccount_created(date);
			user.setAccount_updated(date);
			User storedData = userRepository.save(user);
			return storedData;
		}
		else 
			throw new LowPasswordStrengthException("Your password strength is low please try a different password with 5 to 8 characters and atleaset one number, one upper case and one lower case");

	}

}
