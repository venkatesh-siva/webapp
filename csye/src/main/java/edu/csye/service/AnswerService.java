package edu.csye.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.timgroup.statsd.StatsDClient;

import edu.csye.controller.Controller;
import edu.csye.exception.AnswerNotFoundException;
import edu.csye.exception.InvalidInputException;
import edu.csye.exception.UserNotAuthorizedException;
import edu.csye.helper.Base64Helper;
import edu.csye.helper.DateHelper;
import edu.csye.model.Answer;
import edu.csye.model.Image;
import edu.csye.model.Question;
import edu.csye.repository.AnswersRepository;

@Service
public class AnswerService {

	@Autowired
	private AnswersRepository answersRepository;
	
	@Autowired
	MyUserDetailService myUserDetailService;
	
	@Autowired
	QuestionsService qestionService;
	
	@Autowired
	ImageService imageService;
	
	private Logger logger = LoggerFactory.getLogger(AnswerService.class);
	
	@Autowired
    private StatsDClient stasDClient;
	
	public Answer createAnswer(String questionId, Answer answer,String auth, String userID) throws UnsupportedEncodingException {
		logger.info("Info:Calling createAnswerService");
		long begin = System.currentTimeMillis();
        
		qestionService.getQuestion(questionId);
		
		if(answer.getAnswer_text()==null || answer.getAnswer_text().trim().equals(""))
			throw new InvalidInputException("Answer text is mandatory and missing value");
		
		String userName = Base64Helper.getUserName(Base64Helper.convertToSting(auth));
		//User userdata = myUserDetailService.fetchUser(userName);
		
		answer.setUser_id(userID);
		answer.setCreated_timestamp(DateHelper.getTimeZoneDate());
		answer.setUpdated_timestamp(DateHelper.getTimeZoneDate());
		answer.setQuestion_id(questionId);
		
		long beginDB = System.currentTimeMillis();
		Answer savedAnswer = answersRepository.save(answer);
		
		 long end = System.currentTimeMillis();
		 long timeTakenDB = end - beginDB;
		 logger.info("TIme taken by createAnswerDB " + timeTakenDB + "ms");
         stasDClient.recordExecutionTime("createAnswerDBTime", timeTakenDB);
         
         long timeTaken = end - begin;
         logger.info("TIme taken by createAnswerService " + timeTaken + "ms");
         stasDClient.recordExecutionTime("createAnswerServiceTime", timeTaken);
		return savedAnswer;
	}
	
	public void updateAnswer(String questionId, String answerId, Answer answer, String auth, String userId) {
		logger.info("Info:Calling updateAnswerService");
		long begin = System.currentTimeMillis();
		
		if(answer.getAnswer_text()==null || answer.getAnswer_text().trim().equals(""))
			throw new InvalidInputException("Answer text is mandatory and missing value");
		Question question = qestionService.getQuestion(questionId);
		List<Answer> flag = question.getAnswerList().stream().filter(f -> f.getAnswer_id().equals(answerId)).collect(Collectors.toList());
		if(flag.isEmpty())
			throw new AnswerNotFoundException("The question ID you have provided does not have the provided answer ID, kindly recheck");
		Optional<Answer> answerData = answersRepository.findById(answerId);
		if(!answerData.isPresent())
			throw new AnswerNotFoundException("Answer ID does not exist");
		Answer answerDB = answerData.get();
		if(answerDB.getUser_id().equals(userId)){
			answerDB.setAnswer_text(answer.getAnswer_text());
			answerDB.setUpdated_timestamp(DateHelper.getTimeZoneDate());
			
			long beginDB = System.currentTimeMillis();
			
			answersRepository.save(answerDB);
			
			long end = System.currentTimeMillis();
			 long timeTakenDB = end - beginDB;
			 logger.info("TIme taken by updateAnswerDB " + timeTakenDB + "ms");
	         stasDClient.recordExecutionTime("updateAnswerDBTime", timeTakenDB);
		}else
			throw new UserNotAuthorizedException("Only user who submitted answer can edit answer");
		
		 long end = System.currentTimeMillis();
         long timeTaken = end - begin;
         logger.info("TIme taken by updateAnswerService " + timeTaken + "ms");
         stasDClient.recordExecutionTime("updateAnswerServiceTime", timeTaken);
	}
	
	public Answer getAnswer(String questionId, String answerId) {
		logger.info("Info:Calling getAnswerService");
		long begin = System.currentTimeMillis();
		
		Question question = qestionService.getQuestion(questionId);
		List<Answer> flag = question.getAnswerList().stream().filter(f -> f.getAnswer_id().equals(answerId)).collect(Collectors.toList());
		if(flag.isEmpty())
			throw new AnswerNotFoundException("The question ID you have provided does not have the provided answer ID, kindly recheck");
		
		long beginDB = System.currentTimeMillis();
		
		Optional<Answer> answerData = answersRepository.findById(answerId);
		
		long end = System.currentTimeMillis();
		 long timeTakenDB = end - beginDB;
		 logger.info("TIme taken by getAnswerById Call " + timeTakenDB + "ms");
        stasDClient.recordExecutionTime("getAnswerByIdDBTime", timeTakenDB);
        
		if(!answerData.isPresent())
			throw new AnswerNotFoundException("Answer ID does not exist");
		
		 end = System.currentTimeMillis();
         long timeTaken = end - begin;
         logger.info("TIme taken by getAnswerService " + timeTaken + "ms");
         stasDClient.recordExecutionTime("getAnswerServiceTime", timeTaken);
		return answerData.get();
	}
	
	public void deleteAnswer(String questionId, String answerId,String auth, String userId) {
		logger.info("Info:Calling deleteAnswerService");
		long begin = System.currentTimeMillis();
		
		Question question = qestionService.getQuestion(questionId);
		List<Answer> flag = question.getAnswerList().stream().filter(f -> f.getAnswer_id().equals(answerId)).collect(Collectors.toList());
		if(flag.isEmpty())
			throw new AnswerNotFoundException("The question ID you have provided does not have the provided answer ID, kindly recheck");
		Optional<Answer> answer = answersRepository.findById(answerId);
		if(!answer.isPresent())
			throw new AnswerNotFoundException("Answer ID does not exist");
		Answer answerDB = answer.get();
		if(answerDB.getUser_id().equals(userId)) {
			List<Image> attachments = answerDB.getAttachments();
			for(Image image :attachments) {
				imageService.deleteFile(questionId, answerId, image.getFile_id(),null);
			}
			long beginDB = System.currentTimeMillis();
			
			answersRepository.delete(answerDB);
			
			long end = System.currentTimeMillis();
			 long timeTakenDB = end - beginDB;
			 logger.info("TIme taken by deleteAnswerDB Call " + timeTakenDB + "ms");
	        stasDClient.recordExecutionTime("deleteAnswerDBTime", timeTakenDB);
		}
		else
			throw new UserNotAuthorizedException("Only user who submitted answer can delete");
		//Answer answerData = answersRepository.getOne(answerId);
		 long end = System.currentTimeMillis();
         long timeTaken = end - begin;
         logger.info("TIme taken by deleteAnswerService " + timeTaken + "ms");
         stasDClient.recordExecutionTime("deleteAnswerServiceTime", timeTaken);
	}
}
