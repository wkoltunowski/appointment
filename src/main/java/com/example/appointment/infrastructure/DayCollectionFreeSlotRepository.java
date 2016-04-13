package com.example.appointment.infrastructure;

import com.example.appointment.domain.schedule.DaysDomain;
import com.example.appointment.domain.freeslot.ScheduleRange;
import com.example.appointment.domain.freeslot.FreeSlot;
import com.example.appointment.domain.freeslot.FreeSlotRepository;
import com.example.appointment.domain.schedule.ScheduleId;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;

public class DayCollectionFreeSlotRepository implements FreeSlotRepository {

    private final Map<LocalDate, Collection<FreeSlot>> index = new HashMap<>();
    private LocalDate maxDay = LocalDate.MIN;
    private final Map<ScheduleId, List<FreeSlot>> slotByScheduleId = new HashMap<>();

    @Override
    public void remove(FreeSlot freeSlot) {
        LocalDate day = freeSlot.start().toLocalDate();
        Collection<FreeSlot> freeSlots = index.get(day);
        freeSlots.remove(freeSlot);
        if (freeSlots.isEmpty()) {
            index.remove(day);
        }
        ScheduleId scheduleId = freeSlot.scheduleId();
        List<FreeSlot> scheduleSlots = slotByScheduleId.get(scheduleId);
        scheduleSlots.remove(freeSlot);
        if (scheduleSlots.isEmpty()) {
            slotByScheduleId.remove(scheduleId);
        }
    }

    @Override
    public long size() {
        return index.entrySet().stream().map(Map.Entry::getValue).flatMap(Collection::stream).count();
    }

    @Override
    public void addAll(Collection<FreeSlot> freeSlots) {
        for (FreeSlot freeSlot : freeSlots) {
            add(freeSlot);
        }
    }

    private void add(FreeSlot of) {
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

        List<FreeSlot> scheduleSlots = Optional.ofNullable(slotByScheduleId.get(of.scheduleId())).orElseGet(() -> {
            List<FreeSlot> newList = newArrayList();
            slotByScheduleId.put(of.scheduleId(), newList);
            return newList;
        });
        scheduleSlots.add(of);
    }

    @Override
    public Iterable<FreeSlot> findAfter(LocalDate localDate) {
        if (!localDate.isAfter(maxDay)) {
            Stream<FreeSlot> collectionStream = ContiguousSet
                    .create(Range.closed(localDate, maxDay), DaysDomain.daysDomain())
                    .stream()
                    .map(this::dayFreeSlots)
                    .flatMap(Collection::stream);
            return collectionStream::iterator;
        }
        return () -> Collections.<FreeSlot>emptyList().iterator();
    }

    @Override
    public Optional<FreeSlot> findByAppointment(ScheduleRange scheduleRange) {
        return slotsByScheduleId(scheduleRange.scheduleId())
                .stream()
                .filter(fs -> fs.contains(scheduleRange.range()))
                .findFirst();
    }

    private List<FreeSlot> slotsByScheduleId(ScheduleId scheduleId) {
        return ImmutableList.copyOf(Optional.ofNullable(slotByScheduleId.get(scheduleId)).orElse(emptyList()));
    }

    @Override
    public List<FreeSlot> findByScheduleId(ScheduleId scheduleId) {
        return slotsByScheduleId(scheduleId);
    }

    private Collection<FreeSlot> dayFreeSlots(LocalDate day) {
        return Optional.ofNullable(index.get(day)).orElse(emptyList());
    }


}
