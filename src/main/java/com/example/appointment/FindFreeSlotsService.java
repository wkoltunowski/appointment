package com.example.appointment;

import static com.example.appointment.domain.Visit.*;
import static java.time.LocalDate.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeSet;

import com.example.appointment.domain.FreeSlot;
import com.example.appointment.domain.Schedule;
import com.example.appointment.domain.ScheduleId;
import com.example.appointment.domain.Visit;
import com.example.appointment.domain.VisitAlreadyTakenException;
import com.example.appointment.domain.Visits;

public class FindFreeSlotsService {

  private final int maxFreeVisitsCount;
  private Map<LocalDate, Collection<FreeSlot>> index = new HashMap<>();

  public FindFreeSlotsService() {
    maxFreeVisitsCount = 10;
  }

  public void reserveVisit(Visit visit) {
    LocalDateTime visitStart = visit.getDateTime();
    Duration duration = visit.getDuration();
    Optional<FreeSlot> freeSlotOptional = Optional
        .ofNullable(index.get(visitStart.toLocalDate()))
        .orElse(Collections.emptyList())
        .stream()
        .filter(fs -> fs.contains(visitStart, duration))
        .findFirst();

    FreeSlot oldFreeSlot = freeSlotOptional.orElseThrow(VisitAlreadyTakenException::new);

    remove(oldFreeSlot);

    ScheduleId scheduleId = oldFreeSlot.getScheduleId();
    LocalDateTime freeSlotStart = oldFreeSlot.getStart();
    if (visitStart.isAfter(freeSlotStart)) {
      add(FreeSlot.of(scheduleId, freeSlotStart, visitStart));
    }

    LocalDateTime visitEnd = visitStart.plus(duration);
    LocalDateTime freeSlotEnd = oldFreeSlot.getEnd();
    if (visitEnd.isBefore(freeSlotEnd)) {
      add(FreeSlot.of(scheduleId, visitEnd, freeSlotEnd));
    }
  }

  public void reserveFirst(Visits visits) {
    if (!visits.getVisits().isEmpty()) {
      reserveVisit(visits.getVisits().first());
    }
  }

  public long freeSlotsCount() {
    return index.entrySet().stream().map(Entry::getValue).flatMap(Collection::stream).count();
  }

  private void remove(FreeSlot freeSlot) {
    index.get(freeSlot.getStart().toLocalDate()).remove(freeSlot);
  }

  private void add(FreeSlot of) {
    Collection<FreeSlot> freeSlots = index.get(of.getStart().toLocalDate());
    Collection<FreeSlot> slots = Optional.ofNullable(freeSlots).orElseGet(() -> {
      TreeSet<FreeSlot> treeSet = new TreeSet<>();
      index.put(of.getStart().toLocalDate(), treeSet);
      return treeSet;
    });
    slots.add(of);
  }

  private void generateFreeSlots(Schedule schedule) {
    LocalDate date = now();
    LocalDate reservationMaxDay = date.plus(Period.ofDays(schedule.maxReservationInAdvance()));
    while (date.isBefore(reservationMaxDay) && schedule.validFor(date)) {
      add(FreeSlot.of(schedule.getScheduleId(), date.atTime(schedule.getStart()), date.atTime(schedule.getEnd())));
      date = date.plusDays(1);
    }
  }

  public Visits findFirstFree(LocalDateTime visitDate, Duration duration) {
    List<Visit> visits = new ArrayList<>();
    Optional<LocalDate> foundDay = Optional.empty();
    LocalDate day = visitDate.toLocalDate();

    int daysCount = 0;
    while (visits.isEmpty() && daysCount++ < 90) {
      Collection<FreeSlot> freeSlots = Optional.ofNullable(index.get(day)).orElse(Collections.emptyList());
      for (FreeSlot fs : freeSlots) {
        if (fs.getEnd().isAfter(visitDate)) {
          LocalDateTime scheduleEnd = fs.getEnd();
          LocalDateTime visitStart = fs.getStart();
          while (visitStart.isBefore(scheduleEnd)) {
            LocalDateTime visitEnd = visitStart.plus(duration);
            if (!visitStart.isBefore(visitDate) && !visitEnd.isAfter(scheduleEnd)) {
              if (visits.size() >= maxFreeVisitsCount || foundDay.isPresent() && visitStart.toLocalDate().isAfter(foundDay.get())) {
                return Visits.of(visits);
              }
              foundDay = Optional.of(visitStart.toLocalDate());
              visits.add(visitFor(visitStart, duration, fs.getScheduleId()));
            }
            visitStart = visitEnd;
          }
        }
      }
      day = day.plusDays(1);
    }

    return Visits.of(visits);
  }

  public ScheduleId givenSchedule(LocalTime from, LocalTime to) {
    ScheduleId scheduleId = ScheduleId.newId();
    generateFreeSlots(new Schedule(from, to, scheduleId));
    return scheduleId;
  }

  public ScheduleId givenSchedule(LocalTime startTime, LocalTime endTime, Validity validity) {
    ScheduleId scheduleId = ScheduleId.newId();
    generateFreeSlots(new Schedule(startTime, endTime, validity, scheduleId));
    return scheduleId;
  }
}
