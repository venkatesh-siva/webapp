package edu.csye.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.sun.el.parser.ParseException;
import com.timgroup.statsd.StatsDClient;

import edu.csye.model.Answer;
import edu.csye.model.Question;
import edu.csye.model.User;
import edu.csye.security.AmazonSNSClient;
import edu.csye.service.AnswerService;
import edu.csye.service.ImageService;
import edu.csye.service.MyUserDetailService;
import edu.csye.service.QuestionsService;
import edu.csye.service.UserPrincipal;

@RestController
public class AnswersController {
	
	private Logger logger = LoggerFactory.getLogger(AnswersController.class);
	
	@Autowired
	private AnswerService answerService;
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
    private StatsDClient stasDClient;
	
	@Autowired
	private AmazonSNSClient snsClient;
	
	@Autowired
	private QuestionsService questionService;
	
	@Autowired
	private MyUserDetailService userService; 
	
	@PostMapping(path="/v1/question/{questionId}/answer", consumes= "application/json", produces="application/json")
	public @ResponseBody ResponseEntity<Answer> createAnswer(@PathVariable String questionId, @RequestBody Answer answer,@RequestHeader(value="Authorization") String auth, Principal principal) throws ParseException, UnsupportedEncodingException {
		logger.info("Info:Calling createAnswerApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("createAnswerApi");
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();

		Answer storedData = answerService.createAnswer(questionId,answer, auth, userId);
		if(storedData!=null) {
			Question question = questionService.getQuestion(questionId);
			User user = userService.fetchUserById(question.getUser_id());
			String getHashValue = user.getUsername()+questionId+storedData.getAnswer_id()+storedData.getAnswer_text();
			snsClient.publish(user.getUsername()+","+questionId+","+storedData.getAnswer_id()+
					"http://prod.venkateshcsye6225.me//v1/question/"+questionId+","+
					"http://prod.venkateshcsye6225.me//v1/question/"+questionId+"/answer/"+storedData.getAnswer_id()+
					",createanswer,"+getHashValue.hashCode());
		}
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by createAnswer API " + timeTaken + "ms");
        stasDClient.recordExecutionTime("createAnswerApiTime", timeTaken);
		return new ResponseEntity<Answer>(storedData, HttpStatus.CREATED);	
		}
	
	@PutMapping(path="/v1/question/{questionId}/answer/{answerId}", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<Answer> updateAnswer(@PathVariable String answerId, @PathVariable String questionId, @RequestBody Answer answer,@RequestHeader(value="Authorization") String auth, Principal principal){
		logger.info("Info:Calling updateAnswerApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("updateAnswerApi");
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		answerService.updateAnswer(questionId, answerId, answer, auth, userId);
		if(answer!=null) {
			Question question = questionService.getQuestion(questionId);
			User user = userService.fetchUserById(question.getUser_id());
			String getHashValue = user.getUsername()+questionId+answer.getAnswer_id()+answer.getAnswer_text();
			snsClient.publish(user.getUsername()+","+questionId+","+answer.getAnswer_id()+
					"http://prod.venkateshcsye6225.me//v1/question/"+questionId+","+
					"http://prod.venkateshcsye6225.me//v1/question/"+questionId+"/answer/"+answer.getAnswer_id()+
					",updateanswer,"+getHashValue.hashCode());
			//snsClient.publish(user.getUsername()+","+questionId+",updateanswer");
		}
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by updateAnswer API " + timeTaken + "ms");
        stasDClient.recordExecutionTime("updateAnswerApiTime", timeTaken);
		return new ResponseEntity<Answer>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(path="/v1/question/{questionId}/answer/{answerId}")
	public @ResponseBody ResponseEntity<Answer> getAnswer(@PathVariable String answerId, @PathVariable String questionId){
		logger.info("Info:Calling getAnswerApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("getAnswerApi");
		Answer answerData = answerService.getAnswer(questionId, answerId);
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by getAnswer API " + timeTaken + "ms");
        stasDClient.recordExecutionTime("getAnswerApiTime", timeTaken);
		return new ResponseEntity<Answer>(answerData, HttpStatus.OK);
	}
	
	@DeleteMapping(path="/v1/question/{questionId}/answer/{answerId}")
	public @ResponseBody ResponseEntity<Answer> deleteAnswer(@PathVariable String answerId, @PathVariable String questionId,@RequestHeader(value="Authorization") String auth, Principal principal){
		logger.info("Info:Calling deleteAnswerApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("deleteAnswerApi");
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		answerService.deleteAnswer(questionId, answerId, auth, userId);
		
		Question question = questionService.getQuestion(questionId);
		User user = userService.fetchUserById(question.getUser_id());
		String getHashValue = user.getUsername()+questionId+answerId;
		snsClient.publish(user.getUsername()+","+questionId+","+answerId+
				"http://prod.venkateshcsye6225.me//v1/question/"+questionId+","+
				""+
				",deleteanswer,"+getHashValue.hashCode());
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by deleteAnswer API " + timeTaken + "ms");
        stasDClient.recordExecutionTime("deleteAnswerApiTime", timeTaken);
		return new ResponseEntity<Answer>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(path="/v1/question/{questionId}/answer/{answerId}/file")
	public @ResponseBody ResponseEntity<Question> uplodaImage(@PathVariable String questionId,@PathVariable String answerId,@RequestPart(value = "file") MultipartFile multipartFile,@RequestHeader(value="Authorization") String auth, Principal principal) {
		logger.info("Info:Calling uplodaImageAnswerApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("uplodaImageAnswerApi");
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		imageService.uploadFile(multipartFile, questionId, answerId, userId);
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by uplodaImageAnswer API " + timeTaken + "ms");
        stasDClient.recordExecutionTime("uplodaImageAnswerApiTime", timeTaken);
		return new ResponseEntity<Question>(HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/v1/question/{questionId}/answer/{answerId}/file/{fileId}")
	public @ResponseBody ResponseEntity<Question> deleteImage(@PathVariable String questionId,@PathVariable String answerId,@PathVariable String fileId,@RequestHeader(value="Authorization") String auth, Principal principal){
		logger.info("Info:Calling deleteImageAnswerApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("deleteImageAnswerApi");
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		String deleteResult = imageService.deleteFile(questionId, answerId, fileId, userId);
		if(deleteResult == null) {
			
		}
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by deleteImageAnswer API " + timeTaken + "ms");
        stasDClient.recordExecutionTime("deleteImageAnswerApiTime", timeTaken);
		return new ResponseEntity<Question>(HttpStatus.NO_CONTENT);
	}
}
