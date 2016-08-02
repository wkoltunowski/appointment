package com.example.appointment.scheduling.infrastructure.restapi;

import com.example.appointment.scheduling.domain.schedule.ScheduleId;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDateTime;

public class FreeRange {
    private final String scheduleId;
    private final String start;
    private final String duration;

    public FreeRange(ScheduleId scheduleId, Range<LocalDateTime> range) {
        this.scheduleId = scheduleId.id().toString();
        this.start = range.lowerEndpoint().toString();
        this.duration = Duration.between(range.lowerEndpoint(), range.upperEndpoint()).toString();
    }

    public String getDuration() {
        return duration;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getStart() {
        return start;
    }
}
