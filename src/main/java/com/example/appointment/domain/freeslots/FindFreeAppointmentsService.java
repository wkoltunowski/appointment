package com.example.appointment.domain.freeslots;

import com.example.appointment.domain.*;
import com.example.appointment.infrastructure.DayCollectionFreeSlotsStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

public class FindFreeAppointmentsService {

    private final int firstFreeCount;
    private ScheduleDurations scheduleDurations = new ScheduleDurations();
    private final FreeSlotsStorage storage = new DayCollectionFreeSlotsStorage();
//     private final FreeSlotsStorage storage = new ArrayListFreeSlotsStorage();
//     private final FreeSlotsStorage storage = new TreeSetFreeSlotsStorage();

    public FindFreeAppointmentsService(int firstFreeCount) {
        this.firstFreeCount = firstFreeCount;
    }

    public void reserve(Appointment appointment) {
        FreeSlot freeSlot = findFirstFreeSlot(appointment);

        this.storage.remove(freeSlot);
        this.storage.addAll(freeSlot.splitFor(appointment.range()));
    }


    private FreeSlot findFirstFreeSlot(Appointment appointment) {
        LocalDate day = appointment.start().toLocalDate();
        Iterable<FreeSlot> freeSlotsAfter = findFreeSlotsAfter(day);

        Optional<FreeSlot> freeSlotOptional = StreamSupport
                .stream(freeSlotsAfter.spliterator(), false)
                .filter(fs -> fs.validForAppointment(appointment))
                .findFirst();
        return freeSlotOptional.orElseThrow(AppointmentTakenException::new);
    }

    public void reserveFirst(FreeAppointments freeAppointments) {
        if (!freeAppointments.getAppointments().isEmpty()) {
            reserve(freeAppointments.getAppointments().first());
        }
    }

    public long freeSlotsCount() {
        return storage.size();
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
                .stream(fs.appointmentsFor(startingDate, durationFor(fs)).spliterator(), false);
    }

    private Duration durationFor(FreeSlot fs) {
        return this.scheduleDurations.durationFor(fs.scheduleId());
    }

    public ScheduleId givenSchedule(LocalTime from, LocalTime to, Duration duration) {
        ScheduleId scheduleId = ScheduleId.newId();
        scheduleDurations.defineDuration(scheduleId, duration);
        generateFreeSlots(new Schedule(from, to, scheduleId));
        return scheduleId;
    }

    public ScheduleId givenSchedule(LocalTime startTime, LocalTime endTime, Validity validity, Duration duration) {
        ScheduleId scheduleId = givenSchedule(startTime, endTime, validity);
        scheduleDurations.defineDuration(scheduleId, duration);
        return scheduleId;
    }

    public ScheduleId givenSchedule(LocalTime startTime, LocalTime endTime, Validity validity) {
        ScheduleId scheduleId = ScheduleId.newId();
        generateFreeSlots(new Schedule(startTime, endTime, validity, scheduleId));
        return scheduleId;
    }

    private void generateFreeSlots(Schedule schedule) {
        this.storage.addAll(schedule.buildFreeSlots(now()));
    }

}
