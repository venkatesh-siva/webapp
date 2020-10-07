package edu.csye.service;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.csye.exception.AnswerNotFoundException;
import edu.csye.exception.InvalidInputException;
import edu.csye.exception.UserNotAuthorizedException;
import edu.csye.helper.Base64Helper;
import edu.csye.helper.DateHelper;
import edu.csye.model.Answer;
import edu.csye.repository.AnswersRepository;

@Service
public class AnswerService {

	@Autowired
	private AnswersRepository answersRepository;
	
	@Autowired
	MyUserDetailService myUserDetailService;
	
	@Autowired
	QuestionsService qestionService;
	
	public Answer createAnswer(String questionId, Answer answer,String auth, String userID) throws UnsupportedEncodingException {
		
		qestionService.getQuestion(questionId);
		
		if(answer.getAnswer_text()==null || answer.getAnswer_text().trim().equals(""))
			throw new InvalidInputException("Answer text is mandatory and missing value");
		
		String userName = Base64Helper.getUserName(Base64Helper.convertToSting(auth));
		//User userdata = myUserDetailService.fetchUser(userName);
		
		answer.setUser_id(userID);
		answer.setCreated_timestamp(DateHelper.getTimeZoneDate());
		answer.setUpdated_timestamp(DateHelper.getTimeZoneDate());
		answer.setQuestion_id(questionId);
		
		Answer savedAnswer = answersRepository.save(answer);
		
		return savedAnswer;
	}
	
	public void updateAnswer(String questionId, String answerId, Answer answer, String auth, String userId) {
		if(answer.getAnswer_text()==null || answer.getAnswer_text().trim().equals(""))
			throw new InvalidInputException("Answer text is mandatory and missing value");
		qestionService.getQuestion(questionId);
		Optional<Answer> answerData = answersRepository.findById(answerId);
		if(!answerData.isPresent())
			throw new AnswerNotFoundException("Answer ID does not exist");
		Answer answerDB = answerData.get();
		if(answerDB.getUser_id().equals(userId)){
			answerDB.setAnswer_text(answer.getAnswer_text());
			answerDB.setUpdated_timestamp(DateHelper.getTimeZoneDate());
			answersRepository.save(answerDB);
		}else
			throw new UserNotAuthorizedException("Only user who submitted answer can edit answer");
	}
	
	public Answer getAnswer(String questionId, String answerId) {
		Optional<Answer> answerData = answersRepository.findById(answerId);
		if(!answerData.isPresent())
			throw new AnswerNotFoundException("Answer ID does not exist");
		return answerData.get();
	}
	
	public void deleteAnswer(String questionId, String answerId,String auth, String userId) {
		qestionService.getQuestion(questionId);
		Optional<Answer> answer = answersRepository.findById(answerId);
		if(!answer.isPresent())
			throw new AnswerNotFoundException("Answer ID does not exist");
		Answer answerDB = answer.get();
		if(answerDB.getUser_id().equals(userId)) {
		answersRepository.delete(answerDB);
		}
		else
			throw new UserNotAuthorizedException("Only user who submitted answer can delete");
		//Answer answerData = answersRepository.getOne(answerId);
		
	}
}
