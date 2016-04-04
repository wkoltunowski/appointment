package com.example.appointment;

import com.example.appointment.application.DefineScheduleService;
import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.application.GenerateFreeSlotsService;
import com.example.appointment.application.ReserveAppointmentService;
import com.example.appointment.domain.freeslot.FreeSlotRepository;
import com.example.appointment.domain.schedule.FromScheduleDuration;
import com.example.appointment.domain.schedule.ScheduleRepository;
import com.example.appointment.infrastructure.DayCollectionFreeSlotRepository;
import com.example.appointment.infrastructure.InMemoryApplicationEventing;
import com.example.appointment.infrastructure.InMemoryScheduleRepository;

public class Factory {
    private DayCollectionFreeSlotRepository freeSlotRepository;
    private FromScheduleDuration scheduleDurations;
    private ScheduleRepository scheduleRepository;
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

    public DefineScheduleService scheduleDefinitionService() {
        return new DefineScheduleService(scheduleRepository(), applicationEventing());
    }

    private ApplicationEventing applicationEventing() {
        return new InMemoryApplicationEventing(generateFreeSlotsService());
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
}
