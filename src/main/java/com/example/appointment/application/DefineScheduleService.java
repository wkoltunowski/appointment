package com.example.appointment.application;

import com.example.appointment.domain.*;

import java.time.Duration;

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

    public ScheduleId addSchedule(Validity validity, Duration duration, ScheduleHours scheduleHours) {
        ScheduleId scheduleId = addSchedule(validity, scheduleHours);
        scheduleDurations.defineDuration(scheduleId, duration);
        return scheduleId;
    }

    public ScheduleId addSchedule(Validity validity, ScheduleHours scheduleHours) {
        ScheduleId scheduleId = ScheduleId.newId();
        generateFreeSlots(new Schedule(scheduleHours, validity, scheduleId));
        return scheduleId;
    }

    private void generateFreeSlots(Schedule schedule) {
        this.storage.addAll(schedule.buildFreeSlots(now()));
    }
}
