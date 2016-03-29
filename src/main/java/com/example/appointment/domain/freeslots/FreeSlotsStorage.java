package com.example.appointment.domain.freeslots;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface FreeSlotsStorage {

    void remove(FreeSlot freeSlot);

    long size();

    void add(FreeSlot slot);

    void addAll(Collection<FreeSlot> freeSlots);

    Iterable<FreeSlot> findAfter(LocalDate localDate);


}
