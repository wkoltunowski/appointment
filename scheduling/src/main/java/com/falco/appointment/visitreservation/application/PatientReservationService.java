package com.falco.appointment.visitreservation.application;

import com.falco.appointment.scheduling.api.CancellationService;
import com.falco.appointment.scheduling.api.ReservationService;
import com.falco.appointment.scheduling.api.ScheduleRange;
import com.falco.appointment.visitreservation.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.falco.appointment.visitreservation.domain.PatientReservation.serviceReservation;

@Component
public class PatientReservationService {
    private final ReservationService reservationService;
    private final CancellationService cancellationService;
    private final ReservationRepository reservationRepository;

    @Autowired
    public PatientReservationService(ReservationService reservationService,
                                     CancellationService cancellationService,
                                     ReservationRepository reservationRepository) {
        this.reservationService = reservationService;
        this.cancellationService = cancellationService;
        this.reservationRepository = reservationRepository;
    }

    public ReservationId makeReservationFor(PatientId patient, ServiceId serviceId, ScheduleRange scheduleRange) {
        reservationService.reserve(scheduleRange);
        PatientReservation reservation = serviceReservation(patient, Optional.of(serviceId), scheduleRange);
        reservationRepository.save(reservation);
        return reservation.id();
    }


    public void cancelReservation(ScheduleRange scheduleRange) {
        cancellationService.cancel(scheduleRange);
        reservationRepository.findAll(100).stream()
                .filter(r -> r.scheduleRange().equals(scheduleRange))
                .forEach(reservation -> {
                    reservation.cancel();
                    reservationRepository.update(reservation);
                });
    }

    public void cancelReservation(ReservationId reservationId) {
        PatientReservation reservation = reservationRepository.findById(reservationId);
        cancellationService.cancel(reservation.scheduleRange());
        reservation.cancel();
        reservationRepository.update(reservation);
    }
}
