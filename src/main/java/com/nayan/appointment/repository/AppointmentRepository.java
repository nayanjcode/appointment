package com.nayan.appointment.repository;

import com.nayan.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>
{
	@Query("""
			     SELECT a FROM Appointment a
			         WHERE a.companyId = :companyId AND
			             a.appointmentDate BETWEEN :startOfDay AND :endOfDay AND
			             a.statusId IN :status
			""")
	List<Appointment> getAppointments(@Param("companyId") long companyId, @Param("startOfDay") LocalDateTime startOfDay,
	                                  @Param("endOfDay") LocalDateTime endOfDay, @Param("status") List<Integer> status);


	@Modifying
	@Query("UPDATE Appointment a SET a.statusId = :status WHERE a.appointmentId = :appointmentId")
	void updateAppointmentStatus(@Param("appointmentId") Long appointmentId, @Param("status") int status);

	Optional<Appointment> findTopByCompanyIdAndStatusIdNotInAndAppointmentDateBetweenOrderByAppointmentDateDesc(Long companyId, List<Integer> statusId, LocalDateTime startOfDay, LocalDateTime endOfDay);

}
