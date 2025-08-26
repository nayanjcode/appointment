package com.nayan.appointment.controller;

import com.nayan.appointment.api.request.GetAppointmentRequest;
import com.nayan.appointment.api.request.UpdateAppointmentStatus;
import com.nayan.appointment.api.response.GetAppointmentResponse;
import com.nayan.appointment.dto.CreateAppointmentRequest;
import com.nayan.appointment.entity.Appointment;
import com.nayan.appointment.entity.AppointmentStatus;
import com.nayan.appointment.entity.Company;
import com.nayan.appointment.entity.CompanyAppointmentService;
import com.nayan.appointment.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController
{
	@Autowired
	AppointmentService appointmentService;

	@PostMapping("/getAppointments")
	public ResponseEntity<List<GetAppointmentResponse>> getAppointments(@RequestBody GetAppointmentRequest appointmentRequest)
	{
		final List<GetAppointmentResponse> appointments = appointmentService.getAppointments(appointmentRequest.getCompanyId(), appointmentRequest.getFilter());
		return ResponseEntity.ok(appointments);
	}

	@PostMapping("/bookAppointment")
	public ResponseEntity<List<Appointment>> saveAppointment(@RequestBody CreateAppointmentRequest appointment)
	{
		final List<Appointment> updatedAppointments = appointmentService.saveAppointment(appointment);
		return ResponseEntity.status(HttpStatus.CREATED).body(updatedAppointments);
	}


	@PostMapping("/updateStatus")
	public ResponseEntity<?> updateAppoitmentStatus(@RequestBody UpdateAppointmentStatus updateAppointmentStatus)
	{
		appointmentService.updateAppointmentStatus(updateAppointmentStatus.getAppointmentId(), updateAppointmentStatus.getStatusId());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/allStatusInfo")
	public ResponseEntity<List<AppointmentStatus>> getStatusInfoOfCompany(@RequestParam("companyId") Long companyId)
	{
		List<AppointmentStatus> appointmentStatusInfo = appointmentService.getAppointmentStatus(companyId);
		return ResponseEntity.ok(appointmentStatusInfo);
	}

	@GetMapping("/allServiceInfo")
	public ResponseEntity<List<CompanyAppointmentService>> getServiceInfoOfCompany(@RequestParam("companyId") Long companyId)
	{
		List<CompanyAppointmentService> servicesInfo = appointmentService.getAppointmentServiceInfo(companyId);
		return ResponseEntity.ok(servicesInfo);
	}

	@GetMapping("/companyDetails")
	public ResponseEntity<List<Company>> getCompanyDetails(@RequestParam("companyId") Long companyId)
	{
		if (companyId != null)
		{
			Company company = appointmentService.getCompanyDetails(companyId);
			return ResponseEntity.ok(List.of(company));
		} else
		{
			return ResponseEntity.ok(appointmentService.getAllCompanyDetails());
		}
	}

	@GetMapping("/findNextAppointmentTime")
	public ResponseEntity<LocalDateTime> getNextAppointmentTime(@RequestParam("companyId") Long companyId)
	{
		LocalDateTime nextAppointmentSlot = appointmentService.getNextAppointmentTime(companyId);
		return ResponseEntity.ok(nextAppointmentSlot);
	}
}
