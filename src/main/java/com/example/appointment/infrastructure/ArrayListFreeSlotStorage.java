package com.example.appointment.infrastructure;

import java.time.LocalDate;
import java.util.*;

import com.example.appointment.domain.freeslots.FreeSlotStorage;
import com.example.appointment.domain.freeslots.FreeSlot;
import com.example.appointment.domain.ScheduleId;
import com.google.common.collect.Ranges;

public class ArrayListFreeSlotStorage implements FreeSlotStorage {

  private List<FreeSlot> index = new ArrayList<>();

  @Override
  public void remove(FreeSlot freeSlot) {
    this.index.remove(freeSlot);
  }

  @Override
  public long size() {
    return index.size();
  }

  @Override
  public void add(FreeSlot slot) {
    int newIndex = Collections.binarySearch(index, slot);
    index.add(-(newIndex) - 1, slot);
  }

  @Override
  public void addAll(List<FreeSlot> freeSlots) {
    for (FreeSlot freeSlot : freeSlots) {
      add(freeSlot);
    }
  }

  @Override
  public Iterable<FreeSlot> findAfter(LocalDate localDate) {
    FreeSlot fromElement = FreeSlot.of(ScheduleId.newId(), Ranges.closedOpen(
        localDate.atTime(0, 0),
        localDate.atTime(0, 1)));
    int index = Collections.binarySearch(this.index, fromElement);
    if (index < 0) {
      index = -(index) - 1;
    }
    return this.index.subList(index, this.index.size());
  }

}