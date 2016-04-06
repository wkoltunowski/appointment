package com.example.appointment.domain.schedule;

import java.time.Duration;

public class FromScheduleDuration implements ScheduleDurations {


    private final ScheduleRepository scheduleRepository;

    public FromScheduleDuration(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public Duration durationFor(ScheduleId scheduleId) {
        Schedule schedule = this.scheduleRepository
                .findById(scheduleId);
        return schedule.duration()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No duration defined for schedule :'%s'", scheduleId)));
    }


}
