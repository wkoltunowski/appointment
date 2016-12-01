package com.falco.appointment;

import com.falco.appointment.scheduling.api.CancellationService;
import com.falco.appointment.scheduling.api.FindFreeRangesService;
import com.falco.appointment.scheduling.api.ReservationService;
import com.falco.appointment.scheduling.application.DefineNewScheduleService;
import com.falco.appointment.scheduling.application.FindFreeRangesServiceImpl;
import com.falco.appointment.scheduling.application.GenerateFreeSlotsService;
import com.falco.appointment.scheduling.application.ReserveScheduleRangeServiceImpl;
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
        return new FindFreeRangesServiceImpl(maxResultCount, scheduleDurations(), freeSlotRepository());
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

    public ReservationService reservationService() {
        return new ReserveScheduleRangeServiceImpl(freeSlotRepository());
    }

    public CancellationService cancellationService() {
        return new ReserveScheduleRangeServiceImpl(freeSlotRepository());
    }

    public ScheduleRepository scheduleRepository() {
        if (scheduleRepository == null) {
            scheduleRepository = new InMemoryScheduleRepository();
        }
        return scheduleRepository;
    }

    public PatientReservationService patientReservation() {
        return new PatientReservationService(reservationService(), cancellationService(), reservationRepository());
    }

    public ReservationRepository reservationRepository() {
        if (reservationRepository == null) {
            reservationRepository = new InMemoryReservationRepository();
        }
        return reservationRepository;
    }

}
