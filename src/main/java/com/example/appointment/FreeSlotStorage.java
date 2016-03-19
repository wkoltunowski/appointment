package com.example.appointment;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.example.appointment.domain.FreeSlot;
import com.example.appointment.domain.ScheduleId;

public interface FreeSlotStorage {

  void remove(FreeSlot freeSlot);

  long size();

  void add(FreeSlot slot);

  void addAll(List<FreeSlot> freeSlots);

  Iterable<FreeSlot> findAfter(LocalDate localDate);

  Collection<FreeSlot> findByScheduleId(ScheduleId scheduleId);
}
