package com.example.appointment;

import com.example.appointment.scheduling.application.DefineNewScheduleService;
import com.example.appointment.scheduling.application.FindFreeRangesService;

public class Application {
    private final Factory factory = new Factory();


    public DefineNewScheduleService defineNewScheduleService() {
        return factory.scheduleDefinitionService();

    }

    public FindFreeRangesService findFreeSlots(int maxResultCount) {
        return factory.findFreeService(maxResultCount);
    }
}
