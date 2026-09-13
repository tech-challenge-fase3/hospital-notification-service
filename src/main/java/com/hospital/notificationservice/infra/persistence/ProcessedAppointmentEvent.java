package com.hospital.notificationservice.infra.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "processed_appointment_events")
public class ProcessedAppointmentEvent {

    @Id
    private UUID eventId;
    private LocalDateTime processedAt;

    protected ProcessedAppointmentEvent() {
    }

    public ProcessedAppointmentEvent(UUID eventId, LocalDateTime processedAt) {
        this.eventId = eventId;
        this.processedAt = processedAt;
    }
}
