package com.example.appointment.domain;

import com.example.appointment.application.FindFreeAppointmentsService;
import com.example.appointment.application.ReserveAppointmentService;
import com.example.appointment.application.DefineScheduleService;
import com.example.appointment.infrastructure.DayCollectionFreeSlotsStorage;

public class Factory {
    private DayCollectionFreeSlotsStorage storage;
    private ScheduleDurations scheduleDurations;

    public FindFreeAppointmentsService findFreeService(int maxResultCount) {
        return new FindFreeAppointmentsService(maxResultCount, scheduleDurations(), storage());
    }

    public ScheduleDurations scheduleDurations() {
        if (scheduleDurations == null) {
            scheduleDurations = new ScheduleDurations();
        }
        return scheduleDurations;
    }

    public FreeSlotsStorage storage() {
        if (storage == null) {
            storage = new DayCollectionFreeSlotsStorage();
        }
        return storage;
    }

    public DefineScheduleService scheduleDefinitionService() {
        return new DefineScheduleService(scheduleDurations(), storage());
    }

    public ReserveAppointmentService reservationService() {
        return new ReserveAppointmentService(storage());
    }
}
