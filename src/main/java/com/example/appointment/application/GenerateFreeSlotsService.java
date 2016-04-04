package com.example.appointment.application;

import com.example.appointment.domain.freeslot.FreeSlot;
import com.example.appointment.domain.freeslot.FreeSlotRepository;
import com.example.appointment.domain.schedule.Schedule;
import com.example.appointment.domain.schedule.ScheduleId;
import com.example.appointment.domain.schedule.ScheduleRepository;
import com.example.appointment.domain.schedule.SearchTags;
import com.google.common.collect.Range;

import java.time.Period;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDate.now;

public class GenerateFreeSlotsService {
    private final ScheduleRepository scheduleRepository;
    private final FreeSlotRepository freeSlotRepository;

    public GenerateFreeSlotsService(ScheduleRepository scheduleRepository, FreeSlotRepository freeSlotRepository) {
        this.scheduleRepository = scheduleRepository;
        this.freeSlotRepository = freeSlotRepository;
    }

    public void generateFreeSlots(ScheduleId scheduleId) {
        Schedule schedule = this.scheduleRepository.findById(scheduleId);
        Period advance = Period.ofDays(90);
        freeSlotRepository.addAll(schedule.buildFreeSlots(Range.closed(now(), now().plus(advance))));
    }

    public void regenerateFreeSlots(ScheduleId scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        SearchTags searchTags = schedule.searchTags();
        List<FreeSlot> scheduleSlots = freeSlotRepository.findByScheduleId(scheduleId);
        for (FreeSlot scheduleSlot : scheduleSlots) {
            FreeSlot newSlot = scheduleSlot.withSearchTags(searchTags);
            freeSlotRepository.remove(scheduleSlot);
            freeSlotRepository.addAll(Collections.singleton(newSlot));
        }
    }
}
