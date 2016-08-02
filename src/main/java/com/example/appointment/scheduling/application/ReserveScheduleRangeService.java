package com.example.appointment.scheduling.application;

import com.example.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlot;
import com.example.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

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
}
