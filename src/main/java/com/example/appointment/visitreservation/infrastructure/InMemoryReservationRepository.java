package com.example.appointment.visitreservation.infrastructure;

import com.example.appointment.visitreservation.domain.PatientId;
import com.example.appointment.visitreservation.domain.PatientReservation;
import com.example.appointment.visitreservation.domain.ReservationRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryReservationRepository implements ReservationRepository {
    final List<PatientReservation> reservations = new ArrayList<>();

    @Override
    public void save(PatientReservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public List<PatientReservation> findPatientReservations(PatientId patientId) {
        return reservations.stream().filter(r -> r.patient().equals(patientId)).collect(Collectors.toList());
    }

    @Override
    public List<PatientReservation> findAll(int maxSize) {
        return reservations.stream().limit(maxSize).collect(Collectors.toList());
    }
}
