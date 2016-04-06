package com.example.appointment.application;

import com.example.appointment.domain.freeslot.FreeAppointment;
import com.example.appointment.domain.freeslot.FreeAppointments;
import com.example.appointment.domain.freeslot.FreeSlot;
import com.example.appointment.domain.freeslot.FreeSlotRepository;
import com.example.appointment.domain.schedule.ScheduleDurations;
import com.example.appointment.domain.freeslot.SearchTags;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Spliterator;
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
        List<FreeAppointment> freeAppointments = StreamSupport
                .stream(this.storage.findAfter(startingDay).spliterator(), false)
                .filter(fs -> fs.matches(searchTags))
                .flatMap(appointmentsStream(startingFrom))
                .limit(firstFreeCount)
                .collect(toList());
        return FreeAppointments.of(freeAppointments);
    }

    private Function<FreeSlot, Stream<FreeAppointment>> appointmentsStream(LocalDateTime startingDate) {
        return fs -> {
            Duration duration = this.scheduleDurations.durationFor(fs.scheduleId());
            Spliterator<FreeAppointment> spliterator = fs.appointmentsFor(startingDate, duration).spliterator();
            return StreamSupport.stream(spliterator, false);
        };
    }


}
