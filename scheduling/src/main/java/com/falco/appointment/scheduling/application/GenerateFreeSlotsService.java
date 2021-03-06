package com.falco.appointment.scheduling.application;

import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlot;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.falco.appointment.scheduling.api.SearchTags;
import com.falco.appointment.scheduling.domain.schedule.Schedule;
import com.falco.appointment.scheduling.api.ScheduleId;
import com.falco.appointment.scheduling.domain.schedule.ScheduleRepository;
import com.google.common.collect.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

@Component
public class GenerateFreeSlotsService {
    private final ScheduleRepository scheduleRepository;
    private final FreeScheduleSlotRepository freeScheduleSlotRepository;

    @Autowired
    public GenerateFreeSlotsService(ScheduleRepository scheduleRepository, FreeScheduleSlotRepository freeScheduleSlotRepository) {
        this.scheduleRepository = scheduleRepository;
        this.freeScheduleSlotRepository = freeScheduleSlotRepository;
    }

    public void generateFreeSlots(ScheduleId scheduleId) {
        Range<LocalDate> generationRange = Range.closed(now(), now().plus(Period.ofDays(90)));
        generateFreeSlotsInRange(scheduleId, generationRange);
    }

    public void generateFreeSlotsInRange(ScheduleId scheduleId, Range<LocalDate> generationRange) {
        Schedule schedule = this.scheduleRepository.findById(scheduleId);
        freeScheduleSlotRepository.addAll(scheduleFreeSlots(schedule, generationRange));
    }

    private List<FreeScheduleSlot> scheduleFreeSlots(Schedule schedule, Range<LocalDate> generationRange) {
        ScheduleId scheduleId = schedule.scheduleId();
        SearchTags searchTags = schedule.searchTags();
        List<Range<LocalDateTime>> rangesList = schedule.dates(generationRange);
        return rangesList
                .stream()
                .map(slotRange -> FreeScheduleSlot.of(scheduleId, slotRange, searchTags))
                .collect(toList());

    }

    public void regenerateFreeSlots(ScheduleId scheduleId) {
        List<FreeScheduleSlot> scheduleSlots = freeScheduleSlotRepository.findByScheduleId(scheduleId);
        Schedule schedule = scheduleRepository.findById(scheduleId);
        SearchTags searchTags = schedule.searchTags();
        for (FreeScheduleSlot scheduleSlot : scheduleSlots) {
            FreeScheduleSlot newSlot = scheduleSlot.withSearchTags(searchTags);
            freeScheduleSlotRepository.remove(scheduleSlot);
            freeScheduleSlotRepository.addAll(Collections.singleton(newSlot));
        }
    }


}
