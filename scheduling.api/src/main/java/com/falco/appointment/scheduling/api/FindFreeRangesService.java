package com.falco.appointment.scheduling.api;

import java.time.LocalDateTime;
import java.util.List;

public interface FindFreeRangesService {
    List<ScheduleRange> findFirstFree(LocalDateTime startingFrom);

    List<ScheduleRange> findFirstFree(LocalDateTime startingFrom, SearchTags searchTags);
}
