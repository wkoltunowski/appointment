package com.falco.appointment;

import com.falco.appointment.scheduling.application.DefineNewScheduleService;
import com.falco.appointment.scheduling.application.FindFreeRangesService;
import com.falco.appointment.scheduling.application.GenerateFreeSlotsService;
import com.falco.appointment.scheduling.application.ReserveScheduleRangeService;
import com.falco.appointment.scheduling.domain.ApplicationEventing;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.falco.appointment.scheduling.infrastructure.*;
import com.falco.appointment.visitreservation.application.PatientReservationService;
import com.falco.appointment.visitreservation.domain.ReservationRepository;
import com.falco.appointment.scheduling.domain.schedule.ScheduleRepository;
import com.falco.appointment.visitreservation.infrastructure.InMemoryReservationRepository;

public class Factory {
    private FreeScheduleSlotRepository freeScheduleSlotRepository;
    private FromScheduleDuration scheduleDurations;
    private ScheduleRepository scheduleRepository;
    private ReservationRepository reservationRepository;
    private GenerateFreeSlotsService generateFreeSlotsService;

    public FindFreeRangesService findFreeService(int maxResultCount) {
        return new FindFreeRangesService(maxResultCount, scheduleDurations(), freeSlotRepository());
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
//            freeScheduleSlotRepository = new SortedListFreeScheduleSlotRepository();
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
        return new PatientReservationService(reservationService(), reservationRepository());
    }

    public ReservationRepository reservationRepository() {
        if (reservationRepository == null) {
            reservationRepository = new InMemoryReservationRepository();
        }
        return reservationRepository;
    }

}
