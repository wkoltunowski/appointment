package com.example.appointment.domain.schedule;

import java.time.Duration;

public class FromScheduleDuration implements ScheduleDurations {


    private final ScheduleRepository scheduleRepository;

    public FromScheduleDuration(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public Duration durationFor(ScheduleId scheduleId) {
        return this.scheduleRepository.findById(scheduleId).duration();
    }


}
