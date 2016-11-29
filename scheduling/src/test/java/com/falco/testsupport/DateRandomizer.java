package com.falco.testsupport;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

public class DateRandomizer {
    private final List<LocalDateTime> randomDates;
    private Iterator<LocalDateTime> randomDatesIterator;

    public DateRandomizer(int range) {
        this.randomDates = randomDates(range);
        this.randomDatesIterator = randomDates.iterator();
    }


    public LocalDateTime randomDate() {
        if (!randomDatesIterator.hasNext()) {
            randomDatesIterator = randomDates.iterator();
        }
        return randomDatesIterator.next();
    }


    private List<LocalDateTime> randomDates(int n) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        int count = 0;
        List<LocalDateTime> randomDates = Lists.newArrayList();
        while (count < n) {
            randomDates.add(randomDate(DateTestUtils.todayAt(8, 0), DateTestUtils.todayAt(8, 0).plusDays(90)));
            count++;
        }
        System.out.println("random dates of " + n + " took :" + stopwatch);
        return randomDates;
    }

    private LocalDateTime randomDate(LocalDateTime start, LocalDateTime end) {
        double random = Math.random();
        long secondsBetweenStartEnd = Duration.between(start, end).getSeconds();
        return start.plusSeconds(Math.round(random * secondsBetweenStartEnd));
    }

}
