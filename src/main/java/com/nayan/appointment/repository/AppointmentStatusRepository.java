package com.nayan.appointment.repository;

import com.nayan.appointment.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentStatusRepository extends JpaRepository<AppointmentStatus, Integer>
{
	List<AppointmentStatus> findAppointmentStatusByCompanyId(Long companyId);
}
