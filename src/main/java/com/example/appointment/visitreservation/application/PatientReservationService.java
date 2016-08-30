package com.example.appointment.visitreservation.application;

import com.example.appointment.visitreservation.domain.PatientId;
import com.example.appointment.visitreservation.domain.PatientReservation;
import com.example.appointment.visitreservation.domain.ReservationRepository;
import com.example.appointment.scheduling.application.ReserveScheduleRangeService;
import com.example.appointment.visitreservation.domain.ServiceId;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.example.appointment.visitreservation.domain.PatientReservation.serviceReservation;

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

    public void makeReservationFor(PatientId patient, ServiceId serviceId, ScheduleRange scheduleRange) {
        reserveScheduleRangeService.reserve(scheduleRange);
        reservationRepository.save(serviceReservation(patient, Optional.of(serviceId), scheduleRange));
    }


}
