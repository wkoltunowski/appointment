package com.falco.appointment.visitreservation.application;

import com.falco.appointment.visitreservation.domain.*;
import com.falco.appointment.scheduling.application.ReserveScheduleRangeService;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.falco.appointment.visitreservation.domain.PatientReservation.serviceReservation;

@Component
public class PatientReservationService {
    private final ReserveScheduleRangeService reserveScheduleRangeService;
    private final ReservationRepository reservationRepository;

    @Autowired
    public PatientReservationService(ReserveScheduleRangeService reserveScheduleRangeService,
                                     ReservationRepository reservationRepository) {
        this.reserveScheduleRangeService = reserveScheduleRangeService;
        this.reservationRepository = reservationRepository;
    }

    public ReservationId makeReservationFor(PatientId patient, ServiceId serviceId, ScheduleRange scheduleRange) {
        reserveScheduleRangeService.reserve(scheduleRange);
        PatientReservation reservation = serviceReservation(patient, Optional.of(serviceId), scheduleRange);
        reservationRepository.save(reservation);
        return reservation.id();
    }


    public void cancelReservation(ScheduleRange scheduleRange) {
        reserveScheduleRangeService.cancel(scheduleRange);
        reservationRepository.findAll(100).stream()
                .filter(r -> r.scheduleRange().equals(scheduleRange))
                .forEach(reservation -> {
                    reservation.cancel();
                    reservationRepository.update(reservation);
                });
    }

    public void cancelReservation(ReservationId reservationId) {
        PatientReservation reservation = reservationRepository.findById(reservationId);
        reserveScheduleRangeService.cancel(reservation.scheduleRange());
        reservation.cancel();
        reservationRepository.update(reservation);
    }
}
