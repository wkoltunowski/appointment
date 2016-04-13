package com.example.appointment.tmp;

import com.example.appointment.domain.freeslot.ScheduleRange;
import com.example.appointment.domain.schedule.ServiceId;

import java.util.Optional;

public class Reservation {
    private ScheduleRange scheduleRange;
    private PatientId patientId;
    private Optional<ServiceId> serviceId;


    public static Reservation of(PatientId patientName, ScheduleRange scheduleRange, Optional<ServiceId> serviceId) {
        Reservation reservation = new Reservation();
        reservation.patientId = patientName;
        reservation.scheduleRange = scheduleRange;
        reservation.serviceId = serviceId;

        return reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reservation that = (Reservation) o;

        if (!serviceId.equals(that.serviceId)) return false;
        if (!patientId.equals(that.patientId)) return false;
        return scheduleRange.equals(that.scheduleRange);

    }

    @Override
    public int hashCode() {
        int result = serviceId.hashCode();
        result = 31 * result + patientId.hashCode();
        result = 31 * result + scheduleRange.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "scheduleRange='" + scheduleRange + '\'' +
                ", service='" + serviceId + '\'' +
                ", patientName='" + patientId + '\'' +
                '}';
    }

    public static Reservation forService(PatientId patient, ScheduleRange scheduleRange, ServiceId serviceId) {
        return of(patient, scheduleRange, Optional.of(serviceId));
    }
}
