package com.example.appointment.infrastructure;

import com.example.appointment.domain.ScheduleId;
import com.example.appointment.domain.FreeSlot;
import com.example.appointment.domain.FreeSlotsStorage;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

public class TreeSetFreeSlotsStorage implements FreeSlotsStorage {

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
    public void addAll(Collection<FreeSlot> freeSlots) {
        this.freeSlots.addAll(freeSlots);
    }

    @Override
    public Iterable<FreeSlot> findAfter(LocalDate localDate) {
        FreeSlot fromElement = FreeSlot.of(ScheduleId.newId(),
                Range.closedOpen(localDate.atTime(0, 0), localDate.atTime(0, 1)));
        FreeSlot ceiling = freeSlots.ceiling(fromElement);
        return ceiling != null ? freeSlots.tailSet(ceiling) : Collections.emptyList();
    }

}
