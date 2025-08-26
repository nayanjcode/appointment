package com.nayan.appointment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User
{
	@Id
	@GeneratedValue
	private Long userId;
	private int userType;
	private String firstName;
	private String lastName;
	private String contactNumber;
	private String emailId;
	private LocalDateTime createDate;
	@UpdateTimestamp
	private LocalDateTime updateDate;
	private Long companyId;
}
