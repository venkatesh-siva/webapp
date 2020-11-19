package edu.csye.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.timgroup.statsd.StatsDClient;

import edu.csye.exception.InvalidInputException;
import edu.csye.exception.QuestionNotFoundException;
import edu.csye.exception.UserNotAuthorizedException;
import edu.csye.helper.Base64Helper;
import edu.csye.helper.CategoryNameValidatorHelper;
import edu.csye.helper.DateHelper;
import edu.csye.model.Category;
import edu.csye.model.Image;
import edu.csye.model.Question;
import edu.csye.model.User;
import edu.csye.repository.CategoryRepository;
import edu.csye.repository.QuestionRepository;

@Service
public class QuestionsService {

	@Autowired
	QuestionRepository questionRepository;
	
	@Autowired
	MyUserDetailService myUserDetailService;
	
	@Autowired
	CategoryRepository  categoryRepo;
	
	@Autowired
	ImageService imageService;
	
	private Logger logger = LoggerFactory.getLogger(QuestionsService.class);

	@Autowired
    private StatsDClient stasDClient;
	
	public Question createQuestion(Question question, String auth, String userId) throws UnsupportedEncodingException {
		logger.info("Info:Calling createQuestionService");
		long begin = System.currentTimeMillis();
		
		if(question.getQuestion_text()==null || question.getQuestion_text().trim().equals(""))
			throw new InvalidInputException("Questoin text is mandatory and missing");
		
		String userName = Base64Helper.getUserName(Base64Helper.convertToSting(auth));
		
		//User userdata = myUserDetailService.fetchUser(userName);
		
		
		question.setUser_id(userId);
		
		List<Category> catList = new ArrayList<Category>();
		List<String> categories = new ArrayList<String>();
		if(question.getCategories()!=null && question.getCategories().size()>0) {
			
		for(Category cat: question.getCategories()) {
			if(CategoryNameValidatorHelper.validateCategoryName(cat.getCategory()))
				throw new InvalidInputException("Category name cannot have special character");
			String catLow = cat.getCategory().toLowerCase().trim();
			if(catLow.equals(""))
				throw new InvalidInputException("category value is missing");
			if(!categories.contains(catLow))
			{
				Category fromDb = categoryRepo.findByCategory(catLow);
				if(fromDb == null) {
					cat.setCategory(catLow);
					fromDb = cat;
					try {
					fromDb = categoryRepo.save(fromDb);
					}catch(Exception e) {
						fromDb = categoryRepo.findByCategory(catLow);
					}
				}
				categories.add(catLow);
				catList.add(fromDb);
			}
		}
		
		question.setCategories(null);
		
		question.setCategories(catList);

		}
		String time = DateHelper.getTimeZoneDate();
		
		question.setCreated_timestamp(time);
		
		question.setUpdated_timeStamp(time);
		
		long beginDB = System.currentTimeMillis();
		
		questionRepository.save(question);
		
		long end = System.currentTimeMillis();
		long timeTakenDB = end - beginDB;
		logger.info("TIme taken by createQuestionDB " + timeTakenDB + "ms");
        stasDClient.recordExecutionTime("createQuestionDBTime", timeTakenDB);
        
		long timeTaken = end - begin;
        logger.info("TIme taken by createQuestionService " + timeTaken + "ms");
        stasDClient.recordExecutionTime("createQuestionServiceTime", timeTaken);
		return question;
	}
	
	public Question getQuestion(String questionId) {
		logger.info("Info:Calling getQuestionService");
		long begin = System.currentTimeMillis();
		long beginDB = System.currentTimeMillis();
		Optional<Question> questionData = questionRepository.findById(questionId);
		long end = System.currentTimeMillis();
		long timeTakenDB = end - beginDB;
		logger.info("TIme taken by getQuestionDB " + timeTakenDB + "ms");
        stasDClient.recordExecutionTime("getQuestionDBTime", timeTakenDB);
		if(!questionData.isPresent())
			throw new QuestionNotFoundException("Question ID does not exist");
		
		end = System.currentTimeMillis();
		long timeTaken = end - begin;
        logger.info("TIme taken by getQuestionService " + timeTaken + "ms");
        stasDClient.recordExecutionTime("getQuestionServiceTime", timeTaken);
		return questionData.get();
	}
	
	public List<Question> getQuestions() {
		logger.info("Info:Calling getQuestionsService");
		long begin = System.currentTimeMillis();
		
		List<Question> answerList = (List<Question>) questionRepository.findAll();
		
		long end = System.currentTimeMillis();
		long timeTaken = end - begin;
        logger.info("TIme taken by getQuestionsDB " + timeTaken + "ms");
        stasDClient.recordExecutionTime("getQuestionsDBTime", timeTaken);
        logger.info("TIme taken by getQuestionsService " + timeTaken + "ms");
        stasDClient.recordExecutionTime("getQuestionsServiceTime", timeTaken);
		return answerList;
	}
	
