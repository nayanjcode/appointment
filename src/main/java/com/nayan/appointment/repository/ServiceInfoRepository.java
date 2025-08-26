package com.nayan.appointment.repository;

import com.nayan.appointment.entity.ServiceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceInfoRepository extends JpaRepository<ServiceInfo, Long>
{
}
