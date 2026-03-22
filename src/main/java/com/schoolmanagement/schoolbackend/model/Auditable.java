package com.schoolmanagement.schoolbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass // Means this class is not a table, but its fields will be added to child tables
@EntityListeners(AuditingEntityListener.class) // This listener automatically populates the fields
@Getter
@Setter
public abstract class Auditable<U> {

    @CreatedBy // Automatically grabs the current logged-in user
    @Column(updatable = false) // "Created By" should never change once set
    private U createdBy;

    @CreatedDate // Automatically sets the timestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy // Updates this if someone edits the record later
    private U lastModifiedBy;

    @LastModifiedDate // Updates timestamp on edit
    private LocalDateTime lastModifiedDate;
}