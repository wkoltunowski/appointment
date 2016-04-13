package com.example.appointment;

import com.example.appointment.tmp.Reservation;

import java.util.List;

public interface ReservationRepository {
    void save(PatientReservation reservation);

    List<PatientReservation> findAll();
}
