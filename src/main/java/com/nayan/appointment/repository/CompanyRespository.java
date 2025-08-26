package com.nayan.appointment.repository;

import com.nayan.appointment.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRespository extends JpaRepository<Company,Long>
{
}
