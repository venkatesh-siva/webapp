package edu.csye.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.glue.model.EntityNotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.timgroup.statsd.StatsDClient;

import edu.csye.exception.AnswerNotFoundException;
import edu.csye.exception.ImageExistException;
import edu.csye.exception.ImageNotFoundException;
import edu.csye.exception.QuestionNotFoundException;
import edu.csye.exception.UnsupportedImageException;
import edu.csye.exception.UserNotAuthorizedException;
import edu.csye.helper.DateHelper;
import edu.csye.model.Answer;
import edu.csye.model.Image;
import edu.csye.model.Question;
import edu.csye.repository.ImageRepository;


@Service
public class ImageService {
	
	//@Value("${aws.accesskey}")
	//String access_key;
	
	//@Value("${aws.secretkey}")
	//String secret_key;
    
    @Value("${aws.s3.bucketname}")
    String bucketName;

    //@Value("${aws.s3.endpointURL}")
    //String endpointUrl;

    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private QuestionsService questionService;
    
    @Autowired
    private AnswerService answerService;
    
    @Autowired
    private AmazonS3 s3Client; 
    
    private Logger logger = LoggerFactory.getLogger(ImageService.class);
	
	@Autowired
    private StatsDClient stasDClient;
    
    //@PostConstruct
    //private void initializeAmazon() {
    //	s3Client = new AmazonS3Client(new BasicAWSCredentials(access_key, secret_key));
    //}
    

    public void uploadFile(MultipartFile multipartFile, String questionId, String answerId, String userId) {
    	logger.info("Info:Calling uploadFileService");
		long begin = System.currentTimeMillis();
		
    	Answer answer = null ;
    	Question question = null;
    	if(answerId!=null)
    		answer = answerService.getAnswer(questionId, answerId);
    	else
    		question = questionService.getQuestion(questionId);
        //String fileURl = "";
    	if(answer!=null && !answer.getUser_id().equals(userId))
    		throw new UserNotAuthorizedException("Can't post image to other user's answer");
    	if(question !=null && !question.getUser_id().contentEquals(userId))
    		throw new UserNotAuthorizedException("Can't post image to other user's question");
        Image image = new Image();
        try {
            //File file = convertMultipartToFile(multipartFile);
        	
            String s3Object ;
            if(answerId==null)
            	s3Object= questionId+"/"+multipartFile.getOriginalFilename().replace(" ", "_");
            else
            	s3Object= questionId+"/"+answerId+"/"+multipartFile.getOriginalFilename().replace(" ", "_");
            //PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3Object, file);
            //s3Client.putObject(putObjectRequest);
            Image imageDB = imageRepository.findByS3ObjectName(s3Object);
            ObjectMetadata data = new ObjectMetadata();
            data.setContentType(multipartFile.getContentType());
            data.setContentLength(multipartFile.getSize());
            if(imageDB!=null)
            	throw new ImageExistException("Please provide a different image name");
            Tika tika = new Tika();
            String detectedType = tika.detect(multipartFile.getBytes());
            if(!"image/jpeg".equalsIgnoreCase(detectedType) && !"image/png".equalsIgnoreCase(detectedType) && !"image/jpg".equalsIgnoreCase(detectedType))
            	 throw new UnsupportedImageException("Please upload jpeg/jpg/png images");
            
            long beginS3 = System.currentTimeMillis();
            s3Client.putObject(new PutObjectRequest(bucketName, s3Object, multipartFile.getInputStream(), data).withCannedAcl(CannedAccessControlList.PublicRead));
            long end = System.currentTimeMillis();
            long timeTakenS3 = end - beginS3;
            logger.info("Time taken by s3 web call upload" + timeTakenS3 + "ms");
            stasDClient.recordExecutionTime("uploadS3Time", timeTakenS3);
            //s3Client.putObject(new PutObjectRequest(bucketName, s3Object, file).withCannedAcl(CannedAccessControlList.PublicRead));
            //image.setIsbn(bookISBN);
            //image.setUserEmail(userEmail);
            image.setS3ObjectName(s3Object);
            image.setFileName(multipartFile.getOriginalFilename());
            image.setCreated_date(DateHelper.getTimeZoneDate());
            if(answerId==null)
            	image.setQuestion_id(questionId);
            if(answerId!=null)
            	image.setAnswer_id(answerId);
            long beginDB = System.currentTimeMillis();
            imageRepository.save(image);
            end = System.currentTimeMillis();
            long timeTaken = end - beginDB;
            logger.info("TIme taken by uploadFileDB " + timeTaken + "ms");
            stasDClient.recordExecutionTime("uploadFileDBTime", timeTaken);
        } catch (IOException e) {
            throw new UnsupportedImageException("Unable to read file");
        }
        long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by uploadFileService " + timeTaken + "ms");
        stasDClient.recordExecutionTime("uploadFileServiceTime", timeTaken);
    }

