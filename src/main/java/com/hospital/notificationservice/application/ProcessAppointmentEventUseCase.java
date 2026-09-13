package com.hospital.notificationservice.application;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.notificationservice.infra.messaging.AppointmentEvent;
import com.hospital.notificationservice.infra.persistence.AppointmentNotification;
import com.hospital.notificationservice.infra.persistence.AppointmentNotification.AppointmentEventData;
import com.hospital.notificationservice.infra.persistence.AppointmentNotificationRepository;
import com.hospital.notificationservice.infra.persistence.ProcessedAppointmentEvent;
import com.hospital.notificationservice.infra.persistence.ProcessedAppointmentEventRepository;

@Service
public class ProcessAppointmentEventUseCase {

    private static final Logger log
            = LoggerFactory.getLogger(ProcessAppointmentEventUseCase.class);

    private final ProcessedAppointmentEventRepository processedEventRepository;
    private final AppointmentNotificationRepository notificationRepository;

    public ProcessAppointmentEventUseCase(
            ProcessedAppointmentEventRepository processedEventRepository,
            AppointmentNotificationRepository notificationRepository) {
        this.processedEventRepository = processedEventRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void execute(AppointmentEvent event) {
        if (processedEventRepository.existsById(event.eventId())) {
            log.info("Evento já processado: eventId={}", event.eventId());
            return;
        }

        AppointmentEventData eventData = new AppointmentEventData(
                event.appointmentId(),
                event.patientId(),
                event.eventType(),
                event.status(),
                event.appointmentDate(),
                event.updatedAt()
        );

        notificationRepository.findById(event.appointmentId())
                .ifPresentOrElse(
                        notification -> {
                            if (notification.isNewerThan(event.updatedAt())) {
                                notification.update(eventData);
                                notificationRepository.save(notification);
                            }
                        },
                        () -> notificationRepository.save(
                                new AppointmentNotification(eventData)
                        )
                );

        processedEventRepository.save(
                new ProcessedAppointmentEvent(event.eventId(), LocalDateTime.now())
        );

        log.info(
                "Notificação de agendamento processada: eventId={}, appointmentId={}, eventType={}",
                event.eventId(), event.appointmentId(), event.eventType()
        );
    }
}
