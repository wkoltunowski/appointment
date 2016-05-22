package com.example.appointment.domain.freescheduleranges;

import com.example.appointment.domain.schedule.ScheduleId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FreeSlotRepository {

    void addAll(Collection<FreeScheduleSlot> freeScheduleSlots);

    void remove(FreeScheduleSlot freeScheduleSlot);

    long size();

    Optional<FreeScheduleSlot> findByScheduleRange(ScheduleRange scheduleRange);

    List<FreeScheduleSlot> findByScheduleId(ScheduleId scheduleId);

    Iterable<FreeScheduleSlot> findAfter(LocalDateTime startingFrom);
}
