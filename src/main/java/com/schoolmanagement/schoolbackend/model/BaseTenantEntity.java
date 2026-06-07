package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "schoolId", type = Long.class)})
@Filter(name = "tenantFilter", condition = "school_profile_id = :schoolId")
@Getter
@Setter
// Yaha maine <String> isliye likha hai kyunki username usually String hota hai, 
// agar  User object ya Long ID hai , toh yaha <Long> kar skte ho.
public abstract class BaseTenantEntity extends Auditable<String> {
	
	
	@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_profile_id")
    private SchoolProfile schoolProfile;

	
//	Agar hume frontend par sach mein dikhana hai ki ye student kis school ka hai, 
//	toh @JsonIgnore nhi lagana hai. Uski jagah ye annotation lagana hai
//	jisse Jackson proxy ke kachre ko ignore kar dega aur code nahi fatega:
//	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
}