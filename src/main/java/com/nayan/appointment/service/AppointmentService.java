package com.nayan.appointment.service;

import com.nayan.appointment.api.request.GetAppointmentRequest;
import com.nayan.appointment.api.response.GetAppointmentResponse;
import com.nayan.appointment.dto.CreateAppointmentRequest;
import com.nayan.appointment.entity.Appointment;
import com.nayan.appointment.entity.AppointmentStatus;
import com.nayan.appointment.entity.Company;
import com.nayan.appointment.entity.CompanyAppointmentService;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService
{
	List<GetAppointmentResponse> getAppointments(Long companyId, GetAppointmentRequest.ApointmentFilter filter);

	List<Appointment> saveAppointment(CreateAppointmentRequest appointment);

	void updateAppointmentStatus(Long appointmentId, int status);

	List<AppointmentStatus> getAppointmentStatus(Long companyId);

	List<CompanyAppointmentService> getAppointmentServiceInfo(Long companyId);

	Company getCompanyDetails(Long companyId);

	List<Company> getAllCompanyDetails();

	LocalDateTime getNextAppointmentTime(Long companyId);
}
