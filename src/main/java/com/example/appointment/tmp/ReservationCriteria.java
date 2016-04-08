package com.example.appointment.tmp;

import java.time.LocalDateTime;

public class ReservationCriteria {
    private String serviceName;
    private LocalDateTime startingFrom;
    private String doctorName;

    public ReservationCriteria(ReservationCriteria reservationCriteria) {
        this.serviceName = reservationCriteria.serviceName;
        this.startingFrom = reservationCriteria.startingFrom;
        this.doctorName = reservationCriteria.doctorName;
    }

    public ReservationCriteria() {

    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public LocalDateTime getStartingFrom() {
        return startingFrom;
    }

    public ReservationCriteria service(String serviceName) {
        ReservationCriteria reservationCriteria = new ReservationCriteria(this);
        reservationCriteria.serviceName = serviceName;
        return reservationCriteria;
    }

    public ReservationCriteria doctor(String doctorName) {
        ReservationCriteria reservationCriteria = new ReservationCriteria(this);
        reservationCriteria.doctorName= doctorName;
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
                "doctorName='" + doctorName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", startingFrom=" + startingFrom +
                '}';
    }
}
