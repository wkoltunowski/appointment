package com.example.appointment;

import com.example.appointment.tmp.PatientId;
import com.example.appointment.domain.freeslot.ScheduleRange;

public class PatientReservation {
    private final PatientId patient;
    private final ScheduleRange scheduleRange;

    public PatientReservation(PatientId patient, ScheduleRange scheduleRange) {
        this.patient = patient;
        this.scheduleRange = scheduleRange;
    }

    public ScheduleRange scheduleRange() {
        return scheduleRange;
    }

    public PatientId patient() {
        return patient;
    }

    public static PatientReservation of(PatientId patient, ScheduleRange scheduleRange) {
        return new PatientReservation(patient, scheduleRange);
    }
}
