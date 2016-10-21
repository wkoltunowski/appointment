package com.falco.appointment.scheduling.application;

import com.falco.appointment.scheduling.domain.freescheduleranges.*;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleDurations;
import com.falco.appointment.scheduling.domain.SearchTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class FindFreeRangesService {

    private final int firstFreeCount;
    private final ScheduleDurations scheduleDurations;
    private final FreeScheduleSlotRepository storage;

    @Autowired
    public FindFreeRangesService(
            @Value("${FindFreeScheduleRangesService.firstFreeCount}") int firstFreeCount,
            ScheduleDurations scheduleDurations,
            FreeScheduleSlotRepository storage) {
        this.firstFreeCount = firstFreeCount;
        this.scheduleDurations = scheduleDurations;
        this.storage = storage;
    }

    public List<ScheduleRange> findFirstFree(LocalDateTime startingFrom) {
        return findFirstFree(startingFrom, SearchTags.empty());
    }

    public List<ScheduleRange> findFirstFree(LocalDateTime startingFrom, SearchTags searchTags) {
        Stream<FreeScheduleSlot> stream = StreamSupport.stream(this.storage.findAfter(startingFrom).spliterator(), false);
        return stream
                .filter(s -> s.matches(searchTags))
                .map(slot -> freeSlotRanges(startingFrom, slot))
                .flatMap(scheduleRangeStream -> scheduleRangeStream)
                .limit(2 * firstFreeCount)
                .sorted(Comparator.comparing(ScheduleRange::start).thenComparing(ScheduleRange::end))
                .limit(firstFreeCount)
                .collect(Collectors.toList());

    }


    private Stream<ScheduleRange> freeSlotRanges(LocalDateTime startingFrom, FreeScheduleSlot freeSlot) {
        Duration duration = this.scheduleDurations.durationFor(freeSlot.scheduleId());
        return StreamSupport.stream(freeSlot.scheduleRanges(startingFrom, duration).spliterator(), false);
    }


    public List<ScheduleRange> findFirstFree(SearchCriteria crit) {
        return findFirstFree(crit.getStartingFrom(), crit.searchTags());
    }


}
