package com.falco.appointment;

import com.falco.appointment.scheduling.api.FindFreeRangesService;
import com.falco.appointment.scheduling.application.DefineNewScheduleService;

public class Application {
    private final Factory factory = new Factory();


    public DefineNewScheduleService defineNewScheduleService() {
        return factory.scheduleDefinitionService();

    }

    public FindFreeRangesService findFreeSlots(int maxResultCount) {
        return factory.findFreeService(maxResultCount);
    }
}
