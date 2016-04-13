package com.example.appointment;

import com.example.appointment.application.ReserveAppointmentService;
import com.example.appointment.domain.freeslot.ScheduleRange;
import com.example.appointment.domain.schedule.ScheduleConnections;
import com.example.appointment.domain.schedule.ScheduleRepository;
import com.example.appointment.tmp.PatientId;
import com.example.appointment.tmp.Reservation;

import java.util.List;
import java.util.stream.Collectors;

public class PatientReservationService {
    private final ReserveAppointmentService reserveAppointmentService;
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;

    public PatientReservationService(
            ReserveAppointmentService reserveAppointmentService,
            ScheduleRepository scheduleRepository,
            ReservationRepository reservationRepository) {
        this.reserveAppointmentService = reserveAppointmentService;
        this.scheduleRepository = scheduleRepository;
        this.reservationRepository = reservationRepository;
    }

    public void makeReservationFor(PatientId patient, ScheduleRange scheduleRange) {
        ScheduleRange freeAppointment = ScheduleRange.of(scheduleRange.range(), scheduleRange.scheduleId());
        reserveAppointmentService.reserve(freeAppointment);

        reserve(patient, freeAppointment);
    }

    private void reserve(PatientId patient, ScheduleRange scheduleRange) {
        reservationRepository.save(PatientReservation.of(patient, ScheduleRange.of(scheduleRange.range(), scheduleRange.scheduleId())));
    }

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll().stream().map(this::toReservation).collect(Collectors.toList());
    }

    private Reservation toReservation(PatientReservation pr) {
        ScheduleConnections scheduleConnections = scheduleRepository.findById(pr.scheduleRange().scheduleId()).scheduleDefinition();
        return Reservation.of(pr.patient(), pr.scheduleRange(), scheduleConnections.serviceId());
    }
}
