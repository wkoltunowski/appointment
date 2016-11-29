package com.falco.appointment.scheduling;

import com.falco.testsupport.DateRandomizer;
import com.falco.testsupport.DateTestUtils;
import com.falco.appointment.Factory;
import com.falco.appointment.scheduling.application.DefineNewScheduleService;
import com.falco.appointment.scheduling.application.FindFreeRangesService;
import com.falco.appointment.scheduling.application.ReserveScheduleRangeService;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.falco.appointment.scheduling.domain.schedule.WorkingHours;
import com.google.common.base.Stopwatch;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.falco.testsupport.PerformanceUtils.runSpeedCheck;


public class ReservationPerformanceTest {
    private FindFreeRangesService findFreeSlots;
    private DefineNewScheduleService defineNewScheduleService;
    private ReserveScheduleRangeService reserveScheduleRangeService;
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private DateRandomizer dateRandomizer = new DateRandomizer(700000);
    private int runningTimeSecs;
    private int threadsCount;
    private long maxTime;


    @BeforeMethod
    public void setUp() throws Exception {
        Factory factory = new Factory();
        findFreeSlots = factory.findFreeService(1);
        defineNewScheduleService = factory.scheduleDefinitionService();
        reserveScheduleRangeService = factory.reservationService();
        runningTimeSecs = 22;
        threadsCount = 4;
        maxTime = 10;
    }

    @Test
    public void shouldWorkFast2FirstFree() throws Exception {
        generateNSchedules(900);
        runSpeedCheck(() -> findFreeSlots.findFirstFree(dateRandomizer.randomDate()), "findFirstFree", runningTimeSecs, threadsCount);
    }

    @Test
    public void shouldWorkFast2ReserveAll() throws Exception {
        generateNSchedules(900);
        runSpeedCheck(() -> {
            for (ScheduleRange scheduleRange : findFreeSlots.findFirstFree(dateRandomizer.randomDate())) {
                reserveScheduleRangeService.reserve(scheduleRange);
            }
        }, "search & reserve All reservation ", runningTimeSecs, threadsCount);

    }

    @Test
    public void shouldWorkFast2ReserveFirst() throws Exception {
        generateNSchedules(900);
        runSpeedCheck(() -> reserveScheduleRangeService.reserve(findFreeSlots.findFirstFree(dateRandomizer.randomDate()).get(0)),
                "search & reserve reservation ", runningTimeSecs, threadsCount);

    }


    @Test
    public void shouldReserveAppointmentsStartingFromToday() throws Exception {
        generateNSchedules(900);
        LocalDateTime localDateTime = DateTestUtils.todayAt(8, 0);
        reserveFirstFreeFor(() -> localDateTime, "for same date", maxTime);
    }

    @Test
    public void shouldReserveAppointmentsStartingFromRandom() throws Exception {
        generateNSchedules(900);
        reserveFirstFreeFor(() -> dateRandomizer.randomDate(), "for random dates", maxTime);
    }

    @Test
    public void shouldFindAppointmentsForManySchedules() throws Exception {
        generateNSchedules(900);
        int searchCount = 0;
        Stopwatch stopwatch = Stopwatch.createStarted();
        while (stopwatch.elapsed(TimeUnit.SECONDS) < maxTime) {
            findFreeSlots.findFirstFree(dateRandomizer.randomDate());
            searchCount++;
        }
        long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        float searchesPerMs = (float) searchCount / elapsedMs;
        logTime("searching speed res/s ", numberFormat.format(1000 * searchesPerMs));
    }

    public static void logTime(String msg, String val) {
        logTime(msg, val, 50);
    }

    public static void logTime(String msg, String val, int padding) {
        System.out.printf("%-" +
                padding +
                "s %s%n", msg, val);
    }

    private void reserveFirstFreeFor(Supplier<LocalDateTime> date, String title, long maxTime) {
        List<ScheduleRange> freeScheduleRanges = findFreeSlots.findFirstFree(date.get());
        int count = 0;


        Stopwatch stopwatch = Stopwatch.createStarted();

        while (stopwatch.elapsed(TimeUnit.SECONDS) < maxTime && !freeScheduleRanges.isEmpty()) {
            reserveScheduleRangeService.reserve(freeScheduleRanges.get(0));
            freeScheduleRanges = findFreeSlots.findFirstFree(date.get());
            count++;
        }
        stopwatch.stop();
        long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        logTime("reservation speed " + title + " res/s ", numberFormat.format(1000 * count / elapsedMs));
    }

    private void generateNSchedules(int n) {
        for (int i = 0; i < n; i++) {
            defineNewScheduleService.addDailySchedule(WorkingHours.ofHours("08:00-16:00"), Duration.ofMinutes(15));
        }
    }


}
