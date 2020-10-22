package edu.csye.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.ReadOnlyProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(name = "file")
public class Image {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@ReadOnlyProperty
	private String file_id;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ReadOnlyProperty
	private String answer_id;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ReadOnlyProperty
	private String question_id;
	
    private String s3ObjectName;
    
    private String created_date;
    
    private String fileName;
    
	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public String getAnswer_id() {
		return answer_id;
	}

	public void setAnswer_id(String answer_id) {
		this.answer_id = answer_id;
	}

	public String getQuestion_id() {
		return question_id;
	}

	public void setQuestion_id(String question_id) {
		this.question_id = question_id;
	}

	public String getS3ObjectName() {
		return s3ObjectName;
	}

	public void setS3ObjectName(String s3ObjectName) {
		this.s3ObjectName = s3ObjectName;
	}

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