    private File convertMultipartToFile(MultipartFile file) throws IOException {
        File convertedfile = new File(file.getOriginalFilename());
        FileOutputStream fileOutputStream = new FileOutputStream(convertedfile);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
        return convertedfile;
    }


     public String deleteFile(String questionId, String answerId, String fieldId, String userId) {
    	 logger.info("Info:Calling deleteFileService");
 		long begin = System.currentTimeMillis();
 		
    	 Image image;
    	 Answer answer = null ;
     	Question question = null;
     	if(userId!=null && answerId!=null)
     		answer = answerService.getAnswer(questionId, answerId);
     	else if(userId!=null)
     		question = questionService.getQuestion(questionId);
         //String fileURl = "";
     	if(userId!=null && answer!=null && !answer.getUser_id().equals(userId))
     		throw new UserNotAuthorizedException("Can't delete image posted by other user's");
     	if(userId!=null && question !=null && !question.getUser_id().contentEquals(userId))
     		throw new UserNotAuthorizedException("Can't delete image posted by other user's");
     	
    	 if(fieldId == null || fieldId.trim().equals(""))
    		 throw new ImageNotFoundException("Field cannot be empty");
    	 Optional<Image> optionalimage = imageRepository.findById(fieldId);
    	 if(!optionalimage.isPresent())
    		 throw new ImageNotFoundException("Provided fieldId not found in the database");
    	 else
    		 image = optionalimage.get();
    	 if(answerId==null && !questionId.equals(image.getQuestion_id()))
    		 throw new QuestionNotFoundException("Question Id and the field ID provided does not match");
    	 if(answerId!=null)
    		 answerService.getAnswer(questionId, answerId);
    	 if(answerId!=null && !answerId.equals(image.getAnswer_id()))
    		 throw new AnswerNotFoundException("Answer Id and the filed Id provided does not match");
    	 String name = image.getS3ObjectName();
        try{
        	S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, name));
        	if(s3object != null) {
        		long beginS3 = System.currentTimeMillis();
        		s3Client.deleteObject(new DeleteObjectRequest(bucketName, name));
        		long end = System.currentTimeMillis();
                long timeTakenS3 = end - beginS3;
                logger.info("Time taken by s3 web call delete" + timeTakenS3 + "ms");
                stasDClient.recordExecutionTime("deleteS3Time", timeTakenS3);
        		long beginDB = System.currentTimeMillis();
        		imageRepository.delete(image);
        		end = System.currentTimeMillis();
                long timeTaken = end - beginDB;
                logger.info("TIme taken by deleteFileDB " + timeTaken + "ms");
                stasDClient.recordExecutionTime("deleteFileDBTime", timeTaken);
        		return "SUCCESS";
        	}else {
                throw new ImageNotFoundException("Image not found!!!");
            }
        	
        } catch (AmazonS3Exception e) {
            System.err.println(e.getMessage());
        }catch(EntityNotFoundException e) {
        	throw new ImageNotFoundException("Provided fieldId not found in the database");
        }
        long end = System.currentTimeMillis();
        long timeTaken = end - begin;
        logger.info("TIme taken by deleteFileService " + timeTaken + "ms");
        stasDClient.recordExecutionTime("deleteFileServiceTime", timeTaken);
        return null;
     }
}
