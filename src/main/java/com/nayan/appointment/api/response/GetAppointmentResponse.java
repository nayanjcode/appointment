package com.nayan.appointment.api.response;

import com.nayan.appointment.entity.Appointment;
import com.nayan.appointment.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetAppointmentResponse
{
	Appointment appointment;
	User customerDetails;
}
