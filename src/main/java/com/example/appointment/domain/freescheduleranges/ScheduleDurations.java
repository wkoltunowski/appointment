package com.example.appointment.domain.freescheduleranges;

import com.example.appointment.domain.schedule.ScheduleId;

import java.time.Duration;

public interface ScheduleDurations {
    Duration durationFor(ScheduleId scheduleId);
}
