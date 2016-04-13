package com.example.appointment.domain.reservation;

import java.util.List;

public interface ReservationRepository {
    void save(PatientServiceReservation reservation);

    List<PatientServiceReservation> findPatientReservations(PatientId patientId);
}
