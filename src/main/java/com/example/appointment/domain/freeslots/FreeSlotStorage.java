package com.example.appointment.domain.freeslots;

import java.time.LocalDate;
import java.util.List;

public interface FreeSlotStorage {

    void remove(FreeSlot freeSlot);

    long size();

    void add(FreeSlot slot);

    void addAll(List<FreeSlot> freeSlots);

    Iterable<FreeSlot> findAfter(LocalDate localDate);


}
