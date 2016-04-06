package com.example.appointment.application;

import com.example.appointment.domain.freeslot.Appointment;
import com.example.appointment.domain.freeslot.Appointments;
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

    public Appointments findFirstFree(LocalDateTime startingFrom) {
        return findFirstFree(startingFrom, SearchTags.empty());
    }

    public Appointments findFirstFree(LocalDateTime startingFrom, SearchTags searchTags) {
        LocalDate startingDay = startingFrom.toLocalDate();
        List<Appointment> appointments = StreamSupport
                .stream(this.storage.findAfter(startingDay).spliterator(), false)
                .filter(fs -> fs.matches(searchTags))
                .flatMap(appointmentsStream(startingFrom))
                .limit(firstFreeCount)
                .collect(toList());
        return Appointments.of(appointments);
    }

    private Function<FreeSlot, Stream<Appointment>> appointmentsStream(LocalDateTime startingDate) {
        return fs -> {
            Duration duration = this.scheduleDurations.durationFor(fs.scheduleId());
            Spliterator<Appointment> spliterator = fs.appointmentsFor(startingDate, duration).spliterator();
            return StreamSupport.stream(spliterator, false);
        };
    }


}
