package com.example.appointment.application;

import com.example.appointment.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.domain.freescheduleranges.FreeScheduleSlot;
import com.example.appointment.domain.freescheduleranges.FreeScheduleSlotRepository;

import java.util.Optional;

public class ReserveScheduleRangeService {
    private final FreeScheduleSlotRepository freeScheduleSlotRepository;

    public ReserveScheduleRangeService(FreeScheduleSlotRepository freeScheduleSlotRepository) {
        this.freeScheduleSlotRepository = freeScheduleSlotRepository;
    }

    public void reserve(ScheduleRange scheduleRange) {
        Optional<FreeScheduleSlot> scheduleSlots = this.freeScheduleSlotRepository.findByScheduleRange(scheduleRange);
        FreeScheduleSlot freeScheduleSlot = scheduleSlots.orElseThrow(AppointmentTakenException::new);
        this.freeScheduleSlotRepository.remove(freeScheduleSlot);
        this.freeScheduleSlotRepository.addAll(freeScheduleSlot.splitFor(scheduleRange.range()));
    }
}
