package com.example.appointment;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import com.example.appointment.domain.FreeSlot;

public class DayCollectionFreeSlotStorage implements FreeSlotStorage {

  private Map<LocalDate, Collection<FreeSlot>> index = new HashMap<>();

  @Override
  public void remove(FreeSlot freeSlot) {
      index.get(freeSlot.getStart().toLocalDate()).remove(freeSlot);
  }

  @Override
  public long size() {
    return index.entrySet().stream().map(Map.Entry::getValue).flatMap(Collection::stream).count();
  }

  @Override
  public void add(FreeSlot of) {
    Collection<FreeSlot> freeSlots = index.get(of.getStart().toLocalDate());
    Collection<FreeSlot> slots = Optional.ofNullable(freeSlots).orElseGet(() -> {
      Collection<FreeSlot> treeSet = new ArrayList<FreeSlot>();
//      TreeSet<FreeSlot> treeSet = new TreeSet<>();
      index.put(of.getStart().toLocalDate(), treeSet);
      return treeSet;
    });
    slots.add(of);
  }

  @Override
  public Iterable<FreeSlot> findAfter(LocalDate localDate) {
    Stream<Collection<FreeSlot>> collectionStream = Stream
        .iterate(localDate, localDate1 -> localDate1.plusDays(1))
        .map(d -> {
          Collection<FreeSlot> daySlots = index.get(d);
          if (daySlots != null) {
            return daySlots;
          }
          return Collections.emptyList();
        });
    return () -> collectionStream.flatMap(Collection::stream).iterator();

    // Collection<FreeSlot> freeSlots = Optional.ofNullable(index.get(day)).orElse(Collections.emptyList());
    // for (FreeSlot fs : freeSlots) {
    // if (fs.getEnd().isAfter(visitDate)) {
    // LocalDateTime scheduleEnd = fs.getEnd();
    // LocalDateTime visitStart = fs.getStart();
    // while (visitStart.isBefore(scheduleEnd)) {
    // LocalDateTime visitEnd = visitStart.plus(fs.duration());
    // if (!visitStart.isBefore(visitDate) && !visitEnd.isAfter(scheduleEnd)) {
    // if (visits.size() >= maxFreeVisitsCount || foundDay.isPresent() && visitStart.toLocalDate().isAfter(foundDay.get())) {
    // return Visits.of(visits);
    // }
    // foundDay = Optional.of(visitStart.toLocalDate());
    // visits.add(visitFor(visitStart, visitEnd, fs.getScheduleId()));
    // }
    // visitStart = visitEnd;
    // }
    // }
    // }
    // day = day.plusDays(1);

  }

  @Override
  public Iterable<FreeSlot> allSlots() {
    return () -> index.entrySet().stream().map(Map.Entry::getValue).flatMap(Collection::stream).iterator();
  }

}
