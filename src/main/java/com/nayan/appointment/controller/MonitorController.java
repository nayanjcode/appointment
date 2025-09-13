package com.nayan.appointment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor")
public class MonitorController
{
	@GetMapping("/status")
	public ResponseEntity<String> checkServiceStatus()
	{
		return ResponseEntity.ok("Service is running...");
	}
}
