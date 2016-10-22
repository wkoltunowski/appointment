package com.falco.appointment.scheduling.application;

import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlot;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        List<FreeScheduleSlot> scheduleSlots = this.freeScheduleSlotRepository.findByScheduleId(scheduleRange.scheduleId());
        scheduleSlots.get(0).searchTags();
        this.freeScheduleSlotRepository.addAll(asList(new FreeScheduleSlot(scheduleRange.scheduleId(), scheduleRange.range(), scheduleSlots.get(0).searchTags())));
    }
}
