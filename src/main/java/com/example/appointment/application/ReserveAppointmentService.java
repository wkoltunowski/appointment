package com.example.appointment.application;

import com.example.appointment.domain.freeslot.FreeAppointment;
import com.example.appointment.domain.freeslot.AppointmentTakenException;
import com.example.appointment.domain.freeslot.FreeSlot;
import com.example.appointment.domain.freeslot.FreeSlotRepository;

import java.util.Optional;

public class ReserveAppointmentService {
    private final FreeSlotRepository freeSlotRepository;

    public ReserveAppointmentService(FreeSlotRepository freeSlotRepository) {
        this.freeSlotRepository = freeSlotRepository;
    }

    public void reserve(FreeAppointment freeAppointment) {
        adjustFreeSlots(freeAppointment);
    }

    private void adjustFreeSlots(FreeAppointment freeAppointment) {
        Optional<FreeSlot> scheduleSlots = this.freeSlotRepository.findByAppointment(freeAppointment);
        FreeSlot freeSlot = scheduleSlots.orElseThrow(AppointmentTakenException::new);
        this.freeSlotRepository.remove(freeSlot);
        this.freeSlotRepository.addAll(freeSlot.splitFor(freeAppointment.range()));
    }


}
