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
import static java.util.Collections.synchronizedList;

@Component
public class DayCollectionFreeScheduleSlotRepository implements FreeScheduleSlotRepository {

    private final Map<LocalDate, ImmutableList<FreeScheduleSlot>> index = new HashMap<>();
    private LocalDate maxDay = LocalDate.MIN;
    private final Map<ScheduleId, ImmutableList<FreeScheduleSlot>> slotByScheduleId = new HashMap<>();
    private Map<ScheduleId, SearchTags> tags = new HashMap<>();

    @Override
    public void remove(FreeScheduleSlot freeScheduleSlot) {
        LocalDate day = freeScheduleSlot.start().toLocalDate();
        ImmutableList<FreeScheduleSlot> freeScheduleSlots = index.getOrDefault(day, ImmutableList.of());
        if (freeScheduleSlots.size() > 1) {
            ArrayList<FreeScheduleSlot> newSlots = new ArrayList<>(freeScheduleSlots);
            newSlots.remove(freeScheduleSlot);
            index.put(day, ImmutableList.copyOf(newSlots));
        } else {
            index.remove(day);
        }


        ScheduleId scheduleId = freeScheduleSlot.scheduleId();
        ImmutableList<FreeScheduleSlot> scheduleSlots = slotByScheduleId.getOrDefault(scheduleId, ImmutableList.of());

        if (scheduleSlots.size() > 1) {
            ArrayList<FreeScheduleSlot> newSlots = new ArrayList<>(scheduleSlots);
            newSlots.remove(freeScheduleSlot);
            slotByScheduleId.put(scheduleId, ImmutableList.copyOf(newSlots));
        } else {
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

    private void add(FreeScheduleSlot freeScheduleSlot) {
        LocalDate slotStartDay = freeScheduleSlot.start().toLocalDate();
        ArrayList<FreeScheduleSlot> freeScheduleSlots = newArrayList(index.getOrDefault(slotStartDay, ImmutableList.of()));
        freeScheduleSlots.add(freeScheduleSlot);
        index.put(slotStartDay, ImmutableList.copyOf(freeScheduleSlots));

        if (slotStartDay.isAfter(maxDay)) {
            maxDay = slotStartDay;
        }

        List<FreeScheduleSlot> scheduleSlots = newArrayList(slotByScheduleId.getOrDefault(freeScheduleSlot.scheduleId(), ImmutableList.of()));
        scheduleSlots.add(freeScheduleSlot);
        slotByScheduleId.put(freeScheduleSlot.scheduleId(), ImmutableList.copyOf(scheduleSlots));

        tags.put(freeScheduleSlot.scheduleId(), freeScheduleSlot.searchTags());
    }

    @Override
    public Optional<FreeScheduleSlot> findByScheduleRange(ScheduleRange scheduleRange) {
        return slotsByScheduleId(scheduleRange.scheduleId())
                .stream()
                .filter(fs -> fs.contains(scheduleRange.range()))
                .findFirst();
    }


    private List<FreeScheduleSlot> slotsByScheduleId(ScheduleId scheduleId) {
        return slotByScheduleId.getOrDefault(scheduleId, ImmutableList.of());
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
            private Iterator<FreeScheduleSlot> iterator = synchronizedList(dayFreeSlots(day)).iterator();
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
        return index.getOrDefault(day, ImmutableList.of());
    }

    @Override
    public SearchTags findTags(ScheduleId scheduleId) {
        return tags.get(scheduleId);
    }


}
