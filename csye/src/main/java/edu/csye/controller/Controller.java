package edu.csye.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.timgroup.statsd.StatsDClient;

import edu.csye.exception.UserNotAuthorizedException;
import edu.csye.helper.Base64Helper;
import edu.csye.model.User;
import edu.csye.service.MyUserDetailService;

@RestController
public class Controller {
	
	private Logger logger = LoggerFactory.getLogger(Controller.class);
	
	@Autowired
	private MyUserDetailService userDetailService;
	@Autowired
    private StatsDClient stasDClient;

	
	
	@PostMapping(path="/v1/user", consumes= "application/json", produces="application/json")
	public @ResponseBody ResponseEntity<User> createNewUser(@RequestBody User user)  {
		logger.info("Info:Calling createNewUserApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("createNewUserApi");
        User storedData = null;
        try {
            long beginDB = System.currentTimeMillis();
            storedData = userDetailService.createUser(user);
            long end = System.currentTimeMillis();
            long timeTaken = end - begin;
            long timeTakenByDBQuery = end - beginDB;
            stasDClient.recordExecutionTime("DB-registerNewUserDBQueryTime", timeTakenByDBQuery);
            logger.info("TIme taken by createNewUser API " + timeTaken + "ms");
            stasDClient.recordExecutionTime("createNewUser", timeTaken);
            return new ResponseEntity<User>(storedData, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Exception while creating new User: "+e);
        }
		
		return null;	
		}
	
	@GetMapping(path="/v1/user/{id}", produces="application/json")
	public @ResponseBody ResponseEntity<User> getUserByID(@PathVariable String id) {
		logger.info("Info:Calling getUserByIDApi");
        stasDClient.incrementCounter("updateUserApi");
        long begin = System.currentTimeMillis();
		User userDetailsFromDatabase = userDetailService.fetchUserById(id);
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by updateUserApi API " + timeTaken + "ms");
        stasDClient.recordExecutionTime("DB-updateUserApiTime", timeTaken);
		return new ResponseEntity<User>(userDetailsFromDatabase, HttpStatus.OK);
	}
	
	@GetMapping(path="/v1/user/self", produces="application/json")
	public @ResponseBody ResponseEntity<User> fetchUser(@RequestHeader(value="Authorization") String auth) throws UnsupportedEncodingException{
		logger.info("Info:Calling fetchUserApi");
		stasDClient.incrementCounter("updateUserApi");
        long begin = System.currentTimeMillis();
		User userDetailsFromDatabase = userDetailService.fetchUser(Base64Helper.getUserName(Base64Helper.convertToSting(auth)));
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by updateUserApi API " + timeTaken + "ms");
        stasDClient.recordExecutionTime("DB-updateUserApiTime", timeTaken);
		if(userDetailsFromDatabase == null)
			throw new UserNotAuthorizedException("Unauthorized");
		return new ResponseEntity<User>(userDetailsFromDatabase, HttpStatus.OK);
	}
	
	@PutMapping(path="/v1/user/self", consumes= "application/json", produces="application/json")
    public @ResponseBody ResponseEntity<Object> updateUserDetails(@RequestBody User user, @RequestHeader(value="Authorization") String auth) throws UnsupportedEncodingException, ParseException {
		logger.info("Info:Calling updateUserDetailsApi");
		stasDClient.incrementCounter("updateUserApi");
        long begin = System.currentTimeMillis();
		int userValue = userDetailService.updateUser(user, auth);
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by updateUserApi API " + timeTaken + "ms");
        stasDClient.recordExecutionTime("DB-updateUserApiTime", timeTaken);
		return new ResponseEntity<Object>("", HttpStatus.NO_CONTENT);
    }
	
	
	
}