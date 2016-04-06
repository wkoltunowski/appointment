package com.example.appointment;

import com.example.appointment.application.DefineNewScheduleService;
import com.example.appointment.application.FindFreeSlotsService;

public class Application {
    private final Factory factory = new Factory();


    public DefineNewScheduleService defineDoctorSchedule() {
        return factory.scheduleDefinitionService();

    }

    public FindFreeSlotsService findFreeSlots(int maxResultCount) {
        return new FindFreeSlotsService(factory.findFreeService(maxResultCount));
    }
}
