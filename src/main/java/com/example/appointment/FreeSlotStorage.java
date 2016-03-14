package com.example.appointment;

import java.time.LocalDate;

import com.example.appointment.domain.FreeSlot;

public interface FreeSlotStorage {

  void remove(FreeSlot freeSlot);

  long size();

  void add(FreeSlot slot);

  Iterable<FreeSlot> findAfter(LocalDate localDate);

  Iterable<FreeSlot> allSlots();
}
