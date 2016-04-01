package com.example.appointment.application;

import com.example.appointment.domain.*;

import java.time.Duration;
import java.time.LocalTime;

import static java.time.LocalDate.now;

public class DefineScheduleService {

    private final ScheduleDurations scheduleDurations;
    private final FreeSlotsStorage storage;

    public DefineScheduleService(ScheduleDurations scheduleDurations, FreeSlotsStorage storage) {
        this.scheduleDurations = scheduleDurations;
        this.storage = storage;
    }

    public ScheduleId addSchedule(ScheduleHours scheduleHours, Duration duration) {
        ScheduleId scheduleId = ScheduleId.newId();
        scheduleDurations.defineDuration(scheduleId, duration);
        generateFreeSlots(new Schedule(scheduleId, scheduleHours));
        return scheduleId;
    }

    public ScheduleId addSchedule(LocalTime startTime, LocalTime endTime, Validity validity, Duration duration) {
        ScheduleId scheduleId = addSchedule(startTime, endTime, validity);
        scheduleDurations.defineDuration(scheduleId, duration);
        return scheduleId;
    }

    public ScheduleId addSchedule(LocalTime startTime, LocalTime endTime, Validity validity) {
        ScheduleId scheduleId = ScheduleId.newId();
        generateFreeSlots(new Schedule(new ScheduleHours(startTime, endTime), validity, scheduleId));
        return scheduleId;
    }

    private void generateFreeSlots(Schedule schedule) {
        this.storage.addAll(schedule.buildFreeSlots(now()));
    }
}
