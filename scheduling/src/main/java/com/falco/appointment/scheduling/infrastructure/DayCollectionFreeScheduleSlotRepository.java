package com.falco.appointment.scheduling.infrastructure;

import com.falco.appointment.scheduling.api.SearchTags;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlot;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.falco.appointment.scheduling.api.ScheduleRange;
import com.falco.appointment.scheduling.domain.schedule.DaysDomain;
import com.falco.appointment.scheduling.api.ScheduleId;
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
    private Map<ScheduleId, SearchTags> tags = new HashMap<>();

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
        tags.put(of.scheduleId(), of.searchTags());
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


    @Override
    public Iterable<FreeScheduleSlot> findAfter(LocalDateTime startingFrom) {
        return findAfterWhile(startingFrom);
//        return findAfterStream(startingFrom);
    }

    private Iterable<FreeScheduleSlot> findAfterStream(LocalDateTime startingFrom) {
        LocalDate startingDay = startingFrom.toLocalDate();
        if (!startingDay.isAfter(maxDay)) {
            Stream<FreeScheduleSlot> collectionStream = ContiguousSet
                    .create(Range.closed(startingDay, maxDay), DaysDomain.daysDomain())
                    .stream()
                    .map(this::dayFreeSlots)
                    .flatMap(Collection::stream)
                    .filter(slot -> slot.end().isAfter(startingFrom));
            return collectionStream::iterator;
        }
        return () -> Collections.<FreeScheduleSlot>emptyList().iterator();
    }

    private Iterable<FreeScheduleSlot> findAfterWhile(final LocalDateTime startingFrom) {
        final LocalDate day = startingFrom.toLocalDate();
        return () -> new AbstractIterator<FreeScheduleSlot>() {
            private Iterator<FreeScheduleSlot> iterator = dayFreeSlots(day).iterator();
            private LocalDate date = day;
            @Override
            protected FreeScheduleSlot computeNext() {
                while (!date.isAfter(maxDay)) {
                    if (iterator.hasNext()) {
                        FreeScheduleSlot slot = iterator.next();
                        if (slot.end().isAfter(startingFrom)) {
                            return slot;
                        }
                    }
                    date = date.plusDays(1);
                    iterator = dayFreeSlots(date).iterator();
                }
                return endOfData();
            }
        };
    }

    private List<FreeScheduleSlot> dayFreeSlots(LocalDate day) {
        return Optional.ofNullable(index.get(day)).orElse(emptyList());
    }

    @Override
    public SearchTags findTags(ScheduleId scheduleId) {
        return tags.get(scheduleId);
    }


}
