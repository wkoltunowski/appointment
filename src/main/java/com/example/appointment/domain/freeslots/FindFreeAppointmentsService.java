package com.example.appointment.domain.freeslots;

import com.example.appointment.domain.*;
import com.example.appointment.infrastructure.DayCollectionFreeSlotsStorage;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
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
        FreeSlot oldFreeSlot = findFirstFreeSlot(appointment);

        this.storage.remove(oldFreeSlot);
        addNotEmpty(split(appointment, oldFreeSlot));
    }

    private List<FreeSlot> split(Appointment appointment, FreeSlot oldFreeSlot) {
        Range<LocalDateTime> beforeAppointment = Ranges.closedOpen(oldFreeSlot.start(), appointment.range().lowerEndpoint());
        FreeSlot beforeSlot = oldFreeSlot.withRange(beforeAppointment);

        Range<LocalDateTime> afterAppointment = Ranges.closedOpen(appointment.range().upperEndpoint(), oldFreeSlot.end());
        FreeSlot slotAfter = oldFreeSlot.withRange(afterAppointment);
        return asList(beforeSlot, slotAfter);
    }

    private FreeSlot findFirstFreeSlot(Appointment appointment) {
        LocalDate day = appointment.start().toLocalDate();
        Iterable<FreeSlot> freeSlotsAfter = findFreeSlotsAfter(day);

        Optional<FreeSlot> freeSlotOptional = StreamSupport
                .stream(freeSlotsAfter.spliterator(), false)
                .filter(freeSlot -> freeSlot.getScheduleId().equals(appointment.scheduleId()))
                .filter(fs -> fs.contains(appointment.range()))
                .findFirst();
        return freeSlotOptional.orElseThrow(AppointmentTakenException::new);
    }

    private void addNotEmpty(Collection<FreeSlot> newSLot) {
        this.storage.addAll(newSLot.stream().filter(fs -> !fs.isEmpty()).collect(Collectors.toList()));
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

    private Iterable<FreeSlot> findFreeSlotsAfter(LocalDate localDate) {
        return this.storage.findAfter(localDate);
    }

    private Function<FreeSlot, Stream<Appointment>> appointmentsStream(LocalDateTime requestedDate) {
        return fs -> StreamSupport
                .stream(fs.appointmentsFor(fs, durationFor(fs)).spliterator(), false)
                .filter(isAfterOrEqual(requestedDate));
    }

    private Duration durationFor(FreeSlot fs) {
        return this.scheduleDurations.durationFor(fs.getScheduleId());
    }

    private Predicate<Appointment> isAfterOrEqual(LocalDateTime requestedDate) {
        return appointment -> !appointment.start().isBefore(requestedDate);
    }

    public ScheduleId givenSchedule(LocalTime from, LocalTime to, Duration duration) {
        ScheduleId scheduleId = ScheduleId.newId();
        scheduleDurations.defineDuration(scheduleId, duration);
        generateFreeSlots(new Schedule(from, to, scheduleId));
        return scheduleId;
    }

    public ScheduleId givenSchedule(LocalTime startTime, LocalTime endTime, Validity validity, Duration duration) {
        ScheduleId scheduleId = ginveSchedule(startTime, endTime, validity);
        scheduleDurations.defineDuration(scheduleId, duration);
        return scheduleId;
    }

    public ScheduleId ginveSchedule(LocalTime startTime, LocalTime endTime, Validity validity) {
        ScheduleId scheduleId = ScheduleId.newId();
        generateFreeSlots(new Schedule(startTime, endTime, validity, scheduleId));
        return scheduleId;
    }

    private void generateFreeSlots(Schedule schedule) {
        this.storage.addAll(schedule.buildFreeSlots(now()));
    }

}
