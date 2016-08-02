package com.example.appointment.scheduling.domain.schedule;

import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class WorkingHours {
    private final LocalTime startTime;
    private final LocalTime endTime;

    public WorkingHours(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public Range<LocalDateTime> toRange(LocalDate date) {
        if (!startTime.isAfter(endTime)) {
            return Range.closedOpen(date.atTime(startTime), date.atTime(endTime));
        }
        return Range.closedOpen(date.atTime(startTime), date.plusDays(1).atTime(endTime));
    }

    public static WorkingHours ofHours(LocalTime from, LocalTime to) {
        return new WorkingHours(from, to);
    }

    public static WorkingHours ofHours(String hours) {
        String[] split = hours.split("-");
        LocalTime from = LocalTime.parse(split[0]);
        LocalTime to = LocalTime.parse(split[1]);

        return new WorkingHours(from, to);
    }

    public static WorkingHours none() {
        return ofHours("00:00-00:00");
    }
}
