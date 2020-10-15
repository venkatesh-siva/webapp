package edu.csye.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sun.el.parser.ParseException;

import edu.csye.model.Answer;
import edu.csye.service.AnswerService;
import edu.csye.service.UserPrincipal;

@RestController
public class AnswersController {
	
	@Autowired
	private AnswerService answerService;
	
	@PostMapping(path="/v1/question/{questionId}/answer", consumes= "application/json", produces="application/json")
	public @ResponseBody ResponseEntity<Answer> createAnswer(@PathVariable String questionId, @RequestBody Answer answer,@RequestHeader(value="Authorization") String auth, Principal principal) throws ParseException, UnsupportedEncodingException {
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();

		Answer storedData = answerService.createAnswer(questionId,answer, auth, userId);
		return new ResponseEntity<Answer>(storedData, HttpStatus.CREATED);	
		}
	
	@PutMapping(path="/v1/question/{questionId}/answer/{answerId}", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<Answer> updateAnswer(@PathVariable String answerId, @PathVariable String questionId, @RequestBody Answer answer,@RequestHeader(value="Authorization") String auth, Principal principal){
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		answerService.updateAnswer(questionId, answerId, answer, auth, userId);
		return new ResponseEntity<Answer>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(path="/v1/question/{questionId}/answer/{answerId}")
	public @ResponseBody ResponseEntity<Answer> getAnswer(@PathVariable String answerId, @PathVariable String questionId){
		Answer answerData = answerService.getAnswer(questionId, answerId);
		return new ResponseEntity<Answer>(answerData, HttpStatus.OK);
	}
	
	@DeleteMapping(path="/v1/question/{questionId}/answer/{answerId}")
	public @ResponseBody ResponseEntity<Answer> deleteAnswer(@PathVariable String answerId, @PathVariable String questionId,@RequestHeader(value="Authorization") String auth, Principal principal){
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		answerService.deleteAnswer(questionId, answerId, auth, userId);
		return new ResponseEntity<Answer>(HttpStatus.NO_CONTENT);
	}
}
