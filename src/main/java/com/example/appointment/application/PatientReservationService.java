package com.example.appointment.application;

import com.example.appointment.domain.PatientId;
import com.example.appointment.domain.PatientReservation;
import com.example.appointment.domain.ReservationRepository;
import com.example.appointment.scheduling.application.ReserveScheduleRangeService;
import com.example.appointment.domain.ServiceId;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;

import java.util.Optional;

public class PatientReservationService {
    private final ReserveScheduleRangeService reserveScheduleRangeService;
    private final ReservationRepository reservationRepository;

    public PatientReservationService(ReserveScheduleRangeService reserveScheduleRangeService,
                                     ReservationRepository reservationRepository) {
        this.reserveScheduleRangeService = reserveScheduleRangeService;
        this.reservationRepository = reservationRepository;
    }

    public void makeReservationFor(PatientId patient, ScheduleRange scheduleRange, ServiceId serviceId) {
        reserveScheduleRangeService.reserve(scheduleRange);
        reservationRepository.save(PatientReservation.serviceReservation(patient, Optional.of(serviceId), ScheduleRange.scheduleRange(scheduleRange.range(), scheduleRange.scheduleId())));
    }


}
