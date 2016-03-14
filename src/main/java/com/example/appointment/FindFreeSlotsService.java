package com.example.appointment;

import static com.example.appointment.domain.Visit.visitFor;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

import java.time.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.example.appointment.domain.*;

public class FindFreeSlotsService {

  private final int maxFreeVisitsCount;
  private FreeSlotStorage storage = new ArrayListFreeSlotStorage();
//  private FreeSlotStorage storage = new DayCollectionFreeSlotStorage();
//  private FreeSlotStorage storage = new TreeSetFreeSlotStorage();

  public FindFreeSlotsService(int maxFreeVisitsCount) {
    this.maxFreeVisitsCount = maxFreeVisitsCount;
  }

  public void reserveVisit(Visit visit) {
    LocalDateTime visitStart = visit.getDateTime();
    Duration duration = visit.getDuration();
    Optional<FreeSlot> freeSlotOptional = StreamSupport.stream(findSlotsFor(visitStart).spliterator(), false)
        .filter(fs -> fs.contains(visitStart, duration))
        .findFirst();

    FreeSlot oldFreeSlot = freeSlotOptional.orElseThrow(VisitAlreadyTakenException::new);

    this.storage.remove(oldFreeSlot);

    LocalDateTime freeSlotStart = oldFreeSlot.getStart();
    if (visitStart.isAfter(freeSlotStart)) {
      add(oldFreeSlot.withNewEnd(visitStart));
    }

    LocalDateTime visitEnd = visitStart.plus(duration);
    LocalDateTime freeSlotEnd = oldFreeSlot.getEnd();
    if (visitEnd.isBefore(freeSlotEnd)) {
      add(oldFreeSlot.withNewStart(visitEnd));
    }
  }

  private Iterable<FreeSlot> findSlotsFor(LocalDateTime visitDate) {
    return findFreeSlotsAfter(visitDate.toLocalDate());
  }

  public void reserveFirst(Visits visits) {
    if (!visits.getVisits().isEmpty()) {
      reserveVisit(visits.getVisits().first());
    }
  }

  public long freeSlotsCount() {
    return storage.size();
  }

  private void add(FreeSlot of) {
    this.storage.add(of);

  }

  private void generateFreeSlots(Schedule schedule, Duration duration) {
    LocalDate date = now();
    LocalDate reservationMaxDay = date.plus(Period.ofDays(schedule.maxReservationInAdvance()));
    while (date.isBefore(reservationMaxDay) && schedule.validFor(date)) {
      add(FreeSlot.of(schedule.getScheduleId(), schedule.toFromTo(date), duration));
      date = date.plusDays(1);
    }
  }

  public Visits findFirstFree(LocalDateTime visitDate) {
    return findStream(visitDate);
  }

  private Visits findStream(LocalDateTime visitDate) {
    List<Visit> visits = StreamSupport.stream(findFreeSlotsAfter(visitDate.toLocalDate()).spliterator(), false)
        .filter(after(visitDate))
        .flatMap(findVisitsStream(visitDate))
        .limit(maxFreeVisitsCount)
        .collect(toList());
    return Visits.of(visits);
  }

  private Iterable<FreeSlot> findFreeSlotsAfter(LocalDate localDate) {
    return this.storage.findAfter(localDate);

  }

  private Predicate<? super FreeSlot> after(LocalDateTime visitDate) {
    return fs -> !fs.getEnd().isBefore(visitDate.plus(fs.duration()));
  }

  private Function<FreeSlot, Stream<Visit>> findVisitsStream(LocalDateTime requestedDate) {
    return fs -> {
      Iterable<LocalDateTime> visitStarts = () -> new A(fs.getStart(), fs.getEnd(), fs.duration());
      return StreamSupport
          .stream(visitStarts.spliterator(), false)
          .filter(isAfterOrEqual(requestedDate))
          .map(visitStart -> visitFor(visitStart, visitStart.plus(fs.duration()), fs.getScheduleId()));
    };

  }

  private Predicate<LocalDateTime> isAfterOrEqual(LocalDateTime requestedDate) {
    return visitStart -> {
      boolean notBefore = !visitStart.isBefore(requestedDate);
      return notBefore;
    };
  }

  private Function<FreeSlot, Stream<Visit>> findVisits(LocalDateTime visitDate) {
    return fs -> {
      List<Visit> visits = new ArrayList<>();
      if (fs.getEnd().isAfter(visitDate)) {
        LocalDateTime scheduleEnd = fs.getEnd();
        LocalDateTime visitStart = fs.getStart();
        while (visitStart.isBefore(scheduleEnd)) {
          LocalDateTime visitEnd = visitStart.plus(fs.duration());
          if (!visitStart.isBefore(visitDate) && !visitEnd.isAfter(scheduleEnd)) {
            visits.add(visitFor(visitStart, visitEnd, fs.getScheduleId()));
          }
          visitStart = visitEnd;
        }

      }
      return visits.stream();
    };

  }

  private Visits findForWhileIf(LocalDateTime visitDate) {
    List<Visit> visits = new ArrayList<>();
    LocalDate day = visitDate.toLocalDate();

    int daysCount = 0;
    while (visits.isEmpty() && daysCount++ < 90) {
      for (FreeSlot fs : storage.allSlots()) {

        if (fs.getEnd().isAfter(visitDate)) {
          LocalDateTime scheduleEnd = fs.getEnd();
          LocalDateTime visitStart = fs.getStart();
          while (visitStart.isBefore(scheduleEnd)) {
            LocalDateTime visitEnd = visitStart.plus(fs.duration());
            if (!visitStart.isBefore(visitDate) && !visitEnd.isAfter(scheduleEnd)) {
              if (visits.size() >= maxFreeVisitsCount) {
                return Visits.of(visits);
              }
              visits.add(visitFor(visitStart, visitEnd, fs.getScheduleId()));
            }
            visitStart = visitEnd;
          }
        }
      }
      day = day.plusDays(1);
    }

    return Visits.of(visits);
  }

  public ScheduleId givenSchedule(LocalTime from, LocalTime to, Duration duration) {
    ScheduleId scheduleId = ScheduleId.newId();
    generateFreeSlots(new Schedule(from, to, scheduleId), duration);
    return scheduleId;
  }

  public ScheduleId givenSchedule(LocalTime startTime, LocalTime endTime, Validity validity, Duration duration) {
    ScheduleId scheduleId = ScheduleId.newId();
    generateFreeSlots(new Schedule(startTime, endTime, validity, scheduleId), duration);
    return scheduleId;
  }

  private class A implements Iterator<LocalDateTime> {

    private LocalDateTime date;
    private final LocalDateTime endDate;
    private final Duration duration;

    public A(LocalDateTime startDate, LocalDateTime endDate, Duration duration) {
      this.date = startDate;
      this.endDate = endDate;
      this.duration = duration;
    }

    @Override
    public boolean hasNext() {
      return !date.plus(duration).isAfter(endDate);
    }

    @Override
    public LocalDateTime next() {
      LocalDateTime oldDate = this.date;
      this.date = this.date.plus(duration);

      return oldDate;
    }
  }
}
