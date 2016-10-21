package com.example.appointment.scheduling.infrastructure;

import com.example.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlot;
import com.example.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.scheduling.domain.schedule.DaysDomain;
import com.example.appointment.scheduling.domain.schedule.ScheduleId;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;

@Component
public class DayCollectionFreeScheduleSlotRepository implements FreeScheduleSlotRepository {

    private final Map<LocalDate, List<FreeScheduleSlot>> index = new HashMap<>();
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
            List<FreeScheduleSlot> list = new ArrayList<FreeScheduleSlot>();
            index.put(slotStartDay, list);
            return list;
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
    public Optional<FreeScheduleSlot> findByScheduleRange(ScheduleRange scheduleRange) {
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


    public Iterable<FreeScheduleSlot> findAfterStream(LocalDateTime startingFrom) {
        LocalDate localDate = startingFrom.toLocalDate();
        if (!localDate.isAfter(maxDay)) {
            Stream<FreeScheduleSlot> collectionStream = ContiguousSet
                    .create(Range.closed(localDate, maxDay), DaysDomain.daysDomain())
                    .stream()
                    .map(this::dayFreeSlots)
                    .flatMap(Collection::stream)
                    .filter(slot -> slot.end().isAfter(startingFrom));
            return collectionStream::iterator;
        }
        return () -> Collections.<FreeScheduleSlot>emptyList().iterator();
    }

    @Override
    public Iterable<FreeScheduleSlot> findAfter(LocalDateTime startingFrom) {
        return findAfterWhile(startingFrom);
//        return findAfterStream(startingFrom);
    }

    private Iterable<FreeScheduleSlot> findAfterWhile(final LocalDateTime startingFrom) {
        return () -> {
            final LocalDate[] date = {startingFrom.toLocalDate()};
            final int[] slotIndex = {0};
            return new AbstractIterator<FreeScheduleSlot>() {
                @Override
                protected FreeScheduleSlot computeNext() {
                    while (!date[0].isAfter(maxDay)) {
                        List<FreeScheduleSlot> dayFreeSlots = dayFreeSlots(date[0]);
                        if (!dayFreeSlots.isEmpty() && slotIndex[0] < dayFreeSlots.size()) {
                            FreeScheduleSlot freeScheduleSlot = dayFreeSlots.get(slotIndex[0]++);
                            if (freeScheduleSlot.end().isAfter(startingFrom)) {
                                return freeScheduleSlot;
                            }
                        }
                        slotIndex[0] = 0;
                        date[0] = date[0].plusDays(1);
                    }
                    return endOfData();
                }
            };
        };
    }

    private List<FreeScheduleSlot> dayFreeSlots(LocalDate day) {
        return Optional.ofNullable(index.get(day)).orElse(emptyList());
    }


}
