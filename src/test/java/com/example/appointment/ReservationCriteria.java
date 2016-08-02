package com.example.appointment;

import com.example.appointment.domain.DoctorTag;
import com.example.appointment.scheduling.domain.SearchTags;
import com.example.appointment.domain.DoctorId;
import com.example.appointment.domain.ServiceId;
import com.example.appointment.domain.ServiceTag;

import java.time.LocalDateTime;

public class ReservationCriteria {
    private LocalDateTime startingFrom;
    private DoctorId doctor;
    private ServiceId service;
    private SearchTags searchTags = SearchTags.empty();

    public ReservationCriteria() {
    }

    public ReservationCriteria(ReservationCriteria reservationCriteria) {
        this.service = reservationCriteria.service;
        this.startingFrom = reservationCriteria.startingFrom;
        this.doctor = reservationCriteria.doctor;

        if (service != null) {
            searchTags = searchTags.withTagAdded(ServiceTag.of(service));
        }
        if (doctor != null) {
            searchTags = searchTags.withTagAdded(DoctorTag.of(doctor));
        }
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
        return searchTags;
    }

    @Override
    public String toString() {
        return "ReservationCriteria{" +
                "searchTags='" + searchTags + '\'' +
                ", startingFrom=" + startingFrom +
                '}';
    }

    public ServiceId serviceId() {
        return service;
    }
}
