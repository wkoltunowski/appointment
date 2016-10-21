package com.falco.appointment.scheduling.infrastructure;

import com.falco.appointment.scheduling.application.GenerateFreeSlotsService;
import com.falco.appointment.scheduling.domain.ApplicationEventing;
import com.falco.appointment.scheduling.domain.schedule.ScheduleAddedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynchronousApplicationEventing implements ApplicationEventing {

    private final GenerateFreeSlotsService generateFreeSlotsService;

    @Autowired
    public SynchronousApplicationEventing(GenerateFreeSlotsService generateFreeSlotsService) {
        this.generateFreeSlotsService = generateFreeSlotsService;
    }

    @Override
    public void publishEvent(ScheduleAddedEvent scheduleAddedEvent) {
        generateFreeSlotsService.generateFreeSlots(scheduleAddedEvent.scheduleId());
    }

}
