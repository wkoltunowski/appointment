package com.example.appointment.application;

import com.example.appointment.domain.appointment.Appointment;
import com.example.appointment.domain.appointment.AppointmentTakenException;
import com.example.appointment.domain.freeslot.FreeSlot;
import com.example.appointment.domain.freeslot.FreeSlotRepository;
import com.example.appointment.domain.schedule.ScheduleId;

import java.util.List;

public class ReserveAppointmentService {
    private final FreeSlotRepository storage;

    public ReserveAppointmentService(FreeSlotRepository storage) {
        this.storage = storage;
    }

    public void reserve(Appointment appointment) {
        ScheduleId scheduleId = appointment.scheduleId();
        List<FreeSlot> scheduleSlots = this.storage.findByScheduleId(scheduleId);
        FreeSlot freeSlot = scheduleSlots.stream().filter(fs -> fs.contains(appointment.range()))
                .findFirst()
                .orElseThrow(AppointmentTakenException::new);
        this.storage.remove(freeSlot);
        this.storage.addAll(freeSlot.splitFor(appointment.range()));
    }


}
