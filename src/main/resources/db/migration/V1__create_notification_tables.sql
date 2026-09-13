CREATE TABLE appointment_notifications (
    appointment_id UUID PRIMARY KEY,
    patient_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    appointment_date TIMESTAMP NOT NULL,
    last_event_at TIMESTAMP NOT NULL
);

CREATE TABLE processed_appointment_events (
    event_id UUID PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL
);