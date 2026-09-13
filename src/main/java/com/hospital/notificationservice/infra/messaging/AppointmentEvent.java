package com.hospital.notificationservice.infra.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentEvent(
        UUID eventId,
        String eventType,
        UUID appointmentId,
        UUID id,
        String patientId,
        String doctorId,
        LocalDateTime appointmentDate,
        String status,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

}
