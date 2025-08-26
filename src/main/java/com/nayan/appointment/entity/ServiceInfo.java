package com.nayan.appointment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ServiceInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long serviceId;
	private String serviceName;
}
