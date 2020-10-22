package edu.csye.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

@Configuration
public class AWSConfig {

	@Bean
	public AmazonS3 amazonS3() {
	    return new AmazonS3Client();
	}
}
