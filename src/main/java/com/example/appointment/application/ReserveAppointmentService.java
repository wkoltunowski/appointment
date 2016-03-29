package com.example.appointment.application;

import com.example.appointment.domain.Appointment;
import com.example.appointment.domain.AppointmentTakenException;
import com.example.appointment.domain.FreeSlot;
import com.example.appointment.domain.FreeSlotsStorage;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class ReserveAppointmentService {
    private final FreeSlotsStorage storage;

    public ReserveAppointmentService(FreeSlotsStorage storage) {
        this.storage = storage;
    }

    public void reserve(Appointment appointment) {
        FreeSlot freeSlot = findFirstFreeSlot(appointment);
        this.storage.remove(freeSlot);
        this.storage.addAll(freeSlot.splitFor(appointment.range()));
    }


    private FreeSlot findFirstFreeSlot(Appointment appointment) {
        LocalDate day = appointment.start().toLocalDate();
        Iterable<FreeSlot> freeSlotsAfter = this.storage.findAfter(day);

        Optional<FreeSlot> freeSlotOptional = StreamSupport
                .stream(freeSlotsAfter.spliterator(), false)
                .filter(fs -> fs.validForAppointment(appointment))
                .findFirst();
        return freeSlotOptional.orElseThrow(AppointmentTakenException::new);
    }

}
