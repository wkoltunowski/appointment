package com.example.appointment.domain.reservation;

import com.example.appointment.application.ReserveAppointmentService;
import com.example.appointment.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.domain.schedule.ScheduleConnections;
import com.example.appointment.domain.schedule.ScheduleRepository;
import com.example.appointment.domain.ServiceId;

import java.util.Optional;

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
        ScheduleRange freeAppointment = ScheduleRange.scheduleRange(scheduleRange.range(), scheduleRange.scheduleId());
        reserveAppointmentService.reserve(freeAppointment);

        reserve(patient, freeAppointment);
    }

    private void reserve(PatientId patient, ScheduleRange scheduleRange) {
        ScheduleConnections scheduleConnections = scheduleRepository.findById(scheduleRange.scheduleId()).scheduleDefinition();
        Optional<ServiceId> serviceId = scheduleConnections.serviceId();
        reservationRepository.save(PatientServiceReservation.serviceReservation(patient, serviceId, ScheduleRange.scheduleRange(scheduleRange.range(), scheduleRange.scheduleId())));
    }


}
