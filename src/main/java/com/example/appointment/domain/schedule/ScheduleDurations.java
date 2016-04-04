package com.example.appointment.domain.schedule;

import java.time.Duration;

public interface ScheduleDurations {
    Duration durationFor(ScheduleId scheduleId);
}
