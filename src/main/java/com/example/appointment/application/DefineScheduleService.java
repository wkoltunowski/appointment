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

    public ScheduleId givenSchedule(LocalTime from, LocalTime to, Duration duration) {
        ScheduleId scheduleId = ScheduleId.newId();
        scheduleDurations.defineDuration(scheduleId, duration);
        generateFreeSlots(new Schedule(from, to, scheduleId));
        return scheduleId;
    }

    public ScheduleId givenSchedule(LocalTime startTime, LocalTime endTime, Validity validity, Duration duration) {
        ScheduleId scheduleId = givenSchedule(startTime, endTime, validity);
        scheduleDurations.defineDuration(scheduleId, duration);
        return scheduleId;
    }

    public ScheduleId givenSchedule(LocalTime startTime, LocalTime endTime, Validity validity) {
        ScheduleId scheduleId = ScheduleId.newId();
        generateFreeSlots(new Schedule(startTime, endTime, validity, scheduleId));
        return scheduleId;
    }

    private void generateFreeSlots(Schedule schedule) {
        this.storage.addAll(schedule.buildFreeSlots(now()));
    }
}
