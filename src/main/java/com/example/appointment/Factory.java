package com.example.appointment;

import com.example.appointment.application.DefineNewScheduleService;
import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.application.GenerateFreeSlotsService;
import com.example.appointment.application.ReserveAppointmentService;
import com.example.appointment.domain.ApplicationEventing;
import com.example.appointment.domain.freeslot.FreeSlotRepository;
import com.example.appointment.domain.schedule.FromScheduleDuration;
import com.example.appointment.domain.schedule.ScheduleRepository;
import com.example.appointment.infrastructure.DayCollectionFreeSlotRepository;
import com.example.appointment.infrastructure.InMemoryScheduleRepository;
import com.example.appointment.infrastructure.SynchronousApplicationEventing;

import java.util.ArrayList;
import java.util.List;

public class Factory {
    private FreeSlotRepository freeSlotRepository;
    private FromScheduleDuration scheduleDurations;
    private ScheduleRepository scheduleRepository;
    private ReservationRepository reservationRepository;
    private GenerateFreeSlotsService generateFreeSlotsService;

    public FindFreeAppointmentsService findFreeService(int maxResultCount) {
        return new FindFreeAppointmentsService(maxResultCount, scheduleDurations(), freeSlotRepository());
    }

    public FromScheduleDuration scheduleDurations() {
        if (scheduleDurations == null) {
            scheduleDurations = new FromScheduleDuration(scheduleRepository());
        }
        return scheduleDurations;
    }

    public FreeSlotRepository freeSlotRepository() {
        if (freeSlotRepository == null) {
            freeSlotRepository = new DayCollectionFreeSlotRepository();
        }
        return freeSlotRepository;
    }

    public DefineNewScheduleService scheduleDefinitionService() {
        return new DefineNewScheduleService(scheduleRepository(), applicationEventing());
    }

    private ApplicationEventing applicationEventing() {
        return new SynchronousApplicationEventing(generateFreeSlotsService());
    }

    private GenerateFreeSlotsService generateFreeSlotsService() {
        if (generateFreeSlotsService == null) {
            generateFreeSlotsService = new GenerateFreeSlotsService(scheduleRepository(), freeSlotRepository());
        }
        return generateFreeSlotsService;
    }

    public ReserveAppointmentService reservationService() {
        return new ReserveAppointmentService(freeSlotRepository());
    }

    public ScheduleRepository scheduleRepository() {
        if (scheduleRepository == null) {
            scheduleRepository = new InMemoryScheduleRepository();
        }
        return scheduleRepository;
    }

    public PatientReservationService patientReservation() {
        return new PatientReservationService(reservationService(), scheduleRepository(),
                reservationRepository());
    }

    private ReservationRepository reservationRepository() {
        if (reservationRepository == null) {
            final List<PatientReservation> reservations = new ArrayList<>();
            reservationRepository = new ReservationRepository() {
                @Override
                public void save(PatientReservation reservation) {
                    reservations.add(reservation);
                }

                @Override
                public List<PatientReservation> findAll() {

                    return reservations;
                }
            };
        }
        return reservationRepository;
    }

}
