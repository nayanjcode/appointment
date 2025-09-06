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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
	public List<GetAppointmentResponse> getAppointments(final GetAppointmentRequest request)
	{
		final Long companyId = request.getCompanyId();
		final GetAppointmentRequest.ApointmentFilter filter = request.getFilter();

		int tzOffset = -request.getTzOffset();
		final ZoneOffset offset = ZoneOffset.ofTotalSeconds(tzOffset);
		// Current date in that offset
		final LocalDate today = filter.getDate();
		// Start of day in that offset
		final Instant startOfDay = today.atStartOfDay().toInstant(offset);
		final Instant endOfDay = today.atTime(LocalTime.MAX).toInstant(offset);
		final List<Integer> status = filter.getStatus() != null && !filter.getStatus().isEmpty() ? filter.getStatus() : List.of(AppointmentStatus.APPOINTMENT_STATUS_PENDING, AppointmentStatus.APPOINTMENT_STATUS_IN_PROGRESS, AppointmentStatus.APPOINTMENT_STATUS_CONFIRM);
		final List<Appointment> appointmentList = appointmentRepo.getAppointments(companyId, startOfDay, endOfDay, status);
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
			Instant aDate = a.getAppointment().getAppointmentDate();
			Instant bDate = b.getAppointment().getAppointmentDate();
			if (aDate.equals(bDate))
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
						.createDate(Instant.now())
						.build()
		));
		// save appointment
		final List<Appointment> createdAppointments = new ArrayList<>();
		for (Long serviceId : appointment.getServiceIds())
		{
			Instant appointmentDateTime = getNextAppointmentTime(appointment.getCompanyId(), appointment.getTzOffset());
			Appointment appointmentToSave = Appointment.builder()
					.companyId(appointment.getCompanyId())
					.customerId(user.getUserId())
					.appointmentDate(appointmentDateTime)
//						.appointmentDate(LocalDateTime.parse(appointment.getAppointmentDateTime()))
					.serviceId(serviceId)
					.statusId(AppointmentStatus.APPOINTMENT_STATUS_PENDING)
					.createDate(Instant.now())
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
	public Instant getNextAppointmentTime(Long companyId, int tzOffset)
	{
		// Suppose you have timezone offset in minutes, e.g., +330 for IST
//		int tzOffset = 330;
		ZoneOffset offset = ZoneOffset.ofTotalSeconds(tzOffset);

		// Current date in that offset
		LocalDate today = LocalDate.now(offset);

		// Start of day in that offset
		Instant startOfDay = today.atStartOfDay().toInstant(offset);

		// End of day in that offset
		Instant endOfDay = today.atTime(LocalTime.MAX).toInstant(offset);

		List<Integer> excludedStatuses = List.of(AppointmentStatus.APPOINTMENT_STATUS_CANCELLED, AppointmentStatus.APPOINTMENT_STATUS_COMPLETED);
		Optional<Appointment> latestAppointmentOpt = appointmentRepo.findTopByCompanyIdAndStatusIdNotInAndAppointmentDateBetweenOrderByAppointmentDateDesc(companyId, excludedStatuses, startOfDay, endOfDay);

		if (latestAppointmentOpt.isPresent())
		{
			Appointment latest = latestAppointmentOpt.get();
			CompanyAppointmentService service = serviceInfoRepository.findById(latest.getServiceId())
					.orElseThrow(() -> new RuntimeException("Service not found"));

			Instant nextSlot = latest.getAppointmentDate()
					.plus(service.getServiceDuration(), ChronoUnit.MINUTES);
			return roundUpToNearest5Minutes(nextSlot);
		} else
		{
			return roundUpToNearest5Minutes(Instant.now());
		}
	}

	private Instant roundUpToNearest5Minutes(Instant time)
	{
		// Convert to ZonedDateTime in desired zone
		ZonedDateTime zdt = time.atZone(ZoneOffset.UTC);

		int minute = zdt.getMinute();
		int remainder = minute % 5;
		if (remainder != 0) {
			// Add the missing minutes to reach the next multiple of 5
			zdt = zdt.plusMinutes(5 - remainder);
		}

		// Truncate smaller units (seconds, nanos)
		zdt = zdt.truncatedTo(ChronoUnit.MINUTES);

		return zdt.toInstant();
	}


}
