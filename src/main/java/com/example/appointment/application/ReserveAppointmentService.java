package com.example.appointment.application;

import com.example.appointment.domain.freeslot.AppointmentTakenException;
import com.example.appointment.domain.freeslot.ScheduleRange;
import com.example.appointment.domain.freeslot.FreeSlot;
import com.example.appointment.domain.freeslot.FreeSlotRepository;

import java.util.Optional;

public class ReserveAppointmentService {
    private final FreeSlotRepository freeSlotRepository;

    public ReserveAppointmentService(FreeSlotRepository freeSlotRepository) {
        this.freeSlotRepository = freeSlotRepository;
    }

    public void reserve(ScheduleRange scheduleRange) {
        Optional<FreeSlot> scheduleSlots = this.freeSlotRepository.findByAppointment(scheduleRange);
        FreeSlot freeSlot = scheduleSlots.orElseThrow(AppointmentTakenException::new);
        this.freeSlotRepository.remove(freeSlot);
        this.freeSlotRepository.addAll(freeSlot.splitFor(scheduleRange.range()));
    }


}
