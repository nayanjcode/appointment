package com.nayan.appointment.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long appointmentId;
	Long companyId;
	Long serviceId;
	Long customerId;
	@Transient
	int statusId;
	Instant appointmentDate;
	Instant createDate;
	@UpdateTimestamp
	Instant updateDate;

	@OneToMany(mappedBy = "appointment",
			cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
			fetch = FetchType.LAZY)
	List<AppointmentStatusHistory> statusHistoryList = new ArrayList<>();

	@Transient
	long appointmentDateEpoch;
}
