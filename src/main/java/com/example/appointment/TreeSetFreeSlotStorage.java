package com.example.appointment;

import java.time.Duration;
import java.time.LocalDate;
import java.util.TreeSet;

import com.example.appointment.domain.FreeSlot;
import com.example.appointment.domain.FromTo;
import com.example.appointment.domain.ScheduleId;

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
  public Iterable<FreeSlot> findAfter(LocalDate localDate) {
    FreeSlot fromElement = FreeSlot.of(ScheduleId.newId(),
        new FromTo(localDate.atTime(0, 0), localDate.atTime(0, 1)),
        Duration.ofHours(1));
    FreeSlot ceiling = freeSlots.ceiling(fromElement);
    return freeSlots.tailSet(ceiling);
  }

  @Override
  public Iterable<FreeSlot> allSlots() {
    return freeSlots;
  }
}
