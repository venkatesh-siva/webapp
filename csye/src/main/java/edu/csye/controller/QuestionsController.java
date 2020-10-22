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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import edu.csye.model.Question;
import edu.csye.service.ImageService;
import edu.csye.service.QuestionsService;
import edu.csye.service.UserPrincipal;

@RestController
public class QuestionsController {
	
	@Autowired
	private QuestionsService questionsService;
	
	@Autowired
	private ImageService imageService;
	
	
	@PostMapping(path="/v1/question/", consumes= "application/json", produces="application/json")
	public @ResponseBody ResponseEntity<Question> createNewUser(@RequestBody Question question, @RequestHeader(value="Authorization") String auth, Principal principal) throws ParseException, UnsupportedEncodingException {
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();

		Question storedData = questionsService.createQuestion(question, auth, userId);
		return new ResponseEntity<Question>(storedData, HttpStatus.CREATED);	
	}
	
	@GetMapping(path="/v1/question/{questionId}")
	public @ResponseBody ResponseEntity<Question> getOneQuestion(@PathVariable String questionId){
		Question questionData = questionsService.getQuestion(questionId);
		return new ResponseEntity<Question>(questionData, HttpStatus.OK);
	}
	
	@GetMapping(path="/v1/questions")
	public @ResponseBody ResponseEntity<List<Question>> getAllQuestions(){
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
	
	@PostMapping(path="/v1/question/{questionId}/file")
	public @ResponseBody ResponseEntity<Question> uplodaImage(@PathVariable String questionId,@RequestPart(value = "file") MultipartFile multipartFile,@RequestHeader(value="Authorization") String auth, Principal principal) {
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		imageService.uploadFile(multipartFile, questionId, null, userId);
		return new ResponseEntity<Question>(HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/v1/question/{questionId}/file/{fileId}")
	public @ResponseBody ResponseEntity<Question> deleteImage(@PathVariable String questionId,@PathVariable String fileId,@RequestHeader(value="Authorization") String auth, Principal principal){
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		String deleteResult = imageService.deleteFile(questionId, null,fileId, userId);
		if(deleteResult == null) {
			
		}
		return new ResponseEntity<Question>(HttpStatus.NO_CONTENT);
	}
	
}
