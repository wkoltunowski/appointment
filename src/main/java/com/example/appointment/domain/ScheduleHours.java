package com.example.appointment.domain;

import java.time.LocalTime;

public class ScheduleHours {
    private final LocalTime startTime;
    private final LocalTime endTime;

    public ScheduleHours(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public static ScheduleHours ofHours(LocalTime from, LocalTime to) {
        return new ScheduleHours(from, to);
    }

    public static ScheduleHours ofHours(String hours) {
        String[] split = hours.split("-");
        LocalTime from = LocalTime.parse(split[0]);
        LocalTime to = LocalTime.parse(split[1]);

        return new ScheduleHours(from, to);
    }
}
