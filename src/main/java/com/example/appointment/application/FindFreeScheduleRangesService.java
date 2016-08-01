package com.example.appointment.application;

import com.example.appointment.domain.freescheduleranges.*;
import com.example.appointment.domain.schedule.ScheduleDurations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.BaseStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FindFreeScheduleRangesService {

    private final int firstFreeCount;
    private final ScheduleDurations scheduleDurations;
    private final FreeScheduleSlotRepository storage;

    public FindFreeScheduleRangesService(int firstFreeCount, ScheduleDurations scheduleDurations, FreeScheduleSlotRepository storage) {
        this.firstFreeCount = firstFreeCount;
        this.scheduleDurations = scheduleDurations;
        this.storage = storage;
    }

    public List<ScheduleRange> findFirstFree(LocalDateTime startingFrom) {
        return findFirstFree(startingFrom, SearchTags.empty());
    }

    public List<ScheduleRange> findFirstFree(LocalDateTime startingFrom, SearchTags searchTags) {
        Stream<FreeScheduleSlot> stream = StreamSupport.stream(this.storage.findAfter(startingFrom).spliterator(), false);
        Iterator<Iterator<ScheduleRange>> iterators = stream
                .filter(s -> s.matches(searchTags))
                .map(slot -> freeSlotRanges(startingFrom, slot))
                .map(BaseStream::iterator)
                .iterator();
        List<ScheduleRange> ranges = new ArrayList<>();
        while (iterators.hasNext()) {
            Iterator<ScheduleRange> slotIterator = iterators.hasNext() ? iterators.next() : Collections.emptyListIterator();
            Iterator<ScheduleRange> nextIterator = iterators.hasNext() ? iterators.next() : Collections.emptyListIterator();
            Optional<ScheduleRange> nextIteratorRange = Optional.empty();
            if (nextIterator.hasNext()) {
                nextIteratorRange = Optional.of(nextIterator.next());
            }
            while (slotIterator.hasNext()) {
                ScheduleRange thisSlotRange = slotIterator.next();
                while (nextIteratorRange.isPresent() && nextIteratorRange.get().start().isBefore(thisSlotRange.start())) {
                    ranges.add(nextIteratorRange.get());
                    if (nextIterator.hasNext()) {
                        nextIteratorRange = Optional.of(nextIterator.next());
                    } else {
                        if (iterators.hasNext()) {
                            nextIterator = iterators.next();
                            if (nextIterator.hasNext()) {
                                nextIteratorRange = Optional.of(nextIterator.next());
                            }
                        } else {
                            nextIteratorRange = Optional.empty();
                        }
                    }
                    if (ranges.size() >= firstFreeCount) {
                        return ranges;
                    }
                }
                ranges.add(thisSlotRange);
                if (ranges.size() >= firstFreeCount) {
                    return ranges;
                }
            }


        }
        return Collections.emptyList();
    }


    private Stream<ScheduleRange> freeSlotRanges(LocalDateTime startingFrom, FreeScheduleSlot freeSlot) {
        Duration duration = this.scheduleDurations.durationFor(freeSlot.scheduleId());
        return StreamSupport.stream(freeSlot.scheduleRanges(startingFrom, duration).spliterator(), false);
    }


    public List<ScheduleRange> findFirstFree(SearchCriteria crit) {
        return findFirstFree(crit.getStartingFrom(), crit.searchTags());
    }
}
