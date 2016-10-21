package com.example.appointment.scheduling.domain.schedule;

import com.example.appointment.scheduling.domain.SearchTags;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface Schedule {
    ScheduleId scheduleId();

    SearchTags searchTags();

    Optional<Duration> duration();

    List<Range<LocalDateTime>> dates(Range<LocalDate> range);
}
