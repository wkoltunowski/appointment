package com.falco.appointment.visitreservation.domain;

import com.falco.appointment.scheduling.api.ScheduleRange;

import java.util.Optional;

public class PatientReservation {
    private final PatientId patient;
    private final ScheduleRange scheduleRange;
    private final Optional<ServiceId> serviceId;
    private String state;
    private ReservationId id;

    public PatientReservation(Optional<ServiceId> serviceId, PatientId patient, ScheduleRange scheduleRange) {
        this.id = ReservationId.newId();
        this.serviceId = serviceId;
        this.patient = patient;
        this.scheduleRange = scheduleRange;
        this.state = "Scheduled";
    }

    public PatientId patient() {
        return patient;
    }

    public ScheduleRange scheduleRange() {
        return scheduleRange;
    }

    public Optional<ServiceId> serviceId() {
        return serviceId;
    }

    public static PatientReservation serviceReservation(PatientId patient, ServiceId serviceId, ScheduleRange scheduleRange) {
        return new PatientReservation(Optional.of(serviceId), patient, scheduleRange);
    }

    public static PatientReservation serviceReservation(PatientId patient, Optional<ServiceId> serviceId, ScheduleRange scheduleRange) {
        return new PatientReservation(serviceId, patient, scheduleRange);
    }

    public void cancel() {
        this.state = "Cancelled";
    }

    public String state() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientReservation that = (PatientReservation) o;

        if (!serviceId.equals(that.serviceId)) return false;
        if (!patient.equals(that.patient)) return false;
        return scheduleRange.equals(that.scheduleRange);

    }

    @Override
    public int hashCode() {
        int result = serviceId.hashCode();
        result = 31 * result + patient.hashCode();
        result = 31 * result + scheduleRange.hashCode();
        return result;
    }

    public ReservationId id() {
        return id;
    }
}
