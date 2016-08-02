package com.example.appointment.visitreservation.domain;

import java.util.List;

public interface ReservationRepository {
    void save(PatientReservation reservation);

    List<PatientReservation> findPatientReservations(PatientId patientId);
}
