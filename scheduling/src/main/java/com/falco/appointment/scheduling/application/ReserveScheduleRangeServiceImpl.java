package com.falco.appointment.scheduling.application;

import com.falco.appointment.scheduling.api.CancellationService;
import com.falco.appointment.scheduling.api.ReservationService;
import com.falco.appointment.scheduling.api.ScheduleRange;
import com.falco.appointment.scheduling.api.SearchTags;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlot;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.google.common.collect.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

import static java.util.Arrays.asList;

@Component
public class ReserveScheduleRangeServiceImpl implements CancellationService, ReservationService {
    private final FreeScheduleSlotRepository freeScheduleSlotRepository;

    @Autowired
    public ReserveScheduleRangeServiceImpl(FreeScheduleSlotRepository freeScheduleSlotRepository) {
        this.freeScheduleSlotRepository = freeScheduleSlotRepository;
    }

    @Override
    public void reserve(ScheduleRange scheduleRange) {
        Optional<FreeScheduleSlot> scheduleSlots = this.freeScheduleSlotRepository.findByScheduleRange(scheduleRange);
        FreeScheduleSlot freeScheduleSlot = scheduleSlots.orElseThrow(() -> new AppointmentTakenException());
        Collection<FreeScheduleSlot> freeScheduleSlots = freeScheduleSlot.splitFor(scheduleRange.range());

        this.freeScheduleSlotRepository.remove(freeScheduleSlot);
        this.freeScheduleSlotRepository.addAll(freeScheduleSlots);
    }

    @Override
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
