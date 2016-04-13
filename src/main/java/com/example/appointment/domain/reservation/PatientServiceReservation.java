package com.example.appointment.domain.reservation;

import com.example.appointment.domain.ServiceId;
import com.example.appointment.domain.freescheduleranges.ScheduleRange;

import java.util.Optional;

public class PatientServiceReservation {
    private final PatientId patient;
    private final ScheduleRange scheduleRange;
    private final Optional<ServiceId> serviceId;

    public PatientServiceReservation(Optional<ServiceId> serviceId, PatientId patient, ScheduleRange scheduleRange) {
        this.serviceId = serviceId;
        this.patient = patient;
        this.scheduleRange = scheduleRange;
    }

    public ScheduleRange scheduleRange() {
        return scheduleRange;
    }

    public PatientId patient() {
        return patient;
    }

    public Optional<ServiceId> service() {
        return serviceId;
    }


    public static PatientServiceReservation serviceReservation(PatientId patient, ServiceId serviceId, ScheduleRange scheduleRange) {
        return new PatientServiceReservation(Optional.of(serviceId), patient, scheduleRange);
    }

    public static PatientServiceReservation serviceReservation(PatientId patient, Optional<ServiceId> serviceId, ScheduleRange scheduleRange) {
        return new PatientServiceReservation(serviceId, patient, scheduleRange);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientServiceReservation that = (PatientServiceReservation) o;

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
}
