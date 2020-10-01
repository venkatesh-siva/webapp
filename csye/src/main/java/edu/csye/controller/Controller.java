package edu.csye.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.csye.helper.Base64Helper;
import edu.csye.model.User;
import edu.csye.service.MyUserDetailService;

@RestController
public class Controller {
	
	@Autowired
	private MyUserDetailService userDetailService;
	
	
	@PostMapping(path="/v1/user", consumes= "application/json", produces="application/json")
	public @ResponseBody ResponseEntity<User> createNewUser(@RequestBody User user) throws ParseException {
		User storedData = userDetailService.createUser(user);
		return new ResponseEntity<User>(storedData, HttpStatus.CREATED);	
		}
	
	@GetMapping(path="/v1/user/self", produces="application/json")
	public @ResponseBody ResponseEntity<User> fetchUser(@RequestHeader(value="Authorization") String auth) throws UnsupportedEncodingException{
		User userDetailsFromDatabase = userDetailService.fetchUser(Base64Helper.getUserName(Base64Helper.convertToSting(auth)));
		return new ResponseEntity<User>(userDetailsFromDatabase, HttpStatus.OK);
	}
	
	@PutMapping(path="/v1/user/self", consumes= "application/json", produces="application/json")
    public @ResponseBody ResponseEntity<Object> updateUserDetails(@RequestBody User user, @RequestHeader(value="Authorization") String auth) throws UnsupportedEncodingException, ParseException {
		int userValue = userDetailService.updateUser(user, auth);
		return new ResponseEntity<Object>("", HttpStatus.NO_CONTENT);
    }
	
}