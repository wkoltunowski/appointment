package com.example.appointment.scheduling.domain.freescheduleranges;

import com.example.appointment.scheduling.domain.schedule.ScheduleId;

import java.time.Duration;

public interface ScheduleDurations {
    Duration durationFor(ScheduleId scheduleId);
}
