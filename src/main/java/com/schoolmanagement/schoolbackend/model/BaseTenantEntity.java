package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.schoolmanagement.schoolbackend.security.tenant.TenantContext;

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
	
	
	
	// Yeh method database me INSERT hone se theek pehle chalega
    @PrePersist
    public void prePersistTenant() {
        if (this.schoolProfile == null) {
            Long currentTenantId = TenantContext.getCurrentTenant();
            
            // Agar ThreadLocal me ID hai, toh dummy SchoolProfile banakar auto-set kar do
            // Super Admin ko handle karne ke liye null check lagaya hai
            if (currentTenantId != null) {
                SchoolProfile profile = new SchoolProfile();
                profile.setId(currentTenantId);
                this.schoolProfile = profile;
            }
        }
    }
}