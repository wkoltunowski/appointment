package com.falco.appointment.performance;

import com.falco.appointment.Factory;
import com.falco.appointment.scheduling.api.FindFreeRangesService;
import com.falco.appointment.scheduling.api.ReservationService;
import com.falco.appointment.scheduling.api.ScheduleRange;
import com.falco.appointment.scheduling.application.DefineNewScheduleService;
import com.falco.appointment.scheduling.domain.schedule.WorkingHours;
import com.falco.testsupport.DateRandomizer;
import com.falco.testsupport.PerformanceUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.falco.testsupport.DateTestUtils.todayAt;


public class ReservationPerformanceTest {
    private FindFreeRangesService findFreeSlots;
    private DefineNewScheduleService defineNewScheduleService;
    private ReservationService reservationService;
    private DateRandomizer dateRandomizer = new DateRandomizer(700000);
    private int runningTimeSecs;
    private int threadsCount;


    @BeforeMethod
    public void setUp() {
        Factory factory = new Factory();
        findFreeSlots = factory.findFreeService(1);
        defineNewScheduleService = factory.scheduleDefinitionService();
        reservationService = factory.reservationService();
        runningTimeSecs = 22;
        threadsCount = 4;
        runningTimeSecs = 10;
        threadsCount = 8;

        generateNSchedules(1500);
    }

    @Test
    public void shouldFindFirstFreeOnly() {
        runSpeedCheckMultiThread("FindFirstFree", () -> findFreeSlots.findFirstFree(randomDate()));
    }

    @Test
    public void shouldWorkFast2ReserveAll() {
        runSpeedCheckMultiThread("Search & Reserve All ", () -> {
            for (ScheduleRange scheduleRange : findFreeSlots.findFirstFree(randomDate())) {
                reservationService.reserve(scheduleRange);
            }
        });
    }

    @Test
    public void shouldWorkFast2ReserveFirst() {
        runSpeedCheckMultiThread("Search & Reserve", () -> reservationService.reserve(findFreeSlots.findFirstFree(randomDate()).get(0)));
    }

    @Test
    public void shouldWorkFast2ReserveFirstSingleThread() {
        runSpeedCheckSingleThread("Search & Reserve ST", () -> reservationService.reserve(findFreeSlots.findFirstFree(randomDate()).get(0)));
    }

    @Test
    public void shouldReserveAppointmentsStartingFromToday() {
        LocalDateTime today8am = todayAt(8, 0);
        runSpeedCheckSingleThread("Search & Reserve today8am", () -> reservationService.reserve(findFreeSlots.findFirstFree(today8am).get(0)));
    }

    @Test
    public void shouldReserveAppointmentsStartingFromRandom() {
        runSpeedCheckSingleThread("Search & Reserve random date", () -> reservationService.reserve(findFreeSlots.findFirstFree(randomDate()).get(0)));
    }

    private LocalDateTime randomDate() {
        return dateRandomizer.randomDate();
    }

    private void runSpeedCheckSingleThread(String msg, Runnable runnable) {
        PerformanceUtils.runSpeedCheck(msg, runnable, runningTimeSecs, 1);
    }

    private void runSpeedCheckMultiThread(String msg, Runnable runnable) {
        PerformanceUtils.runSpeedCheck(msg, runnable, runningTimeSecs, threadsCount);
    }

    private void generateNSchedules(int n) {
        for (int i = 0; i < n / 2; i++) {
            defineNewScheduleService.addDailySchedule(WorkingHours.ofHours("08:00-16:00"), Duration.ofMinutes(15));
            defineNewScheduleService.addDailySchedule(WorkingHours.ofHours("14:00-22:00"), Duration.ofMinutes(15));
        }
    }
}