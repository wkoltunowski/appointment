package com.example.appointment;

import com.example.appointment.application.DefineNewScheduleService;
import com.example.appointment.application.FindFreeScheduleRangesService;

public class Application {
    private final Factory factory = new Factory();


    public DefineNewScheduleService defineDoctorSchedule() {
        return factory.scheduleDefinitionService();

    }

    public FindFreeScheduleRangesService findFreeSlots(int maxResultCount) {
        return factory.findFreeService(maxResultCount);
    }
}
