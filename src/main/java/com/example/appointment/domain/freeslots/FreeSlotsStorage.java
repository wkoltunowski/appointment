package com.example.appointment.domain.freeslots;

import java.time.LocalDate;
import java.util.Collection;

public interface FreeSlotsStorage {

    void add(FreeSlot slot);

    void addAll(Collection<FreeSlot> freeSlots);

    void remove(FreeSlot freeSlot);

    long size();

    Iterable<FreeSlot> findAfter(LocalDate localDate);

}