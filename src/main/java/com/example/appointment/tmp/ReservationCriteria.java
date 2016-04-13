package com.example.appointment.tmp;

import com.example.appointment.domain.schedule.DoctorId;
import com.example.appointment.domain.schedule.ServiceId;

import java.time.LocalDateTime;

public class ReservationCriteria {
    private LocalDateTime startingFrom;
    private DoctorId doctor;
    private ServiceId service;

    public ReservationCriteria(ReservationCriteria reservationCriteria) {
        this.service = reservationCriteria.service;
        this.startingFrom = reservationCriteria.startingFrom;
        this.doctor = reservationCriteria.doctor;
    }

    public ReservationCriteria() {

    }

    public DoctorId getDoctor() {
        return doctor;
    }

    public ServiceId getService() {
        return service;
    }

    public LocalDateTime getStartingFrom() {
        return startingFrom;
    }

    public ReservationCriteria service(ServiceId serviceId) {
        ReservationCriteria reservationCriteria = new ReservationCriteria(this);
        reservationCriteria.service = serviceId;
        return reservationCriteria;
    }

    public ReservationCriteria doctor(DoctorId doctorName) {
        ReservationCriteria reservationCriteria = new ReservationCriteria(this);
        reservationCriteria.doctor = doctorName;
        return reservationCriteria;
    }

    public ReservationCriteria startingFrom(LocalDateTime dateTime) {
        ReservationCriteria reservationCriteria = new ReservationCriteria(this);
        reservationCriteria.startingFrom = dateTime;
        return reservationCriteria;
    }

    @Override
    public String toString() {
        return "ReservationCriteria{" +
                "doctorName='" + doctor + '\'' +
                ", serviceName='" + service + '\'' +
                ", startingFrom=" + startingFrom +
                '}';
    }
}
