package com.example.appointment.domain;

import java.time.LocalDate;
import java.util.Collection;

public interface FreeSlotsStorage {

    void addAll(Collection<FreeSlot> freeSlots);

    void remove(FreeSlot freeSlot);

    long size();

    Iterable<FreeSlot> findAfter(LocalDate localDate);

}
