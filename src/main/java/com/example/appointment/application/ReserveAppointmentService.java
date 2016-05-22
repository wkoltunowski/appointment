package com.example.appointment.application;

import com.example.appointment.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.domain.freescheduleranges.FreeScheduleSlot;
import com.example.appointment.domain.freescheduleranges.FreeSlotRepository;

import java.util.Optional;

public class ReserveAppointmentService {
    private final FreeSlotRepository freeSlotRepository;

    public ReserveAppointmentService(FreeSlotRepository freeSlotRepository) {
        this.freeSlotRepository = freeSlotRepository;
    }

    public void reserve(ScheduleRange scheduleRange) {
        Optional<FreeScheduleSlot> scheduleSlots = this.freeSlotRepository.findByScheduleRange(scheduleRange);
        FreeScheduleSlot freeScheduleSlot = scheduleSlots.orElseThrow(AppointmentTakenException::new);
        this.freeSlotRepository.remove(freeScheduleSlot);
        this.freeSlotRepository.addAll(freeScheduleSlot.splitFor(scheduleRange.range()));
    }
}
