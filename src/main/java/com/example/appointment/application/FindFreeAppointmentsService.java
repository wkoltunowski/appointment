package com.example.appointment.application;

import com.example.appointment.domain.freeslot.*;
import com.example.appointment.domain.schedule.ScheduleDurations;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class FindFreeAppointmentsService {

    private final int firstFreeCount;
    private final ScheduleDurations scheduleDurations;
    private final FreeSlotRepository storage;

    public FindFreeAppointmentsService(int firstFreeCount, ScheduleDurations scheduleDurations, FreeSlotRepository storage) {
        this.firstFreeCount = firstFreeCount;
        this.scheduleDurations = scheduleDurations;
        this.storage = storage;
    }

    public FreeAppointments findFirstFree(LocalDateTime startingFrom) {
        return findFirstFree(startingFrom, SearchTags.empty());
    }

    public FreeAppointments findFirstFree(LocalDateTime startingFrom, SearchTags searchTags) {
        LocalDate startingDay = startingFrom.toLocalDate();
        Iterator<FreeSlot> iterator = StreamSupport
                .stream(this.storage.findAfter(startingDay).spliterator(), false)
                .filter(fs -> fs.matches(searchTags))
                .iterator();
        Multimap<LocalDate, FreeSlot> map = LinkedListMultimap.create();
        List<ScheduleRange> scheduleRanges = new ArrayList<>(firstFreeCount);
        while (iterator.hasNext() && scheduleRanges.size() < firstFreeCount) {
            FreeSlot freeSlot = iterator.next();
            LocalDate slotDay = freeSlot.start().toLocalDate();
            if (!map.containsKey(slotDay)) {
                addToFreeAppointments(scheduleRanges, startingFrom, map.values());
                map.clear();
            }
            map.put(slotDay, freeSlot);
        }

        addToFreeAppointments(scheduleRanges, startingFrom, map.values());

        return FreeAppointments.of(scheduleRanges);
    }

    private void addToFreeAppointments(List<ScheduleRange> scheduleRanges, LocalDateTime startingFrom, Collection<FreeSlot> values) {
        scheduleRanges.addAll(values
                .stream()
                .flatMap(appointmentsStream(startingFrom))
                .sorted(Comparator.comparing(ScheduleRange::start).thenComparing(ScheduleRange::duration))
                .limit(firstFreeCount - scheduleRanges.size())
                .collect(toList()));
    }


    private Function<FreeSlot, Stream<ScheduleRange>> appointmentsStream(LocalDateTime startingDate) {
        return fs -> {
            Duration duration = this.scheduleDurations.durationFor(fs.scheduleId());
            Spliterator<ScheduleRange> spliterator = fs.appointmentsFor(startingDate, duration).spliterator();
            return StreamSupport.stream(spliterator, false);
        };
    }


}
