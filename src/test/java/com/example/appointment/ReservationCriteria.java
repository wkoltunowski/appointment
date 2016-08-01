package com.example.appointment;

import com.example.appointment.domain.SearchTags;
import com.example.appointment.domain.DoctorId;
import com.example.appointment.domain.ServiceId;

import java.time.LocalDateTime;

public class ReservationCriteria {
    private LocalDateTime startingFrom;
    private DoctorId doctor;
    private ServiceId service;

    public ReservationCriteria() {
    }

    public ReservationCriteria(ReservationCriteria reservationCriteria) {
        this.service = reservationCriteria.service;
        this.startingFrom = reservationCriteria.startingFrom;
        this.doctor = reservationCriteria.doctor;
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

    public SearchTags searchTags() {
        SearchTags searchTags = SearchTags.empty();

        if (service != null) {
            searchTags = searchTags.forService(service.asString());
        }
        if (doctor != null) {
            searchTags = searchTags.forDoctor(doctor.asString());
        }
        return searchTags;
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
