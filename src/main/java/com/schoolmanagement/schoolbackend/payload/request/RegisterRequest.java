package com.schoolmanagement.schoolbackend.payload.request;

import lombok.Data;
@Data // Lombok for getters/setters
public class RegisterRequest {
    private String username;
    private String password;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	private String role; // e.g., "ADMIN" or "TEACHER"
}