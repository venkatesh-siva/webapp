package edu.csye.controller;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.csye.filter.PasswordValidator;
import edu.csye.helper.Base64Helper;
import edu.csye.helper.BcryptHelper;
import edu.csye.model.User;
import edu.csye.repository.UserRepository;

@RestController
@RequestMapping("/v1/user")
public class Controller {
	
	@Autowired
    private UserRepository userRespository;
	
	
	@PostMapping("/")
	public @ResponseBody ResponseEntity<Object> createNewUser(@RequestBody User user) throws JsonGenerationException, JsonMappingException, IOException {
		//userRespository.createUser(user.getFirst_name(), user.getLast_name(), user.getEmail_address(), user.getPassword());
		if(PasswordValidator.validatePassword(user.getPassword())) {
			user = BcryptHelper.bcryptUserPassword(user);
			User storedData = userRespository.save(user);
			ObjectMapper mapper = new ObjectMapper();
		      //Converting the Object to JSONString
		      String jsonString = mapper.writeValueAsString(storedData);
			JSONObject obj = new JSONObject(jsonString);
			obj.remove("password");
			obj.toString();
			return new ResponseEntity<Object>(obj.toString(), HttpStatus.OK);
		}else return new ResponseEntity<Object>("Your password is not strong please try a differnet one", HttpStatus.OK);
	}
	
	@GetMapping("/self")
	public @ResponseBody ResponseEntity<Object> fetchUser(@RequestHeader(value="Authorization") String auth) throws JsonGenerationException, JsonMappingException, IOException{
		System.out.println(auth);
		User user = userRespository.getUserByEmail(Base64Helper.getUserName(Base64Helper.convertToSting(auth)));
		ObjectMapper mapper = new ObjectMapper();
	      //Converting the Object to JSONString
	      String jsonString = mapper.writeValueAsString(user);
		JSONObject obj = new JSONObject(jsonString);
		obj.remove("password");
		obj.toString();
		return new ResponseEntity<Object>(obj.toString(), HttpStatus.OK);
	}
	
	@PutMapping("/self")
    public @ResponseBody ResponseEntity<Object> updateUserDetails(@RequestBody User user, @RequestHeader(value="Authorization") String auth) throws JsonGenerationException, JsonMappingException, IOException {
		User userValue = userRespository.getUserByEmail(Base64Helper.getUserName(Base64Helper.convertToSting(auth)));
        userRespository.updateUser(user.getFirst_name(), user.getLast_name(), userValue.getEmail_address());
        User userData = userRespository.getUserByEmail(Base64Helper.getUserName(Base64Helper.convertToSting(auth)));
        ObjectMapper mapper = new ObjectMapper();
	      //Converting the Object to JSONString
	      String jsonString = mapper.writeValueAsString(userData);
		JSONObject obj = new JSONObject(jsonString);
		obj.remove("password");
		obj.toString();
		return new ResponseEntity<Object>(obj.toString(), HttpStatus.OK);
    }
	
}