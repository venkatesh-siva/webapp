package edu.csye.service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.timgroup.statsd.StatsDClient;

import edu.csye.exception.InvalidInputException;
import edu.csye.exception.LowPasswordStrengthException;
import edu.csye.exception.MandatoryFieldValueMissingException;
import edu.csye.exception.NoUpdateNeededException;
import edu.csye.exception.UserAlreadyExistsException;
import edu.csye.exception.UserNotAuthorizedException;
import edu.csye.exception.UserNotFoundException;
import edu.csye.helper.Base64Helper;
import edu.csye.helper.BcryptHelper;
import edu.csye.helper.DateHelper;
import edu.csye.helper.PasswordStrengthValidationHelper;
import edu.csye.helper.UserNameValidationHelper;
import edu.csye.model.User;
import edu.csye.repository.UserRepository;
import edu.csye.repository.UserRepositoryFunctions;

@Service
public class MyUserDetailService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRepositoryFunctions repo;
	
	private Logger logger = LoggerFactory.getLogger(MyUserDetailService.class);
	
	@Autowired
    private StatsDClient stasDClient;
	
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
		logger.info("Info:Calling updateUserService");
		long begin = System.currentTimeMillis();
		
		if(user.getUsername()==null || user.getPassword()==null || user.getFirst_name()==null || user.getLast_name() == null || 
				user.getUsername().equals("") || user.getFirst_name().equals("") || user.getLast_name().equals("") || user.getPassword().equals(""))
			throw new MandatoryFieldValueMissingException("Please fill out all mandatory fields: username, password, first_name, last_name");
		
		if(!UserNameValidationHelper.validateName(user.getFirst_name()) || !UserNameValidationHelper.validateName(user.getLast_name()))
			throw new InvalidInputException("Please provide a proper name");
		
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
					  throw new LowPasswordStrengthException("Your password strength is low please try a different password with minimum of 8 characters with atleaset one number, one upper case and one lower case");
				  }
				  String newPassword = BcryptHelper.bcryptUserPassword(user).getPassword();
				  checkForUpdate = true;
				  password = newPassword;
			 }
		 }
		if(!checkForUpdate) {
			throw new NoUpdateNeededException("No change in any of the values provided");
		}
		
		long beginDB = System.currentTimeMillis();
        
		int userUpdate = userRepository.updateUser(Firstname, lastNmae, email, password, DateHelper.getTimeZoneDate());
		
		long end = System.currentTimeMillis();
		long timeTakenDB = end - beginDB;
        logger.info("TIme taken by updateUserService " + timeTakenDB + "ms");
        stasDClient.recordExecutionTime("updateUserServiceTime", timeTakenDB);
		
		long timeTaken = end - begin;
        logger.info("TIme taken by updateUserService " + timeTaken + "ms");
        stasDClient.recordExecutionTime("updateUserServiceTime", timeTaken);
		
		return userUpdate;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repo.getUserByEmail(username);
		if(user==null)
			throw new UsernameNotFoundException("User not found");
		return new UserPrincipal(user);
	}
	
	public User createUser(User user) throws ParseException {
		logger.info("Info:Calling createUserService");
		long begin = System.currentTimeMillis();
		
		if(user.getUsername()==null || user.getPassword()==null || user.getFirst_name()==null || user.getLast_name() == null || 
				user.getUsername().equals("") || user.getFirst_name().equals("") || user.getLast_name().equals("") || user.getPassword().equals(""))
			throw new MandatoryFieldValueMissingException("Please fill out all mandatory fields: username, password, first_name, last_name");
		
		if(!UserNameValidationHelper.validateEmail(user.getUsername()))
			throw new InvalidInputException("Please provide a proper email id in the username");
		
		if(!UserNameValidationHelper.validateName(user.getFirst_name()) || !UserNameValidationHelper.validateName(user.getLast_name()))
			throw new InvalidInputException("Please provide a proper name");
		
		if(checkUserAlreadyExist(user.getUsername())) 
			throw new UserAlreadyExistsException("Username already exist, please provide a different one");
		
		if(PasswordStrengthValidationHelper.validatePassword(user.getPassword())) {
			user = BcryptHelper.bcryptUserPassword(user);
			 String date = DateHelper.getTimeZoneDate();
			user.setAccount_created(date);
			user.setAccount_updated(date);
			long beginDB = System.currentTimeMillis();
			User storedData;
			try {
				storedData = userRepository.save(user);
			}catch(Exception e) {
				throw new UserAlreadyExistsException("Username already exist, please provide a different one");
			}
			long end = System.currentTimeMillis();
			long timeTakenDB = end - beginDB;
	        logger.info("TIme taken by createUserServiceDB " + timeTakenDB + "ms");
	        stasDClient.recordExecutionTime("createUserServiceDBTime", timeTakenDB);
			end = System.currentTimeMillis();
			long timeTaken = end - begin;
	        logger.info("TIme taken by createUserService " + timeTaken + "ms");
	        stasDClient.recordExecutionTime("createUserServiceTime", timeTaken);
			return storedData;
		}
		else 
			throw new LowPasswordStrengthException("Your password strength is low please try a different password with minimum of 8 characters with atleaset one number, one upper case and one lower case");

	}
	
	public User fetchUserById(String userId) {
		logger.info("Info:Calling fetchUserByIdService");
		long begin = System.currentTimeMillis();
		
		if(userId==null || userId.trim().equals(""))
			throw new InvalidInputException("Not a valid userID");
		long beginDB = System.currentTimeMillis();
		Optional<User> userData = userRepository.findById(userId);
		long end = System.currentTimeMillis();
		long timeTakenDB = end - beginDB;
        logger.info("TIme taken by fetchUserByIdDB " + timeTakenDB + "ms");
        stasDClient.recordExecutionTime("fetchUserByIdDBTime", timeTakenDB);
		if(!userData.isPresent()) {
			throw new UserNotFoundException("Can't find user with the id provided");
		}
		end = System.currentTimeMillis();
		long timeTaken = end - begin;
        logger.info("TIme taken by fetchUserByIdService " + timeTaken + "ms");
        stasDClient.recordExecutionTime("fetchUserByIdServiceTime", timeTaken);
		return userData.get();
	}

}
