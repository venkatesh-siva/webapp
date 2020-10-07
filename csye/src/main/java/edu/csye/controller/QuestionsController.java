package edu.csye.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.util.List;

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

import edu.csye.model.Question;
import edu.csye.service.QuestionsService;
import edu.csye.service.UserPrincipal;

@RestController
public class QuestionsController {
	
	@Autowired
	private QuestionsService questionsService;
	
	
	@PostMapping(path="/v1/question/", consumes= "application/json", produces="application/json")
	public @ResponseBody ResponseEntity<Question> createNewUser(@RequestBody Question question, @RequestHeader(value="Authorization") String auth, Principal principal) throws ParseException, UnsupportedEncodingException {
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();

		Question storedData = questionsService.createQuestion(question, auth, userId);
		return new ResponseEntity<Question>(storedData, HttpStatus.CREATED);	
	}
	
	@GetMapping(path="/v1/question/{questionId}")
	public @ResponseBody ResponseEntity<Question> getOneQuestion(@PathVariable String questionId,@RequestHeader(value="Authorization") String auth){
		Question questionData = questionsService.getQuestion(questionId);
		return new ResponseEntity<Question>(questionData, HttpStatus.OK);
	}
	
	@GetMapping(path="/v1/questions")
	public @ResponseBody ResponseEntity<List<Question>> getAllQuestions(@RequestHeader(value="Authorization") String auth){
		List<Question> questionData = questionsService.getQuestions();
		return new ResponseEntity<List<Question>>(questionData, HttpStatus.OK);
	}
	
	@DeleteMapping(path="/v1/question/{questionId}")
	public @ResponseBody ResponseEntity<Question> deleteQuestion(@PathVariable String questionId,@RequestHeader(value="Authorization") String auth, Principal principal) throws UnsupportedEncodingException{
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		questionsService.deleteQuestion(questionId, auth,userId);
		return new ResponseEntity<Question>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping(path="/v1/question/{questionId}", consumes="application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<Question> updateQuestion(@PathVariable String questionId,@RequestBody Question question,@RequestHeader(value="Authorization") String auth, Principal principal) throws UnsupportedEncodingException{
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		questionsService.updateQuestion(questionId, question, auth, userId);
		return new ResponseEntity<Question>(HttpStatus.NO_CONTENT);
	}
	
}
