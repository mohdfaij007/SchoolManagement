package com.schoolmanagement.schoolbackend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class JwtResponse {

	
	public JwtResponse(String token, String username, String role) {
		super();
		this.token = token;
		this.username = username;
		this.role = role;
	}
	private String token;
    private String username;
    public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	private String role;
}
