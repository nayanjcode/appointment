package com.nayan.appointment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AppointmentStatus
{
	public static final int APPOINTMENT_STATUS_PENDING = 1;
	public static final int APPOINTMENT_STATUS_CONFIRM = 2;
	public static final int APPOINTMENT_STATUS_CANCELLED = 3;
	public static final int APPOINTMENT_STATUS_COMPLETED = 4;
	public static final int APPOINTMENT_STATUS_IN_PROGRESS = 5;
	@Id
	private Integer statusId;
	private String statusName;
	private Long companyId;
}
