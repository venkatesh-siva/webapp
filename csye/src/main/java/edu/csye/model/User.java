package edu.csye.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.ReadOnlyProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity(name = "user")
public class User {
	
	@Column(name = "first_name")
	private String first_name;
	
	@Column(name = "last_name")
	private String last_name;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "password")
	private String password;
	
	@Column(name = "username")
	private String username;
	
	@ReadOnlyProperty
	@Column(name = "account_created")
	private String account_created;
	
	@ReadOnlyProperty
	@Column(name = "account_updated")
	private String account_updated;
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@ReadOnlyProperty
	@Column(name = "id")
    private String id;

	public String getId() {
		return id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccount_created() {
		return account_created;
	}

	public void setAccount_created(String account_created) {
		this.account_created = account_created;
	}

	public String getAccount_updated() {
		return account_updated;
	}

	public void setAccount_updated(String account_updated) {
		this.account_updated = account_updated;
	}

}
