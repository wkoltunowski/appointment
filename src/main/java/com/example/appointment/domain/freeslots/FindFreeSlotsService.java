package com.example.appointment.domain.freeslots;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.example.appointment.domain.*;
import com.example.appointment.infrastructure.DayCollectionFreeSlotStorage;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

public class FindFreeSlotsService {

  private final int maxFreeVisitsCount;
  private final FreeSlotStorage storage = new DayCollectionFreeSlotStorage();
  private ScheduleDurations scheduleDurations = new ScheduleDurations();
  // private final FreeSlotStorage storage = new ArrayListFreeSlotStorage();
  // private final FreeSlotStorage storage = new TreeSetFreeSlotStorage();

  public FindFreeSlotsService(int maxFreeVisitsCount) {
    this.maxFreeVisitsCount = maxFreeVisitsCount;
  }

  public void reserveVisit(Visit visit) {
    FreeSlot oldFreeSlot = findFirstSlotForVisit(visit);

    this.storage.remove(oldFreeSlot);

    Range<LocalDateTime> beforeVisit = Ranges.closedOpen(oldFreeSlot.getStart(), visit.range().lowerEndpoint());
    addNotEmpty(oldFreeSlot.withRange(beforeVisit));

    Range<LocalDateTime> afterVisit = Ranges.closedOpen(visit.range().upperEndpoint(), oldFreeSlot.getEnd());
    addNotEmpty(oldFreeSlot.withRange(afterVisit));
  }

  private FreeSlot findFirstSlotForVisit(Visit visit) {
    LocalDate visitDay = visit.getDateTime().toLocalDate();
    Iterable<FreeSlot> freeSlotsAfter = findFreeSlotsAfter(visitDay);

    Optional<FreeSlot> freeSlotOptional = StreamSupport
        .stream(freeSlotsAfter.spliterator(), false)
        .filter(freeSlot -> freeSlot.getScheduleId().equals(visit.scheduleId()))
        .filter(fs -> fs.contains(visit.range()))
        .findFirst();
    return freeSlotOptional.orElseThrow(VisitAlreadyTakenException::new);
  }

  private void addNotEmpty(FreeSlot newSLot) {
    if (!newSLot.isEmpty()) {
      this.storage.add(newSLot);
    }
  }

  public void reserveFirst(Visits visits) {
    if (!visits.getVisits().isEmpty()) {
      reserveVisit(visits.getVisits().first());
    }
  }

  public long freeSlotsCount() {
    return storage.size();
  }

  public Visits findFirstFree(LocalDateTime visitDate) {

    List<Visit> visits = StreamSupport
        .stream(findFreeSlotsAfter(visitDate.toLocalDate()).spliterator(), false)
        .flatMap(findVisitsStream(visitDate))
        .limit(maxFreeVisitsCount)
        .collect(toList());
    return Visits.of(visits);
  }

  private Iterable<FreeSlot> findFreeSlotsAfter(LocalDate localDate) {
    return this.storage.findAfter(localDate);
  }

  private Function<FreeSlot, Stream<Visit>> findVisitsStream(LocalDateTime requestedDate) {

    return fs -> StreamSupport.stream(fs.visits(fs, defaultDuration(fs)).spliterator(), false)
        .filter(isAfterOrEqual(requestedDate));

  }

  private Duration defaultDuration(FreeSlot fs) {
    return this.scheduleDurations.durationFor(fs.getScheduleId());
  }

  private Predicate<Visit> isAfterOrEqual(LocalDateTime requestedDate) {
    return visitStart -> !visitStart.getDateTime().isBefore(requestedDate);
  }

  public ScheduleId givenSchedule(LocalTime from, LocalTime to, Duration duration) {
    ScheduleId scheduleId = ScheduleId.newId();
    scheduleDurations.defineDuration(scheduleId,duration);
    generateFreeSlots(new Schedule(from, to, scheduleId));
    return scheduleId;
  }

  public ScheduleId givenSchedule(LocalTime startTime, LocalTime endTime, Validity validity, Duration duration) {
    ScheduleId scheduleId = ginveSchedule(startTime, endTime, validity);
    scheduleDurations.defineDuration(scheduleId,duration);
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
