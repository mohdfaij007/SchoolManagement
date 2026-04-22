package com.schoolmanagement.schoolbackend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class JwtResponse {

	
	private String token;
    private String username;
	private String role;
	private Long schoolId;
}
