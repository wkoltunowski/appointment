package com.example.appointment;

import static com.example.appointment.domain.Visit.*;
import static java.util.Arrays.*;
import static org.testng.AssertJUnit.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.appointment.domain.ScheduleId;
import com.example.appointment.domain.VisitAlreadyTakenException;
import com.example.appointment.domain.Visits;

public class RegisterVisitTest {

  private FindFreeSlotsService findFreeSlots;

  @Test
  public void shouldFindFirst10Visits() throws Exception {
    Duration fifteenMinutes = Duration.ofMinutes(15);
    ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(16, 0));

    assertEquals(
        findFreeSlots.findFirstFree(todayAt(8, 0), fifteenMinutes),
        visits(scheduleId, fifteenMinutes,
            asList(
                todayAt(8, 0), todayAt(8, 15), todayAt(8, 30), todayAt(8, 45),
                todayAt(9, 0), todayAt(9, 15), todayAt(9, 30), todayAt(9, 45),
                todayAt(10, 0), todayAt(10, 15))));
  }

  @Test
  public void shouldFindVisitForSecondSlot() throws Exception {
    ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(8, 30));

    assertEquals(findFreeSlots.findFirstFree(todayAt(8, 15), Duration.ofMinutes(15)),
        Visits.of(visitFor(todayAt(8, 15), Duration.ofMinutes(15), scheduleId)));
  }

  @Test
  public void shouldFindVisitWhenInMiddleRequested() throws Exception {
    ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(8, 30));

    assertEquals(findFreeSlots.findFirstFree(todayAt(8, 10), Duration.ofMinutes(15)),
        Visits.of(visitFor(todayAt(8, 15), Duration.ofMinutes(15), scheduleId)));
  }

  @Test
  public void shouldFindNextDay() throws Exception {
    ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(8, 20));

    assertEquals(findFreeSlots.findFirstFree(todayAt(8, 20), Duration.ofMinutes(15)),
        Visits.of(visitFor(tommorrowAt(8, 0), Duration.ofMinutes(15), scheduleId)));
  }

  @Test
  public void shouldFindVisitWhenFirstReserved() throws Exception {
    ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(8, 30));
    findFreeSlots.reserveVisit(visitFor(todayAt(8, 0), Duration.ofMinutes(15), scheduleId));

    assertEquals(findFreeSlots.findFirstFree(todayAt(8, 0), Duration.ofMinutes(15)),
        Visits.of(visitFor(todayAt(8, 15), Duration.ofMinutes(15), scheduleId)));
  }

  @Test
  public void shouldFindEmptyForFullSchedule() throws Exception {
    ScheduleId scheduleId = findFreeSlots.givenSchedule(
        LocalTime.of(8, 0), LocalTime.of(8, 30),
        Validity.fromTo(LocalDate.now(), LocalDate.now()));
    findFreeSlots.reserveVisit(visitFor(todayAt(8, 0), Duration.ofMinutes(15), scheduleId));
    findFreeSlots.reserveVisit(visitFor(todayAt(8, 15), Duration.ofMinutes(15), scheduleId));

    assertEquals(findFreeSlots.findFirstFree(todayAt(8, 0), Duration.ofMinutes(15)), Visits.empty());
  }

  @Test
  public void shouldFindEmptyForNoSchedules() throws Exception {
    assertEquals(findFreeSlots.findFirstFree(todayAt(8, 0), Duration.ofMinutes(15)), Visits.empty());
  }

  @Test(expectedExceptions = VisitAlreadyTakenException.class)
  public void shouldNotReserveSameVisitTwice() throws Exception {
    ScheduleId scheduleId = findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(8, 30));
    findFreeSlots.reserveVisit(visitFor(todayAt(8, 0), Duration.ofMinutes(15), scheduleId));
    findFreeSlots.reserveVisit(visitFor(todayAt(8, 0), Duration.ofMinutes(15), scheduleId));

  }

  @Test
  public void shouldFindVisitForManySchedules() throws Exception {
    for (int i = 0; i < 100; i++) {
      findFreeSlots.givenSchedule(LocalTime.of(8, 0), LocalTime.of(16, 0));
    }
    System.out.println("free slots count : " + findFreeSlots.freeSlotsCount());

    Visits visits = findFreeSlots.findFirstFree(todayAt(8, 0), Duration.ofMinutes(15));
    int count = 0;
    int MAX_COUNT = 250000;
    while (!visits.getVisits().isEmpty() && count++ < MAX_COUNT) {
      visits = findFreeSlots.findFirstFree(todayAt(8, 0), Duration.ofMinutes(15));
      findFreeSlots.reserveFirst(visits);
    }
    System.out.println("free slots count : " + findFreeSlots.freeSlotsCount());
    System.out.println("reservations count : " + count);
  }

  @Test
  public void shouldFindOrderedVisitsForTwoSchedules() throws Exception {
    ScheduleId schedule1 = findFreeSlots.givenSchedule(LocalTime.of(15, 40), LocalTime.of(16, 0));
    ScheduleId schedule2 = findFreeSlots.givenSchedule(LocalTime.of(15, 45), LocalTime.of(16, 0));

    Visits visits = findFreeSlots.findFirstFree(todayAt(15, 40), Duration.ofMinutes(10));

    assertEquals(visits, Visits.of(
        visitFor(todayAt(15, 40), Duration.ofMinutes(10), schedule1),
        visitFor(todayAt(15, 45), Duration.ofMinutes(10), schedule2),
        visitFor(todayAt(15, 50), Duration.ofMinutes(10), schedule1)));
  }

  private Visits visits(ScheduleId scheduleId, Duration duration, List<LocalDateTime> visitDates) {
    Visits visits = Visits.empty();
    for (LocalDateTime visitDate : visitDates) {
      visits = visits.withVisitAdded(visitFor(visitDate, duration, scheduleId));

    }
    return visits;
  }

  @BeforeMethod
  public void setUp() throws Exception {
    findFreeSlots = new FindFreeSlotsService();
  }

  private LocalDateTime tommorrowAt(int hour, int minute) {
    return todayAt(hour, minute).plusDays(1);
  }

  private LocalDateTime todayAt(int hour, int minute) {
    return LocalDate.now().atTime(LocalTime.of(hour, minute));
  }

}
