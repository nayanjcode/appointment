package com.nayan.appointment.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetAppointmentRequest
{
	Long companyId;
	ApointmentFilter filter;
//	AppointmentSort sort;
	Integer tzOffset;

	@Getter
	@Setter
	public static class ApointmentFilter
	{
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		LocalDate date;
		long epochTime;
		List<Integer> status;
		List<Integer> serviceId;
	}

//	private class AppointmentSort
//	{
//	}
}
