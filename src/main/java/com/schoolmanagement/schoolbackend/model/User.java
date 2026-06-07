package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTenantEntity{	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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

	@Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Will be hashed by Spring Security

    // We can use a simple String role for now
    private String role; // e.g., "ADMIN", "TEACHER", "STUDENT"

  
}
