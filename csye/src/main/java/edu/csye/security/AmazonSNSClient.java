package edu.csye.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;



@Service("amazonSNSService")
public class AmazonSNSClient {
	//@Value("${AWS_REGION}")
	//private String region;
	private AmazonSNS snsClient;
	private CreateTopicResult topic;
	private final static Logger logger = LoggerFactory.getLogger(AmazonSNSClient.class);
	String topicArn;
	@Autowired
	public AmazonSNSClient(){
		
	    InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider(true);
	    this.snsClient =  AmazonSNSClientBuilder.standard().withCredentials(provider).withRegion("us-east-1").build();
	    this.topic = snsClient.createTopic("TOPIC_EMAIL");
	    topicArn=topic.getTopicArn();
	}

    public void publish(String message) {
        final PublishRequest publishRequest = new PublishRequest(topicArn, message);
        logger.info("AmazonSNSClientClass- Published Request : " + publishRequest.toString() + "----");
        final PublishResult publishResponse = snsClient.publish(publishRequest);
        logger.info("AmazonSNSClientClass- Published message with messageId :- " + publishResponse.getMessageId());
    }
}