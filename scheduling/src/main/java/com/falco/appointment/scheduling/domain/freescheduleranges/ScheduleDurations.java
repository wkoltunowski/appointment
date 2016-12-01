package com.falco.appointment.scheduling.domain.freescheduleranges;

import com.falco.appointment.scheduling.api.ScheduleId;

import java.time.Duration;

public interface ScheduleDurations {
    Duration durationFor(ScheduleId scheduleId);
}
