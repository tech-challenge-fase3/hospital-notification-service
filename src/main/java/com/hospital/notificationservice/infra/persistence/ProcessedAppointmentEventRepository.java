package com.hospital.notificationservice.infra.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedAppointmentEventRepository
        extends JpaRepository<ProcessedAppointmentEvent, UUID> {
}
