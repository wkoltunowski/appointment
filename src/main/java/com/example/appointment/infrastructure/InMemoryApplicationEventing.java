package com.example.appointment.infrastructure;

import com.example.appointment.ApplicationEventing;
import com.example.appointment.application.GenerateFreeSlotsService;
import com.example.appointment.application.LocationAddedEvent;
import com.example.appointment.application.ScheduleAddedEvent;
import com.example.appointment.application.ServiceAddedEvent;

public class InMemoryApplicationEventing implements ApplicationEventing {
    private final GenerateFreeSlotsService generateFreeSlotsService;

    public InMemoryApplicationEventing(GenerateFreeSlotsService generateFreeSlotsService) {
        this.generateFreeSlotsService = generateFreeSlotsService;
    }

    @Override
    public void publishEvent(ScheduleAddedEvent scheduleAddedEvent) {
        generateFreeSlotsService.generateFreeSlots(scheduleAddedEvent.scheduleId());
    }

    @Override
    public void publishEvent(ServiceAddedEvent serviceAddedEvent) {
        generateFreeSlotsService.regenerateFreeSlots(serviceAddedEvent.scheduleId());
    }

    @Override
    public void publishEvent(LocationAddedEvent locationAddedEvent) {
        generateFreeSlotsService.regenerateFreeSlots(locationAddedEvent.scheduleId());
    }
}
