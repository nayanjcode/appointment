package com.nayan.appointment.repository;

import com.nayan.appointment.entity.AppointmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentStatusHistoryRepository extends JpaRepository<AppointmentStatusHistory, Long>
{
	AppointmentStatusHistory findTopByAppointmentAppointmentIdOrderByChangedAtDesc(Long appointmentId);
}
