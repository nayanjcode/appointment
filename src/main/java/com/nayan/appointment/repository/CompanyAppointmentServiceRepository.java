package com.nayan.appointment.repository;

import com.nayan.appointment.entity.CompanyAppointmentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyAppointmentServiceRepository extends JpaRepository<CompanyAppointmentService, Long>
{
	List<CompanyAppointmentService> findCompanyAppointmentServiceByCompanyId(Long companyId);
}