	public void deleteQuestion(String questionId, String auth, String UserId) throws UnsupportedEncodingException {
		logger.info("Info:Calling deleteQuestionService");
		long begin = System.currentTimeMillis();
		
		User user = myUserDetailService.fetchUser(Base64Helper.getUserName(Base64Helper.convertToSting(auth)));
		Optional<Question> question = questionRepository.findById(questionId);
		if(!question.isPresent())
			throw new QuestionNotFoundException("Questoin ID does not exist");
		Question questonDb = question.get();
		if(questonDb.getAnswerList().size()>0)
			throw new InvalidInputException("Cannot delete question as it has answers");
		if(questonDb.getUser_id().equals(UserId)) {
			List<Image> attachments = questonDb.getAttachments();
			for(Image image :attachments) {
				imageService.deleteFile(questionId, null, image.getFile_id(),null);
			}
			long beginDB = System.currentTimeMillis();
			questionRepository.delete(questonDb);
			long end = System.currentTimeMillis();
			long timeTakenDB = end - beginDB;
			logger.info("TIme taken by deleteQuestionDB " + timeTakenDB + "ms");
	        stasDClient.recordExecutionTime("deleteQuestionDBTime", timeTakenDB);
		}
		else
			throw new UserNotAuthorizedException("Sorry, question is creatied by another user, cannot delete the user with the given credentials");
		
		long end = System.currentTimeMillis();
		long timeTaken = end - begin;
        logger.info("TIme taken by deleteQuestionService " + timeTaken + "ms");
        stasDClient.recordExecutionTime("deleteQuestionServiceTime", timeTaken);
		
	}
	
	public void updateQuestion(String questionId, Question question, String auth, String UserId) throws UnsupportedEncodingException {
		logger.info("Info:Calling updateQuestionService");
		long begin = System.currentTimeMillis();
		
		//User user = myUserDetailService.fetchUser(Base64Helper.getUserName(Base64Helper.convertToSting(auth)));
		if(question.getQuestion_text()==null || question.getQuestion_text().trim().equals(""))
			throw new InvalidInputException("Questoin text is mandatory and missing");
		Optional<Question> questionData = questionRepository.findById(questionId);
		if(!questionData.isPresent())
			throw new QuestionNotFoundException("Question ID does not exist");
		Question questionDB = questionData.get();
		if(!questionDB.getUser_id().equals(UserId))
			throw new UserNotAuthorizedException("Sorry, question is created by another user, cannot update the user with the given credentials");
		
		//Question questionData = questionRepository.findById(questionId).get();
		
		//question.setUser_id(UserId);
		if(question.getQuestion_text()!=null && !question.getQuestion_text().trim().equals("") && !question.getQuestion_text().equals(questionDB.getQuestion_text()))
			questionDB.setQuestion_text(question.getQuestion_text());
		
		List<Category> catList = new ArrayList<Category>();
		List<String> categories = new ArrayList<String>();
		if(question.getCategories()!=null && question.getCategories().size()>0) {
		for(Category cat: question.getCategories()) {
			if(CategoryNameValidatorHelper.validateCategoryName(cat.getCategory()))
				throw new InvalidInputException("Category name cannot have special character");
			String catLow = cat.getCategory().toLowerCase().trim();
			if(catLow.equals(""))
				throw new InvalidInputException("category value is missing");
			if(!categories.contains(catLow))
			{
				Category fromDb = categoryRepo.findByCategory(catLow);
				if(fromDb == null) {
					cat.setCategory(catLow);
					fromDb = cat;
					fromDb = categoryRepo.save(fromDb);
				}
				categories.add(catLow);
				catList.add(fromDb);
			}
		}
		}
		
		questionDB.setCategories(null);
		
		questionDB.setCategories(catList);
		
		questionDB.setUpdated_timeStamp(DateHelper.getTimeZoneDate());
		long beginDB = System.currentTimeMillis();
		questionRepository.save(questionDB);
		
		long end = System.currentTimeMillis();
		long timeTakenDB = end - beginDB;
		logger.info("TIme taken by updateQuestionDB " + timeTakenDB + "ms");
        stasDClient.recordExecutionTime("updateQuestionDBTime", timeTakenDB);
		long timeTaken = end - begin;
        logger.info("TIme taken by updateQuestionService " + timeTaken + "ms");
        stasDClient.recordExecutionTime("updateQuestionServiceTime", timeTaken);
		
	}
}
