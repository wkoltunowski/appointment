package com.example.appointment.infrastructure;

import com.example.appointment.domain.schedule.DaysDomain;
import com.example.appointment.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.domain.freescheduleranges.FreeScheduleSlot;
import com.example.appointment.domain.freescheduleranges.FreeSlotRepository;
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

    private final Map<LocalDate, Collection<FreeScheduleSlot>> index = new HashMap<>();
    private LocalDate maxDay = LocalDate.MIN;
    private final Map<ScheduleId, List<FreeScheduleSlot>> slotByScheduleId = new HashMap<>();

    @Override
    public void remove(FreeScheduleSlot freeScheduleSlot) {
        LocalDate day = freeScheduleSlot.start().toLocalDate();
        Collection<FreeScheduleSlot> freeScheduleSlots = index.get(day);
        freeScheduleSlots.remove(freeScheduleSlot);
        if (freeScheduleSlots.isEmpty()) {
            index.remove(day);
        }
        ScheduleId scheduleId = freeScheduleSlot.scheduleId();
        List<FreeScheduleSlot> scheduleSlots = slotByScheduleId.get(scheduleId);
        scheduleSlots.remove(freeScheduleSlot);
        if (scheduleSlots.isEmpty()) {
            slotByScheduleId.remove(scheduleId);
        }
    }

    @Override
    public long size() {
        return index.entrySet().stream().map(Map.Entry::getValue).flatMap(Collection::stream).count();
    }

    @Override
    public void addAll(Collection<FreeScheduleSlot> freeScheduleSlots) {
        for (FreeScheduleSlot freeScheduleSlot : freeScheduleSlots) {
            add(freeScheduleSlot);
        }
    }

    private void add(FreeScheduleSlot of) {
        LocalDate slotStartDay = of.start().toLocalDate();
        Collection<FreeScheduleSlot> freeScheduleSlots = index.get(slotStartDay);
        Collection<FreeScheduleSlot> slots = Optional.ofNullable(freeScheduleSlots).orElseGet(() -> {
            Collection<FreeScheduleSlot> treeSet = new ArrayList<FreeScheduleSlot>();
            index.put(slotStartDay, treeSet);
            return treeSet;
        });
        slots.add(of);
        if (slotStartDay.isAfter(maxDay)) {
            maxDay = slotStartDay;
        }

        List<FreeScheduleSlot> scheduleSlots = Optional.ofNullable(slotByScheduleId.get(of.scheduleId())).orElseGet(() -> {
            List<FreeScheduleSlot> newList = newArrayList();
            slotByScheduleId.put(of.scheduleId(), newList);
            return newList;
        });
        scheduleSlots.add(of);
    }

    @Override
    public Iterable<FreeScheduleSlot> findAfter(LocalDate localDate) {
        if (!localDate.isAfter(maxDay)) {
            Stream<FreeScheduleSlot> collectionStream = ContiguousSet
                    .create(Range.closed(localDate, maxDay), DaysDomain.daysDomain())
                    .stream()
                    .map(this::dayFreeSlots)
                    .flatMap(Collection::stream);
            return collectionStream::iterator;
        }
        return () -> Collections.<FreeScheduleSlot>emptyList().iterator();
    }

    @Override
    public Optional<FreeScheduleSlot> findByAppointment(ScheduleRange scheduleRange) {
        return slotsByScheduleId(scheduleRange.scheduleId())
                .stream()
                .filter(fs -> fs.contains(scheduleRange.range()))
                .findFirst();
    }

    private List<FreeScheduleSlot> slotsByScheduleId(ScheduleId scheduleId) {
        return ImmutableList.copyOf(Optional.ofNullable(slotByScheduleId.get(scheduleId)).orElse(emptyList()));
    }

    @Override
    public List<FreeScheduleSlot> findByScheduleId(ScheduleId scheduleId) {
        return slotsByScheduleId(scheduleId);
    }

    private Collection<FreeScheduleSlot> dayFreeSlots(LocalDate day) {
        return Optional.ofNullable(index.get(day)).orElse(emptyList());
    }


}
