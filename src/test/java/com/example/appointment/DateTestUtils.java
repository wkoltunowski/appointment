package com.example.appointment;

import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTestUtils {
    public static Range<LocalDateTime> todayBetween(String timeRangeStr) {
        return toRange(LocalDate.now(), timeRangeStr);
    }

    public static Range<LocalDateTime> tommorrow(String rangeStr) {
        return toRange(LocalDate.now().plusDays(1), rangeStr);
    }

    public static Range<LocalDateTime> toRange(LocalDate date, String rangeStr) {
        String[] split = rangeStr.split("-");
        LocalTime startTime = parseTime(split[0]);
        LocalTime endTime = parseTime(split[1]);
        int daysToAdd = startTime.isAfter(endTime) ? 1 : 0;
        return Range.closedOpen(date.atTime(startTime), date.plusDays(daysToAdd).atTime(endTime));
    }

    public static LocalTime parseTime(String from) {
        return LocalTime.parse(from);
    }

    public static LocalDateTime tommorrowAt(int hour, int minute) {
        return todayBetween(hour, minute).plusDays(1);
    }

    public static LocalDateTime todayBetween(int hour, int minute) {
        return LocalDate.now().atTime(LocalTime.of(hour, minute));
    }

    public static LocalDateTime todayAt(String hour) {
        return LocalDate.now().atTime(LocalTime.parse(hour));
    }
}
