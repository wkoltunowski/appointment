package com.example.appointment;

import com.example.appointment.application.DefineNewScheduleService;
import com.example.appointment.application.FindFreeScheduleRangesService;
import com.example.appointment.application.GenerateFreeSlotsService;
import com.example.appointment.application.ReserveScheduleRangeService;
import com.example.appointment.domain.ApplicationEventing;
import com.example.appointment.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.example.appointment.domain.reservation.PatientReservationService;
import com.example.appointment.domain.reservation.ReservationRepository;
import com.example.appointment.infrastructure.FromScheduleDuration;
import com.example.appointment.domain.schedule.ScheduleRepository;
import com.example.appointment.infrastructure.DayCollectionFreeScheduleSlotRepository;
import com.example.appointment.infrastructure.InMemoryReservationRepository;
import com.example.appointment.infrastructure.InMemoryScheduleRepository;
import com.example.appointment.infrastructure.SynchronousApplicationEventing;

public class Factory {
    private FreeScheduleSlotRepository freeScheduleSlotRepository;
    private FromScheduleDuration scheduleDurations;
    private ScheduleRepository scheduleRepository;
    private ReservationRepository reservationRepository;
    private GenerateFreeSlotsService generateFreeSlotsService;

    public FindFreeScheduleRangesService findFreeService(int maxResultCount) {
        return new FindFreeScheduleRangesService(maxResultCount, scheduleDurations(), freeSlotRepository());
    }

    public FromScheduleDuration scheduleDurations() {
        if (scheduleDurations == null) {
            scheduleDurations = new FromScheduleDuration(scheduleRepository());
        }
        return scheduleDurations;
    }

    public FreeScheduleSlotRepository freeSlotRepository() {
        if (freeScheduleSlotRepository == null) {
            freeScheduleSlotRepository = new DayCollectionFreeScheduleSlotRepository();
        }
        return freeScheduleSlotRepository;
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

    public ReserveScheduleRangeService reservationService() {
        return new ReserveScheduleRangeService(freeSlotRepository());
    }

    public ScheduleRepository scheduleRepository() {
        if (scheduleRepository == null) {
            scheduleRepository = new InMemoryScheduleRepository();
        }
        return scheduleRepository;
    }

    public PatientReservationService patientReservation() {
        return new PatientReservationService(reservationService(),
                reservationRepository());
    }

    public ReservationRepository reservationRepository() {
        if (reservationRepository == null) {
            reservationRepository = new InMemoryReservationRepository();
        }
        return reservationRepository;
    }

}
