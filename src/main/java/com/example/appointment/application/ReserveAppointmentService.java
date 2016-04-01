package com.example.appointment.application;

import com.example.appointment.domain.appointment.Appointment;
import com.example.appointment.domain.appointment.AppointmentTakenException;
import com.example.appointment.domain.freeslot.FreeSlot;
import com.example.appointment.domain.freeslot.FreeSlotsStorage;
import com.example.appointment.domain.schedule.ScheduleId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class ReserveAppointmentService {
    private final FreeSlotsStorage storage;

    public ReserveAppointmentService(FreeSlotsStorage storage) {
        this.storage = storage;
    }

    public void reserve(Appointment appointment) {
        ScheduleId scheduleId = appointment.scheduleId();
        List<FreeSlot> scheduleSlots = this.storage.findByScheduleId(scheduleId);
//        FreeSlot freeSlot = findFirstFreeSlot(appointment);
        FreeSlot freeSlot = scheduleSlots.stream().filter(fs -> fs.contains(appointment.range()))
                .findFirst()
                .orElseThrow(AppointmentTakenException::new);
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
