package com.nayan.appointment.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentStatus
{
	private Long appointmentId;
	private Integer statusId;
}
