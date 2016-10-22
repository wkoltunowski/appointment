package com.falco.appointment.visitreservation.domain;

import java.util.List;

public interface ReservationRepository {
    void save(PatientReservation reservation);

    List<PatientReservation> findPatientReservations(PatientId patientId);

    List<PatientReservation> findAll(int maxSize);

    void update(PatientReservation reservation);

    PatientReservation findById(ReservationId reservationId);
}
