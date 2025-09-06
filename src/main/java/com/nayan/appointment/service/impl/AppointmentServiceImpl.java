package com.nayan.appointment.service.impl;

import com.nayan.appointment.api.request.GetAppointmentRequest;
import com.nayan.appointment.api.response.GetAppointmentResponse;
import com.nayan.appointment.dto.CreateAppointmentRequest;
import com.nayan.appointment.entity.Appointment;
import com.nayan.appointment.entity.AppointmentStatus;
import com.nayan.appointment.entity.Company;
import com.nayan.appointment.entity.CompanyAppointmentService;
import com.nayan.appointment.entity.User;
import com.nayan.appointment.repository.AppointmentRepository;
import com.nayan.appointment.repository.AppointmentStatusRepository;
import com.nayan.appointment.repository.CompanyAppointmentServiceRepository;
import com.nayan.appointment.repository.CompanyRespository;
import com.nayan.appointment.repository.UserRepository;
import com.nayan.appointment.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService
{
	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AppointmentStatusRepository appointmentStatusRepository;

	@Autowired
	private CompanyAppointmentServiceRepository serviceInfoRepository;

	@Autowired
	private CompanyRespository companyRepository;

	@Override
	public List<GetAppointmentResponse> getAppointments(final Long companyId, final GetAppointmentRequest.ApointmentFilter filter)
	{
		final LocalDateTime startOfDay = filter.getDate().atStartOfDay();
		final LocalDateTime endOfDay = filter.getDate().atTime(LocalTime.MAX);
		final List<Integer> status = filter.getStatus() != null && !filter.getStatus().isEmpty() ? filter.getStatus() : List.of(AppointmentStatus.APPOINTMENT_STATUS_PENDING, AppointmentStatus.APPOINTMENT_STATUS_IN_PROGRESS, AppointmentStatus.APPOINTMENT_STATUS_CONFIRM);
		List<Appointment> appointmentList = appointmentRepo.getAppointments(companyId, startOfDay, endOfDay, status);
		List<GetAppointmentResponse> appointmentResponses = new ArrayList<>();
		if (appointmentList != null)
		{
			for (Appointment appointment : appointmentList)
			{
				Optional<User> userOpt = userRepository.findById(appointment.getCustomerId());
				userOpt.ifPresent(user -> appointmentResponses.add(
						GetAppointmentResponse.builder()
								.appointment(appointment)
								.customerDetails(user)
								.build()));
			}
		}
		appointmentResponses.sort((a, b) -> {
			LocalDateTime aDate = a.getAppointment().getAppointmentDate();
			LocalDateTime bDate = b.getAppointment().getAppointmentDate();
			if (aDate.isEqual(bDate))
			{
				return 0;
			} else if (aDate.isAfter(bDate))
			{
				return -1;
			} else
			{
				return 1;
			}
		});
		return appointmentResponses;
	}

	@Override
	public List<Appointment> saveAppointment(final CreateAppointmentRequest appointment)
	{
		if (appointment == null)
		{
			throw new RuntimeException("BAD REQUEST: cannot create empty appointment");
		} else if (appointment.getServiceIds() == null || appointment.getServiceIds().isEmpty())
		{
			throw new RuntimeException("You need to opt for at least one service to book an appointment");
		} else if ((appointment.getEmail() == null || appointment.getEmail().isBlank()) && (appointment.getPhone() == null || appointment.getPhone().isBlank()))
		{
			throw new RuntimeException("You must have at least one contact details of the user");
		}
		// check user from mobile and email. If does not exist then create else fetch the existing.
		final User user = userRepository.findUserForCompany(
				appointment.getCompanyId(),
				appointment.getEmail(),
				appointment.getPhone()
		).orElseGet(() -> userRepository.save(
				User.builder()
						.companyId(appointment.getCompanyId())
						.userType(1)
						.contactNumber(appointment.getPhone())
						.emailId(appointment.getEmail())
						.firstName(appointment.getCustomerFirstName())
						.lastName(appointment.getCustomerLastName())
						.createDate(LocalDateTime.now())
						.build()
		));
		// save appointment
		final List<Appointment> createdAppointments = new ArrayList<>();
		for (Long serviceId : appointment.getServiceIds())
		{
			LocalDateTime appointmentDateTime = getNextAppointmentTime(appointment.getCompanyId(), appointment.getTzOffset());
			Appointment appointmentToSave = Appointment.builder()
					.companyId(appointment.getCompanyId())
					.customerId(user.getUserId())
					.appointmentDate(appointmentDateTime)
//						.appointmentDate(LocalDateTime.parse(appointment.getAppointmentDateTime()))
					.serviceId(serviceId)
					.statusId(AppointmentStatus.APPOINTMENT_STATUS_PENDING)
					.createDate(LocalDateTime.now())
					.build();
			Appointment savedAppointment = appointmentRepo.saveAndFlush(appointmentToSave);
			createdAppointments.add(savedAppointment);
		}

		return createdAppointments;
	}

	@Transactional
	@Override
	public void updateAppointmentStatus(Long appointmentId, int status)
	{
		if (!appointmentRepo.existsById(appointmentId))
			throw new RuntimeException("Appointment you want to update does not exist");
		appointmentRepo.updateAppointmentStatus(appointmentId, status);
	}

	@Override
	public List<AppointmentStatus> getAppointmentStatus(Long companyId)
	{
		return appointmentStatusRepository.findAppointmentStatusByCompanyId(companyId);
	}

	@Override
	public List<CompanyAppointmentService> getAppointmentServiceInfo(Long companyId)
	{
		return serviceInfoRepository.findCompanyAppointmentServiceByCompanyId(companyId);
	}

	@Override
	public Company getCompanyDetails(Long companyId)
	{
		return companyRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found"));
	}

	@Override
	public List<Company> getAllCompanyDetails()
	{
		return companyRepository.findAll();
	}

	@Override
	public LocalDateTime getNextAppointmentTime(Long companyId, int tzOffset)
	{
		// Suppose you have timezone offset in minutes, e.g., +330 for IST
		int offsetMinutes = 330;
		ZoneOffset offset = ZoneOffset.ofTotalSeconds(tzOffset);

		// Current date in that offset
		LocalDate today = LocalDate.now(offset);

		// Start of day in that offset
		LocalDateTime startOfDay = today.atStartOfDay().atOffset(offset).toLocalDateTime();

		// End of day in that offset
		LocalDateTime endOfDay = today.atTime(LocalTime.MAX).atOffset(offset).toLocalDateTime();

		List<Integer> excludedStatuses = List.of(AppointmentStatus.APPOINTMENT_STATUS_CANCELLED, AppointmentStatus.APPOINTMENT_STATUS_COMPLETED);
		Optional<Appointment> latestAppointmentOpt = appointmentRepo.findTopByCompanyIdAndStatusIdNotInAndAppointmentDateBetweenOrderByAppointmentDateDesc(companyId, excludedStatuses, startOfDay, endOfDay);

		if (latestAppointmentOpt.isPresent())
		{
			Appointment latest = latestAppointmentOpt.get();
			CompanyAppointmentService service = serviceInfoRepository.findById(latest.getServiceId())
					.orElseThrow(() -> new RuntimeException("Service not found"));

			LocalDateTime nextSlot = latest.getAppointmentDate()
					.plusMinutes(service.getServiceDuration());
			return roundUpToNearest5Minutes(nextSlot);
		} else
		{
			return roundUpToNearest5Minutes(LocalDateTime.now());
		}
	}

	private LocalDateTime roundUpToNearest5Minutes(LocalDateTime time)
	{
		int minute = time.getMinute();
		int remainder = minute % 5;
		if (remainder == 0)
		{
			return time.truncatedTo(ChronoUnit.MINUTES);
		} else
		{
			return time.plusMinutes(5 - remainder).truncatedTo(ChronoUnit.MINUTES);
		}
	}


}
