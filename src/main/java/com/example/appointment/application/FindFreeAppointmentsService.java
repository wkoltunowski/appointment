package com.example.appointment.application;

import com.example.appointment.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class FindFreeAppointmentsService {

    private final int firstFreeCount;
    private final ScheduleDurations scheduleDurations;
    private final FreeSlotsStorage storage;

    public FindFreeAppointmentsService(int firstFreeCount, ScheduleDurations scheduleDurations, FreeSlotsStorage storage) {
        this.firstFreeCount = firstFreeCount;
        this.scheduleDurations = scheduleDurations;
        this.storage = storage;
    }

    public FreeAppointments findFirstFree(LocalDateTime startingFrom) {
        List<Appointment> appointments = StreamSupport
                .stream(findFreeSlotsAfter(startingFrom.toLocalDate()).spliterator(), false)
                .flatMap(appointmentsStream(startingFrom))
                .limit(firstFreeCount)
                .collect(toList());
        return FreeAppointments.of(appointments);
    }

    private Iterable<FreeSlot> findFreeSlotsAfter(LocalDate startingDay) {
        return this.storage.findAfter(startingDay);
    }

    private Function<FreeSlot, Stream<Appointment>> appointmentsStream(LocalDateTime startingDate) {
        return fs -> StreamSupport
                .stream(fs.appointmentsFor(startingDate, this.scheduleDurations.durationFor(fs.scheduleId())).spliterator(), false);
    }

    public FreeAppointments findFirstFree(LocalDateTime startingFrom, Collection<ScheduleId> scheduleIds) {
        List<Appointment> appointments = StreamSupport
                .stream(findFreeSlotsAfter(startingFrom.toLocalDate()).spliterator(), false)
                .filter(fs -> scheduleIds.isEmpty() || scheduleIds.contains(fs.scheduleId()))
                .flatMap(appointmentsStream(startingFrom))
                .limit(firstFreeCount)
                .collect(toList());
        return FreeAppointments.of(appointments);
    }
}
