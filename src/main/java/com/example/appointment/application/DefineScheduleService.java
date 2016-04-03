package com.example.appointment.application;

import com.example.appointment.domain.freeslot.FreeSlotRepository;
import com.example.appointment.domain.schedule.*;

import java.time.Duration;

import static java.time.LocalDate.now;

public class DefineScheduleService {

    private final ScheduleDurations scheduleDurations;
    private final FreeSlotRepository storage;
    private final ScheduleRepository scheduleRepository;

    public DefineScheduleService(ScheduleDurations scheduleDurations, FreeSlotRepository storage, ScheduleRepository scheduleRepository) {
        this.scheduleDurations = scheduleDurations;
        this.storage = storage;
        this.scheduleRepository = scheduleRepository;
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
        generateFreeSlots(new Schedule(scheduleId, scheduleHours, validity, SearchTags.empty()));
        return scheduleId;
    }

    public ScheduleId addSchedule(ScheduleHours scheduleHours, Duration duration, SearchTags tags) {
        ScheduleId scheduleId = ScheduleId.newId();
        scheduleDurations.defineDuration(scheduleId, duration);
        generateFreeSlots(new Schedule(scheduleId, scheduleHours, Validity.infinite(), tags));
        return scheduleId;
    }

    private void generateFreeSlots(Schedule schedule) {
        this.scheduleRepository.save(schedule);
        this.storage.addAll(schedule.buildFreeSlots(now()));
    }
}
