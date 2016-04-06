package com.example.appointment.infrastructure;

import com.example.appointment.ApplicationEventing;
import com.example.appointment.application.GenerateFreeSlotsService;
import com.example.appointment.application.ScheduleAddedEvent;

public class SynchronousApplicationEventing implements ApplicationEventing {
    private final GenerateFreeSlotsService generateFreeSlotsService;

    public SynchronousApplicationEventing(GenerateFreeSlotsService generateFreeSlotsService) {
        this.generateFreeSlotsService = generateFreeSlotsService;
    }

    @Override
    public void publishEvent(ScheduleAddedEvent scheduleAddedEvent) {
        generateFreeSlotsService.generateFreeSlots(scheduleAddedEvent.scheduleId());
    }

}
