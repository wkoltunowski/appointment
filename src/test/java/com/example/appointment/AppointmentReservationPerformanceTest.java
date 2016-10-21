package com.example.appointment;

import com.example.appointment.scheduling.application.DefineNewScheduleService;
import com.example.appointment.scheduling.application.FindFreeRangesService;
import com.example.appointment.scheduling.application.ReserveScheduleRangeService;
import com.example.appointment.scheduling.domain.freescheduleranges.FreeScheduleSlotRepository;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.scheduling.domain.schedule.WorkingHours;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


public class AppointmentReservationPerformanceTest {
    private FindFreeRangesService findFreeSlots;
    private DefineNewScheduleService defineNewScheduleService;
    private ReserveScheduleRangeService reserveScheduleRangeService;
    private FreeScheduleSlotRepository storage;
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();

    @BeforeMethod
    public void setUp() throws Exception {
        Factory factory = new Factory();
        findFreeSlots = factory.findFreeService(50);
        defineNewScheduleService = factory.scheduleDefinitionService();
        reserveScheduleRangeService = factory.reservationService();
        storage = factory.freeSlotRepository();
    }

    @Test
    public void shouldReserveAppointmentsStartingFromToday() throws Exception {
        generateNSchedules(100);
        reserveFirstFreeFor(() -> DateTestUtils.todayAt(8, 0));
    }

    @Test
    public void shouldReserveAppointmentsStartingFromRandom() throws Exception {
        generateNSchedules(100);
        reserveFirstFreeFor(() -> randomDate(DateTestUtils.todayAt(8, 0), DateTestUtils.todayAt(8, 0).plusDays(30)));
    }

    @Test
    public void shouldFindAppointmentsForManySchedules() throws Exception {
        generateNSchedules(500);
        int count = 0;
        List<LocalDateTime> randomDates = Lists.newArrayList();
        int datesCount = 700000;
        while (count < datesCount) {
            randomDates.add(randomDate(DateTestUtils.todayAt(8, 0), DateTestUtils.todayAt(8, 0).plusDays(90)));
            count++;
        }
        System.out.println("Random dates generated. Dates count is " + numberFormat.format(randomDates.size()));
        long maxTime = 5000;
        long start = System.currentTimeMillis();
        int searchCount = 0;
        long maxEnd = maxTime + start;
        Iterator<LocalDateTime> iterator = randomDates.iterator();
        Stopwatch stopwatch = Stopwatch.createStarted();
        while ((System.currentTimeMillis() < maxEnd) && iterator.hasNext()) {
            findFreeSlots.findFirstFree(iterator.next());
            searchCount++;
        }
        long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("free slots count : " + numberFormat.format(storage.size()));
        System.out.println("searches count : " + numberFormat.format(searchCount) + " in " + elapsedMs + " ms.");
        float searchesPerMs = (float) searchCount / elapsedMs;
        System.out.println("free res/s : " + numberFormat.format(1000 * searchesPerMs));
        System.out.println("free res/ms : " + numberFormat.format(searchesPerMs));

    }

    private void reserveFirstFreeFor(Supplier<LocalDateTime> date) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<ScheduleRange> freeScheduleRanges = findFreeSlots.findFirstFree(date.get());
        int count = 0;


        long maxTime = 5000;
        long start = System.currentTimeMillis();
        long maxEnd = maxTime + start;

        while (System.currentTimeMillis() < maxEnd && !freeScheduleRanges.isEmpty()) {
            reserveScheduleRangeService.reserve(freeScheduleRanges.get(0));
            freeScheduleRanges = findFreeSlots.findFirstFree(date.get());
            count++;
        }
        stopwatch.stop();
        long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("free slots count \t\t: " + storage.size());
        System.out.println("reservations count \t\t: " + count + " in " + elapsedMs + " ms.");
        System.out.println("res/s \t\t\t\t\t: " + (1000 * count / elapsedMs));
    }

    private void generateNSchedules(int n) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < n; i++) {
            defineNewScheduleService.addDailySchedule(WorkingHours.ofHours("08:00-16:00"), Duration.ofMinutes(15));
        }
        System.out.println("free slots count\t\t\t : " + storage.size());
        stopwatch.stop();
        long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("generateNSchedules took\t\t :" + elapsedMs);
    }

    private LocalDateTime randomDate(LocalDateTime start, LocalDateTime end) {
        double random = Math.random();
        long secondsBetweenStartEnd = Duration.between(start, end).getSeconds();
        LocalDateTime randomDateBetweenStartEnd = start.plusSeconds(Math.round(random * secondsBetweenStartEnd));
//        randomDateBetweenStartEnd = randomDateBetweenStartEnd.toLocalDate().atTime(LocalTime.of(9, 0));
        return randomDateBetweenStartEnd;
    }
}
