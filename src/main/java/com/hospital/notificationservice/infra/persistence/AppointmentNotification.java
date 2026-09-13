package com.hospital.notificationservice.infra.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointment_notifications")
public class AppointmentNotification {

    @Id
    private UUID appointmentId;
    private String patientId;
    private String eventType;
    private String status;
    private LocalDateTime appointmentDate;
    private LocalDateTime lastEventAt;

    protected AppointmentNotification() {
    }

    public AppointmentNotification(AppointmentEventData event) {
        this.appointmentId = event.appointmentId();
        this.patientId = event.patientId();
        this.eventType = event.eventType();
        this.status = event.status();
        this.appointmentDate = event.appointmentDate();
        this.lastEventAt = event.updatedAt();
    }

    public boolean isNewerThan(LocalDateTime eventUpdatedAt) {
        return lastEventAt == null || !lastEventAt.isAfter(eventUpdatedAt);
    }

    public void update(AppointmentEventData event) {
        this.patientId = event.patientId();
        this.eventType = event.eventType();
        this.status = event.status();
        this.appointmentDate = event.appointmentDate();
        this.lastEventAt = event.updatedAt();
    }

    public record AppointmentEventData(
            UUID appointmentId,
            String patientId,
            String eventType,
            String status,
            LocalDateTime appointmentDate,
            LocalDateTime updatedAt) {

    }
}
