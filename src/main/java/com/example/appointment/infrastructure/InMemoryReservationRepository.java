package com.example.appointment.infrastructure;

import com.example.appointment.domain.reservation.PatientId;
import com.example.appointment.domain.reservation.PatientServiceReservation;
import com.example.appointment.domain.reservation.ReservationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryReservationRepository implements ReservationRepository {
    final List<PatientServiceReservation> reservations = new ArrayList<>();

    @Override
    public void save(PatientServiceReservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public List<PatientServiceReservation> findPatientReservations(PatientId patientId) {
        return reservations.stream().filter(r -> r.patient().equals(patientId)).collect(Collectors.toList());
    }
}
