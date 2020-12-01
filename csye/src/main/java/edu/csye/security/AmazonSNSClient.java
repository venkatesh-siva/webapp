package edu.csye.security;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

@Service("amazonSNSService")
public class AmazonSNSClient {
    private AmazonSNS snsClient;

    private Logger logger = LoggerFactory.getLogger(AmazonSNSClient.class);

    @PostConstruct
    private void init() {
        this.snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_EAST_1)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
    }

    public void publish(String message) {
        final PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-east-1:371394122941:password-reset", message);
        logger.info("AmazonSNSClientClass- Published Request : " + publishRequest.toString() + "----");
        final PublishResult publishResponse = snsClient.publish(publishRequest);
        logger.info("AmazonSNSClientClass- Published message with messageId :- " + publishResponse.getMessageId());
    }
}