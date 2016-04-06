package com.example.appointment.domain.freeslot;

import com.example.appointment.domain.schedule.ScheduleId;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FreeSlotRepository {

    void addAll(Collection<FreeSlot> freeSlots);

    void remove(FreeSlot freeSlot);

    long size();

    Iterable<FreeSlot> findAfter(LocalDate localDate);

    Optional<FreeSlot> findByAppointment(FreeAppointment freeAppointment);

    List<FreeSlot> findByScheduleId(ScheduleId scheduleId);
}
