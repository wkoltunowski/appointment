package com.example.appointment.scheduling.application;

public class AppointmentTakenException extends RuntimeException {
    public AppointmentTakenException() {
        super("Appointment taken!");
    }
}
