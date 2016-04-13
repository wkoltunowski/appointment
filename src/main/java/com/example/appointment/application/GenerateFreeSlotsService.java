package com.example.appointment.application;

import com.example.appointment.domain.freescheduleranges.FreeScheduleSlot;
import com.example.appointment.domain.freescheduleranges.FreeSlotRepository;
import com.example.appointment.domain.freescheduleranges.SearchTags;
import com.example.appointment.domain.schedule.Schedule;
import com.example.appointment.domain.schedule.ScheduleConnections;
import com.example.appointment.domain.schedule.ScheduleId;
import com.example.appointment.domain.schedule.ScheduleRepository;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

public class GenerateFreeSlotsService {
    private final ScheduleRepository scheduleRepository;
    private final FreeSlotRepository freeSlotRepository;

    public GenerateFreeSlotsService(ScheduleRepository scheduleRepository, FreeSlotRepository freeSlotRepository) {
        this.scheduleRepository = scheduleRepository;
        this.freeSlotRepository = freeSlotRepository;
    }

    public void generateFreeSlots(ScheduleId scheduleId) {
        Range<LocalDate> generationRange = Range.closed(now(), now().plus(Period.ofDays(90)));
        generateFreeSlotsInRange(scheduleId, generationRange);
    }

    public void generateFreeSlotsInRange(ScheduleId scheduleId, Range<LocalDate> generationRange) {
        Schedule schedule = this.scheduleRepository.findById(scheduleId);
        freeSlotRepository.addAll(scheduleFreeSlots(schedule, generationRange));
    }

    private List<FreeScheduleSlot> scheduleFreeSlots(Schedule schedule, Range<LocalDate> generationRange) {
        ScheduleId scheduleId = schedule.scheduleId();
        SearchTags searchTags = searchTagsFor(schedule.scheduleDefinition());
        List<Range<LocalDateTime>> rangesList = schedule.dates(generationRange);
        return rangesList
                .stream()
                .map(slotRange -> FreeScheduleSlot.of(scheduleId, slotRange, searchTags))
                .collect(toList());

    }

    public void regenerateFreeSlots(ScheduleId scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        SearchTags searchTags = searchTagsFor(schedule.scheduleDefinition());
        List<FreeScheduleSlot> scheduleSlots = freeSlotRepository.findByScheduleId(scheduleId);
        for (FreeScheduleSlot scheduleSlot : scheduleSlots) {
            FreeScheduleSlot newSlot = scheduleSlot.withSearchTags(searchTags);
            freeSlotRepository.remove(scheduleSlot);
            freeSlotRepository.addAll(Collections.singleton(newSlot));
        }
    }

    private SearchTags searchTagsFor(ScheduleConnections scheduleDefinition) {
        SearchTags searchTags = SearchTags.empty();
        if (scheduleDefinition.doctorId().isPresent()) {
            searchTags = searchTags.forDoctor(scheduleDefinition.doctorId().get().toString());
        }
        if (scheduleDefinition.locationId().isPresent()) {
            searchTags = searchTags.forLocation(scheduleDefinition.locationId().get().toString());
        }
        if (scheduleDefinition.serviceId().isPresent()) {
            searchTags = searchTags.forService(scheduleDefinition.serviceId().get().toString());
        }
        return searchTags;
    }
}
