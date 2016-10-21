package com.falco.appointment.scheduling.application;

public class AppointmentTakenException extends RuntimeException {
    public AppointmentTakenException() {
        super("Appointment taken!");
    }
}
