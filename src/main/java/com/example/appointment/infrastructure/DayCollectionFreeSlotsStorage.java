package com.example.appointment.infrastructure;

import com.example.appointment.domain.ScheduleId;
import com.example.appointment.domain.freeslots.DaysDomain;
import com.example.appointment.domain.freeslots.FreeSlot;
import com.example.appointment.domain.freeslots.FreeSlotsStorage;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

public class DayCollectionFreeSlotsStorage implements FreeSlotsStorage {

    private Map<LocalDate, Collection<FreeSlot>> index = new HashMap<>();
    private LocalDate maxDay = LocalDate.MIN;
    private Map<ScheduleId, Collection<FreeSlot>> indexByScheduleId = new HashMap<>();

    @Override
    public void remove(FreeSlot freeSlot) {
        index.get(freeSlot.start().toLocalDate()).remove(freeSlot);
//    indexByScheduleId.get(freeSlot.getScheduleId()).remove(freeSlot);
    }

    @Override
    public long size() {
        return index.entrySet().stream().map(Map.Entry::getValue).flatMap(Collection::stream).count();
    }

    @Override
    public void add(FreeSlot of) {
        LocalDate slotStartDay = of.start().toLocalDate();
        Collection<FreeSlot> freeSlots = index.get(slotStartDay);
        Collection<FreeSlot> slots = Optional.ofNullable(freeSlots).orElseGet(() -> {
            Collection<FreeSlot> treeSet = new ArrayList<FreeSlot>();
            index.put(slotStartDay, treeSet);
            return treeSet;
        });
        slots.add(of);
        if (slotStartDay.isAfter(maxDay)) {
            maxDay = slotStartDay;
        }
    }

    @Override
    public void addAll(Collection<FreeSlot> freeSlots) {
        for (FreeSlot freeSlot : freeSlots) {
            add(freeSlot);
        }
    }

    @Override
    public Iterable<FreeSlot> findAfter(LocalDate localDate) {

        if (!localDate.isAfter(maxDay)) {
            Stream<FreeSlot> collectionStream =
                    ContiguousSet.create(Range.closed(localDate, maxDay), DaysDomain.daysDomain())
                            .stream()
                            .map(day -> dayFreeSlots(day))
                            .flatMap(Collection::stream);
            return collectionStream::iterator;
        }
        return () -> Collections.<FreeSlot>emptyList().iterator();
    }

    private Collection<FreeSlot> dayFreeSlots(LocalDate day) {
        return Optional.ofNullable(index.get(day)).orElse(emptyList());
    }


}
