package com.example.appointment.domain.reservation;

import java.util.List;

public interface ReservationRepository {
    void save(PatientReservation reservation);

    List<PatientReservation> findPatientReservations(PatientId patientId);
}
