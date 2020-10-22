package edu.csye.repository;

import org.springframework.data.repository.CrudRepository;

import edu.csye.model.Image;

public interface ImageRepository extends CrudRepository<Image, String> {
	
	Image findByS3ObjectName(String s3ObjectName);
}
