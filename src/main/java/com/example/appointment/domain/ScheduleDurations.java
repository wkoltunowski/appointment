package com.example.appointment.domain;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.appointment.domain.ScheduleId;

public class ScheduleDurations {

  private Map<ScheduleId, Duration> durations = new HashMap<>();

  public Duration durationFor(ScheduleId scheduleId) {
    return Optional.ofNullable(durations.get(scheduleId)).orElseThrow(IllegalArgumentException::new);
  }

  public void defineDuration(ScheduleId scheduleId, Duration duration) {
    this.durations.put(scheduleId, duration);
  }
}
