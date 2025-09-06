package com.nayan.appointment.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateAppointmentRequest
{
	private Long companyId;
	private List<Long> serviceIds;
//	private String appointmentDateTime; // YYYY-MM-DD
	private String customerFirstName;
	private String customerLastName;
	private String email;
	private String phone;
	private int tzOffset;
}
