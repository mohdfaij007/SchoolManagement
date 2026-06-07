package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "standards", uniqueConstraints = {
	    // Ab Grade Name sirf ek particular school ke andar unique rahega
	    @UniqueConstraint(columnNames = {"gradeName", "school_profile_id"})
	})
public class Standard extends BaseTenantEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String gradeName; // Example: "Class 1", "LKG", "10th"

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public Long getId() {
		return id;
	}
}