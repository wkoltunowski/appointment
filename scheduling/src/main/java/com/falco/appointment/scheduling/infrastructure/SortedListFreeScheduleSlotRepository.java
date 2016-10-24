package com.falco.appointment.scheduling.infrastructure;

import com.falco.appointment.scheduling.domain.SearchTags;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlot;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.falco.appointment.scheduling.domain.schedule.ScheduleId;
import com.google.common.collect.Range;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

//@Component
public class SortedListFreeScheduleSlotRepository implements FreeScheduleSlotRepository {

    private final List<ComparableSlot> slots = new ArrayList<>();
    private Map<ScheduleId, SearchTags> tags = new HashMap<>();

    @Override
    public void addAll(Collection<FreeScheduleSlot> freeScheduleSlots) {
        for (FreeScheduleSlot newSlot : freeScheduleSlots) {
            ComparableSlot comparable = comparable(newSlot);
            int index = Collections.binarySearch(slots, comparable);
            slots.add(index >= 0 ? index : (-index - 1), comparable);
            tags.put(newSlot.scheduleId(), newSlot.searchTags());
        }
    }

    @Override
    public void remove(FreeScheduleSlot freeScheduleSlot) {
        int indexOfFirst = firstIndexOf(freeScheduleSlot.start());
        for (FreeScheduleSlot slot : slots.subList(indexOfFirst, slots.size())) {
            if (slot.equals(freeScheduleSlot)) {
                slots.remove(slot);
                return;
            }
        }
    }

    @Override
    public long size() {
        return slots.size();
    }

    @Override
    public Optional<FreeScheduleSlot> findByScheduleRange(ScheduleRange scheduleRange) {
        Iterable<FreeScheduleSlot> after = findAfter(scheduleRange.start().toLocalDate().atStartOfDay());
        return StreamSupport
                .stream(after.spliterator(), false)
                .filter(s -> s.scheduleId().equals(scheduleRange.scheduleId()))
                .filter(s -> s.contains(scheduleRange.range()))
                .findFirst();
    }

    @Override
    public List<FreeScheduleSlot> findByScheduleId(ScheduleId scheduleId) {
        return slots.stream().filter(s -> s.scheduleId().equals(scheduleId)).collect(toList());
    }

    @Override
    public Iterable<FreeScheduleSlot> findAfter(LocalDateTime startingFrom) {
        int index = firstIndexOf(startingFrom);
        if (index < slots.size()) {
            return (List) slots.subList(index, slots.size());
        }
        return Collections.emptyList();
    }

    @Override
    public SearchTags findTags(ScheduleId scheduleId) {
        return tags.get(scheduleId);
    }

    private int firstIndexOf(LocalDateTime dateTime) {
        ComparableSlot comparableSlot = comparableSlot(dateTime, ScheduleId.empty());
        int fromIndex = Collections.binarySearch(slots, comparableSlot);

        int index = 0;
        if (fromIndex < 0) {
            index = -fromIndex - 1;
        } else {
            index = fromIndex;
        }

        while (index < slots.size() && index > 0 && slots.get(index - 1).contains(Range.closed(dateTime, dateTime))) {
            index--;
        }
        return index;
    }

    private ComparableSlot comparable(FreeScheduleSlot newSlot) {
        return new ComparableSlot(newSlot.scheduleId(), newSlot.range(), newSlot.searchTags());
    }

    private ComparableSlot comparableSlot(LocalDateTime startingFrom, ScheduleId scheduleId) {
        return new ComparableSlot(scheduleId, Range.closed(startingFrom, startingFrom), SearchTags.empty());
    }

    private static class ComparableSlot extends FreeScheduleSlot implements Comparable<ComparableSlot> {
        public ComparableSlot(ScheduleId scheduleId, Range<LocalDateTime> range, SearchTags searchTags) {
            super(scheduleId, range, searchTags);
        }

        @Override
        public int compareTo(ComparableSlot o) {
            Comparator<FreeScheduleSlot> freeSlotComparator = Comparator
                    .comparing(FreeScheduleSlot::start)
                    .thenComparing(FreeScheduleSlot::end)
                    .thenComparing(fs -> fs.scheduleId().toString());
            return this.contains(o.range()) ? 0 : freeSlotComparator.compare(this, o);
        }
    }
}
