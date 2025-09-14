package com.nayan.appointment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Comparator;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AppointmentStatusHistory
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int statusId;

	private Instant changedAt;

	@Transient
	private long changedAtEpoch;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "appointmentId", nullable = false)
	@JsonIgnore
	private Appointment appointment;


	public static final Comparator<AppointmentStatusHistory> BY_STATUS_HIST_LATEST_DATE =
			(a, b) -> {
				Instant aDate = a.getChangedAt();
				Instant bDate = b.getChangedAt();

				if (aDate.equals(bDate))
				{
					return 0;
				} else if (aDate.isAfter(bDate))
				{
					return -1; // latest first
				} else
				{
					return 1;
				}
			};
}
