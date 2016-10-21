package com.falco.appointment.visitreservation.application;

import com.falco.appointment.visitreservation.domain.PatientId;
import com.falco.appointment.visitreservation.domain.ReservationRepository;
import com.falco.appointment.scheduling.application.ReserveScheduleRangeService;
import com.falco.appointment.visitreservation.domain.ServiceId;
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

    public void makeReservationFor(PatientId patient, ServiceId serviceId, ScheduleRange scheduleRange) {
        reserveScheduleRangeService.reserve(scheduleRange);
        reservationRepository.save(serviceReservation(patient, Optional.of(serviceId), scheduleRange));
    }


}
