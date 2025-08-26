package com.nayan.appointment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Appointment
{
	@Id
	@GeneratedValue
	Long appointmentId;
	Long companyId;
	Long serviceId;
	Long customerId;
	int statusId;
	LocalDateTime appointmentDate;
	LocalDateTime createDate;
	@UpdateTimestamp
	LocalDateTime updateDate;
}
