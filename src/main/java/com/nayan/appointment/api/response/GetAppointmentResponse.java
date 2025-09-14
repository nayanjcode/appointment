package com.nayan.appointment.api.response;

import com.nayan.appointment.entity.Appointment;
import com.nayan.appointment.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Comparator;

@Builder
@Getter
public class GetAppointmentResponse
{
	Appointment appointment;
	User customerDetails;

	public static final Comparator<GetAppointmentResponse> BY_APPOINTMENT_DATE_DESC =
			(a, b) -> {
				Instant aDate = a.getAppointment().getAppointmentDate();
				Instant bDate = b.getAppointment().getAppointmentDate();

				if (aDate.equals(bDate))
				{
					return 0;
				} else if (aDate.isAfter(bDate))
				{
					return -1; // latest first
				} else
				{
					return 1;
				}
			};
}
