package com.example.appointment;

import com.example.appointment.application.DefineScheduleService;
import com.example.appointment.application.FindFreeSlotsService;

public class Application {
    private final Factory factory = new Factory();


    public DefineScheduleService defineDoctorSchedule() {
        return factory.scheduleDefinitionService();

    }

    public FindFreeSlotsService findFreeSlots(int maxResultCount) {
        return new FindFreeSlotsService(factory.findFreeService(maxResultCount));
    }
}
