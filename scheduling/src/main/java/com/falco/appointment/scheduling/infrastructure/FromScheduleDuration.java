package com.falco.appointment.scheduling.infrastructure;

import com.falco.appointment.scheduling.domain.schedule.Schedule;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleDurations;
import com.falco.appointment.scheduling.api.ScheduleId;
import com.falco.appointment.scheduling.domain.schedule.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class FromScheduleDuration implements ScheduleDurations {
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public FromScheduleDuration(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public Duration durationFor(ScheduleId scheduleId) {
        Schedule schedule = this.scheduleRepository.findById(scheduleId);
        return schedule.duration()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No duration defined for schedule :'%s'", scheduleId)));
    }


}
