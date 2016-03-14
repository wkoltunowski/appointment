package com.example.appointment;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.appointment.domain.FreeSlot;
import com.example.appointment.domain.FromTo;
import com.example.appointment.domain.ScheduleId;

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
  public Iterable<FreeSlot> findAfter(LocalDate localDate) {
    FreeSlot fromElement = FreeSlot.of(ScheduleId.newId(), new FromTo(
        localDate.atTime(0, 0),
        localDate.atTime(0, 1)),
        Duration.ofHours(1));
    int index = Collections.binarySearch(this.index, fromElement);
    if (index < 0) {
      index = -(index) - 1;
    }
    return this.index.subList(index, this.index.size());
  }

  @Override
  public Iterable<FreeSlot> allSlots() {
    return index;
  }
}
