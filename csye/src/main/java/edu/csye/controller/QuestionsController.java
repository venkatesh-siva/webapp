package edu.csye.controller;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.util.List;

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

import com.timgroup.statsd.StatsDClient;

import edu.csye.model.Question;
import edu.csye.service.ImageService;
import edu.csye.service.QuestionsService;
import edu.csye.service.UserPrincipal;

@RestController
public class QuestionsController {
	
	private Logger logger = LoggerFactory.getLogger(QuestionsController.class);
	
	@Autowired
	private QuestionsService questionsService;
	
	@Autowired
	private ImageService imageService;
	
	@Autowired
    private StatsDClient stasDClient;
	
	
	@PostMapping(path="/v1/question/", consumes= "application/json", produces="application/json")
	public @ResponseBody ResponseEntity<Question> createNewQuestion(@RequestBody Question question, @RequestHeader(value="Authorization") String auth, Principal principal) throws ParseException, UnsupportedEncodingException {
		logger.info("Info:Calling createNewQuestionApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("createNewQuestionApi");
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();

		Question storedData = questionsService.createQuestion(question, auth, userId);
		
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by createNewQuestion API " + timeTaken + "ms");
        stasDClient.recordExecutionTime("createNewQuestionApiTime", timeTaken);
		return new ResponseEntity<Question>(storedData, HttpStatus.CREATED);	
	}
	
	@GetMapping(path="/v1/question/{questionId}")
	public @ResponseBody ResponseEntity<Question> getOneQuestion(@PathVariable String questionId){
		logger.info("Info:Calling getOneQuestionApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("getOneQuestionApi");
		Question questionData = questionsService.getQuestion(questionId);
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by getOneQuestion Api " + timeTaken + "ms");
        stasDClient.recordExecutionTime("getOneQuestionApiTime", timeTaken);
		return new ResponseEntity<Question>(questionData, HttpStatus.OK);
	}
	
	@GetMapping(path="/v1/questions")
	public @ResponseBody ResponseEntity<List<Question>> getAllQuestions(){
		logger.info("Info:Calling getAllQuestionsApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("getAllQuestionsApi");
		List<Question> questionData = questionsService.getQuestions();
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by getAllQuestions Api " + timeTaken + "ms");
        stasDClient.recordExecutionTime("getAllQuestionsTime", timeTaken);
		return new ResponseEntity<List<Question>>(questionData, HttpStatus.OK);
	}
	
	@DeleteMapping(path="/v1/question/{questionId}")
	public @ResponseBody ResponseEntity<Question> deleteQuestion(@PathVariable String questionId,@RequestHeader(value="Authorization") String auth, Principal principal) throws UnsupportedEncodingException{
		logger.info("Info:Calling deleteQuestionApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("deleteQuestionApi");
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		questionsService.deleteQuestion(questionId, auth,userId);
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by deleteQuestion Api " + timeTaken + "ms");
        stasDClient.recordExecutionTime("deleteQuestionApiTime", timeTaken);
		return new ResponseEntity<Question>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping(path="/v1/question/{questionId}", consumes="application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<Question> updateQuestion(@PathVariable String questionId,@RequestBody Question question,@RequestHeader(value="Authorization") String auth, Principal principal) throws UnsupportedEncodingException{
		logger.info("Info:Calling updateQuestionApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("updateQuestionApi");
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		questionsService.updateQuestion(questionId, question, auth, userId);
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by updateQuestion Api " + timeTaken + "ms");
        stasDClient.recordExecutionTime("updateQuestionTime", timeTaken);
		return new ResponseEntity<Question>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(path="/v1/question/{questionId}/file")
	public @ResponseBody ResponseEntity<Question> uplodaImage(@PathVariable String questionId,@RequestPart(value = "file") MultipartFile multipartFile,@RequestHeader(value="Authorization") String auth, Principal principal) {
		logger.info("Info:Calling uplodaImageQuestionApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("uplodaImageQuestionApi");
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		imageService.uploadFile(multipartFile, questionId, null, userId);
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by uuplodaImageQuestion Api " + timeTaken + "ms");
        stasDClient.recordExecutionTime("uplodaImageQuestionAPITime", timeTaken);
		return new ResponseEntity<Question>(HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/v1/question/{questionId}/file/{fileId}")
	public @ResponseBody ResponseEntity<Question> deleteImage(@PathVariable String questionId,@PathVariable String fileId,@RequestHeader(value="Authorization") String auth, Principal principal){
		logger.info("Info:Calling deleteImageQuestionApi");
		long begin = System.currentTimeMillis();
        stasDClient.incrementCounter("deleteImageQuestionApi");
		UserPrincipal userPrincipal = (UserPrincipal) ((Authentication)principal).getPrincipal();
		String userId = userPrincipal.getUserID();
		String deleteResult = imageService.deleteFile(questionId, null,fileId, userId);
		if(deleteResult == null) {
			
		}
		long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by deleteImageQuestion Api " + timeTaken + "ms");
        stasDClient.recordExecutionTime("deleteImageQuestionAPITime", timeTaken);
		return new ResponseEntity<Question>(HttpStatus.NO_CONTENT);
	}
	
}
