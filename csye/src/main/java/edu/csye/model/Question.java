package edu.csye.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.ReadOnlyProperty;

import com.sun.istack.NotNull;

@Entity(name = "question")
public class Question {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@ReadOnlyProperty
	private String question_ID;
	
	@ReadOnlyProperty
	private String created_timestamp;
	
	@ReadOnlyProperty
	private String updated_timeStamp;
	
	@ReadOnlyProperty
	private String user_id;
	
	@ReadOnlyProperty
	@NotNull 
	private String question_text;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "question_ID")
	private List<Answer> answerList;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<Category> categories;

	public Question() {
		answerList = new ArrayList<Answer>();
		categories = new ArrayList<Category>();
	}

	public String getQuestion_ID() {
		return question_ID;
	}

	public void setQuestion_ID(String question_ID) {
		this.question_ID = question_ID;
	}

	public String getCreated_timestamp() {
		return created_timestamp;
	}

	public void setCreated_timestamp(String created_timestamp) {
		this.created_timestamp = created_timestamp;
	}

	public String getUpdated_timeStamp() {
		return updated_timeStamp;
	}

	public void setUpdated_timeStamp(String updated_timeStamp) {
		this.updated_timeStamp = updated_timeStamp;
	}
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getQuestion_text() {
		return question_text;
	}

	public void setQuestion_text(String question_Text) {
		this.question_text = question_Text;
	}

	public List<Answer> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<Answer> answerList) {
		this.answerList = answerList;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

			
}
