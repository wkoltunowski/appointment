package com.example.appointment.infrastructure;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import com.example.appointment.domain.freeslots.FreeSlotStorage;
import com.example.appointment.domain.freeslots.FreeSlot;
import com.example.appointment.domain.ScheduleId;
import com.google.common.collect.Ranges;

public class TreeSetFreeSlotStorage implements FreeSlotStorage {

  private final TreeSet<FreeSlot> freeSlots = new TreeSet<>();

  @Override
  public void remove(FreeSlot freeSlot) {
    freeSlots.remove(freeSlot);
  }

  @Override
  public long size() {
    return freeSlots.size();
  }

  @Override
  public void add(FreeSlot slot) {
    freeSlots.add(slot);
  }

  @Override
  public void addAll(List<FreeSlot> freeSlots) {
    this.freeSlots.addAll(freeSlots);
  }

  @Override
  public Iterable<FreeSlot> findAfter(LocalDate localDate) {
    FreeSlot fromElement = FreeSlot.of(ScheduleId.newId(),
        Ranges.closedOpen(localDate.atTime(0, 0), localDate.atTime(0, 1)));
    FreeSlot ceiling = freeSlots.ceiling(fromElement);
    return ceiling != null ? freeSlots.tailSet(ceiling) : Collections.emptyList();
  }

}
