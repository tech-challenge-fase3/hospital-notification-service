package com.hospital.notificationservice.infra.messaging;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.hospital.notificationservice.application.ProcessAppointmentEventUseCase;
import com.rabbitmq.client.Channel;

@Component
public class AppointmentEventListener {

    private static final Logger log
            = LoggerFactory.getLogger(AppointmentEventListener.class);

    private final ProcessAppointmentEventUseCase processAppointmentEventUseCase;

    public AppointmentEventListener(
            ProcessAppointmentEventUseCase processAppointmentEventUseCase) {
        this.processAppointmentEventUseCase = processAppointmentEventUseCase;
    }

    @RabbitListener(queues = "hospital.appointments.created")
    public void onCreated(
            AppointmentEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        process(event, "APPOINTMENT_CREATED", channel, deliveryTag);
    }

    @RabbitListener(queues = "hospital.appointments.updated")
    public void onUpdated(
            AppointmentEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        process(event, "APPOINTMENT_UPDATED", channel, deliveryTag);
    }

    private void process(
            AppointmentEvent event,
            String eventType,
            Channel channel,
            long deliveryTag) throws IOException {
        try {
            processAppointmentEventUseCase.execute(normalize(event, eventType));
            channel.basicAck(deliveryTag, false);
        } catch (IllegalArgumentException exception) {
            log.warn("Evento inválido descartado: {}", exception.getMessage());
            channel.basicReject(deliveryTag, false);
        } catch (RuntimeException exception) {
            log.error("Falha ao processar evento: eventId={}", event.eventId(), exception);
            channel.basicNack(deliveryTag, false, true);
        }
    }

    private AppointmentEvent normalize(AppointmentEvent event, String eventType) {
        UUID appointmentId = event.appointmentId() != null
                ? event.appointmentId()
                : event.id();

        if (appointmentId == null || event.updatedAt() == null) {
            throw new IllegalArgumentException("eventId, appointmentId ou updatedAt ausente");
        }

        UUID eventId = event.eventId() != null
                ? event.eventId()
                : UUID.nameUUIDFromBytes((appointmentId + eventType + event.updatedAt())
                        .getBytes(StandardCharsets.UTF_8));

        return new AppointmentEvent(
                eventId,
                event.eventType() != null ? event.eventType() : eventType,
                appointmentId,
                event.id(),
                event.patientId(),
                event.doctorId(),
                event.appointmentDate(),
                event.status(),
                event.notes(),
                event.createdAt(),
                event.updatedAt()
        );
    }
}
