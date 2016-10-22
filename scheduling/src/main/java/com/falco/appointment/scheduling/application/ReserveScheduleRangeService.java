package com.falco.appointment.scheduling.application;

import com.falco.appointment.scheduling.domain.SearchTags;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlot;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.falco.appointment.scheduling.domain.schedule.ScheduleId;
import com.google.common.collect.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Component
public class ReserveScheduleRangeService {
    private final FreeScheduleSlotRepository freeScheduleSlotRepository;

    @Autowired
    public ReserveScheduleRangeService(FreeScheduleSlotRepository freeScheduleSlotRepository) {
        this.freeScheduleSlotRepository = freeScheduleSlotRepository;
    }

    public void reserve(ScheduleRange scheduleRange) {
        Optional<FreeScheduleSlot> scheduleSlots = this.freeScheduleSlotRepository.findByScheduleRange(scheduleRange);
        FreeScheduleSlot freeScheduleSlot = scheduleSlots.orElseThrow(() -> new AppointmentTakenException());
        Collection<FreeScheduleSlot> freeScheduleSlots = freeScheduleSlot.splitFor(scheduleRange.range());

        this.freeScheduleSlotRepository.remove(freeScheduleSlot);
        this.freeScheduleSlotRepository.addAll(freeScheduleSlots);
    }

    public void cancel(ScheduleRange scheduleRange) {
        SearchTags searchTags = freeScheduleSlotRepository.findTags(scheduleRange.scheduleId());
        FreeScheduleSlot freeScheduleSlot = new FreeScheduleSlot(scheduleRange.scheduleId(), scheduleRange.range(), searchTags);


        Optional<FreeScheduleSlot> left = freeScheduleSlotRepository
                .findByScheduleId(scheduleRange.scheduleId())
                .stream()
                .filter(slot -> slot.end().equals(scheduleRange.start()))
                .findFirst();

        if (left.isPresent()) {
            FreeScheduleSlot leftSlot = left.get();
            this.freeScheduleSlotRepository.remove(leftSlot);
            freeScheduleSlot = leftSlot.withRange(Range.closedOpen(leftSlot.start(), scheduleRange.end()));
        }

        Optional<FreeScheduleSlot> right = freeScheduleSlotRepository
                .findByScheduleId(scheduleRange.scheduleId())
                .stream()
                .filter(slot -> slot.start().equals(scheduleRange.end()))
                .findFirst();
        if (right.isPresent()) {
            FreeScheduleSlot rightSlot = right.get();
            this.freeScheduleSlotRepository.remove(rightSlot);
            freeScheduleSlot = freeScheduleSlot.withRange(Range.closedOpen(freeScheduleSlot.start(), rightSlot.end()));
        }

        this.freeScheduleSlotRepository.addAll(asList(freeScheduleSlot));
    }


}
