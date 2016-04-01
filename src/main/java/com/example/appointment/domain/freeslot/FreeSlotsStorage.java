package com.example.appointment.domain.freeslot;

import com.example.appointment.domain.freeslot.FreeSlot;
import com.example.appointment.domain.schedule.ScheduleId;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface FreeSlotsStorage {

    void addAll(Collection<FreeSlot> freeSlots);

    void remove(FreeSlot freeSlot);

    long size();

    Iterable<FreeSlot> findAfter(LocalDate localDate);

    List<FreeSlot> findByScheduleId(ScheduleId scheduleId);
}
