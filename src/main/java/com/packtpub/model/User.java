package com.packtpub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {
	
	@Id
	private Integer userid;
	private String username;
	
	public User() {
		
	}
	public User(Integer userid, String username) {
		this.userid = userid;
		this.username = username;
	}
	//getter and setter methods
	public Integer getUserid() {
		return userid;
	}
	public void setUserid(Integer userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}