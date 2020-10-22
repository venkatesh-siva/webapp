package edu.csye.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.ReadOnlyProperty;

@Entity(name = "answer")
public class Answer {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@ReadOnlyProperty
	private String answer_id;
	
	@ReadOnlyProperty
	private String question_id;
	
	@ReadOnlyProperty
	private String created_timestamp;
	
	@ReadOnlyProperty
	private String updated_timestamp;
	
	@ReadOnlyProperty
	private String user_id;
	
	private String answer_text;
	@OneToMany
	@JoinColumn(name = "answer_id")
	private List<Image> attachments;

	public Answer() {
		attachments = new ArrayList<Image>();
	}

	public List<Image> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Image> attachments) {
		this.attachments = attachments;
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

	public String getCreated_timestamp() {
		return created_timestamp;
	}

	public void setCreated_timestamp(String created_timestamp) {
		this.created_timestamp = created_timestamp;
	}

	public String getUpdated_timestamp() {
		return updated_timestamp;
	}

	public void setUpdated_timestamp(String updated_timestamp) {
		this.updated_timestamp = updated_timestamp;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getAnswer_text() {
		return answer_text;
	}

	public void setAnswer_text(String answer_text) {
		this.answer_text = answer_text;
	}
	
}
