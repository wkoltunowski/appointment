package com.falco.appointment.scheduling.application;

import com.falco.appointment.scheduling.api.FindFreeRangesService;
import com.falco.appointment.scheduling.api.ScheduleRange;
import com.falco.appointment.scheduling.api.SearchTags;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlot;
import com.falco.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleDurations;
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
public class FindFreeRangesServiceImpl implements FindFreeRangesService {

    private final int firstFreeCount;
    private final ScheduleDurations scheduleDurations;
    private final FreeScheduleSlotRepository storage;

    @Autowired
    public FindFreeRangesServiceImpl(
            @Value("${FindFreeScheduleRangesService.firstFreeCount}") int firstFreeCount,
            ScheduleDurations scheduleDurations,
            FreeScheduleSlotRepository storage) {
        this.firstFreeCount = firstFreeCount;
        this.scheduleDurations = scheduleDurations;
        this.storage = storage;
    }

    @Override
    public List<ScheduleRange> findFirstFree(LocalDateTime startingFrom) {
        return findFirstFree(startingFrom, SearchTags.empty());
    }

    @Override
    public List<ScheduleRange> findFirstFree(LocalDateTime startingFrom, SearchTags searchTags) {
//        return iterator(startingFrom, searchTags);
        return stream(startingFrom, searchTags);

    }

    private List<ScheduleRange> stream(LocalDateTime startingFrom, SearchTags searchTags) {
        Stream<FreeScheduleSlot> stream = StreamSupport.stream(this.storage.findAfter(startingFrom).spliterator(), false);
        return stream
                .filter(s -> s.matches(searchTags))
                .flatMap(slot -> freeSlotRanges(startingFrom, slot))
                .limit(2 * firstFreeCount)
                .sorted(Comparator.comparing(ScheduleRange::start).thenComparing(ScheduleRange::end))
                .limit(firstFreeCount)
                .collect(Collectors.toList());
    }

    private List<ScheduleRange> iterator(LocalDateTime startingFrom, SearchTags searchTags) {
        Iterator<FreeScheduleSlot> slotIterator = this.storage.findAfter(startingFrom).iterator();
        TreeSet<ScheduleRange> scheduleRanges = new TreeSet<>(Comparator.comparing(ScheduleRange::start).thenComparing(ScheduleRange::end));
        Iterator<ScheduleRange> rangesIterator = Collections.emptyListIterator();
        while (slotIterator.hasNext() && scheduleRanges.size() < 2 * firstFreeCount) {
            if (!rangesIterator.hasNext()) {
                FreeScheduleSlot slot = slotIterator.next();
                if (slot.matches(searchTags)) {
                    rangesIterator = slot.scheduleRanges(startingFrom, scheduleDurations.durationFor(slot.scheduleId())).iterator();
                }
            }
            if (rangesIterator.hasNext()) {
                scheduleRanges.add(rangesIterator.next());
            }
        }
        return scheduleRanges.stream().limit(firstFreeCount).collect(Collectors.toList());
//        return ImmutableList.copyOf(scheduleRanges).subList(0,Math.min(scheduleRanges.size(),firstFreeCount));
    }

    private List<ScheduleRange> iterator2(LocalDateTime startingFrom) {
        Iterator<FreeScheduleSlot> iterator = this.storage.findAfter(startingFrom).iterator();
        while (iterator.hasNext()) {

        }
        if (iterator.hasNext()) {
            FreeScheduleSlot next = iterator.next();

            Iterator<ScheduleRange> iterator1 = next.scheduleRanges(startingFrom, scheduleDurations.durationFor(next.scheduleId())).iterator();
            if (iterator1.hasNext()) {
                return Collections.singletonList(iterator1.next());
            }
        }
        return Collections.emptyList();
    }


    private Stream<ScheduleRange> freeSlotRanges(LocalDateTime startingFrom, FreeScheduleSlot freeSlot) {
        Duration duration = this.scheduleDurations.durationFor(freeSlot.scheduleId());
        return StreamSupport.stream(freeSlot.scheduleRanges(startingFrom, duration).spliterator(), false);
    }
}
