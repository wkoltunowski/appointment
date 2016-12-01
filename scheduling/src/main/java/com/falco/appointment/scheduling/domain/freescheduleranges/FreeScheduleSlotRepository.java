package com.falco.appointment.scheduling.domain.freescheduleranges;

import com.falco.appointment.scheduling.api.ScheduleRange;
import com.falco.appointment.scheduling.api.SearchTags;
import com.falco.appointment.scheduling.api.ScheduleId;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FreeScheduleSlotRepository {

    void addAll(Collection<FreeScheduleSlot> freeScheduleSlots);

    void remove(FreeScheduleSlot freeScheduleSlot);

    long size();

    Optional<FreeScheduleSlot> findByScheduleRange(ScheduleRange scheduleRange);

    List<FreeScheduleSlot> findByScheduleId(ScheduleId scheduleId);

    Iterable<FreeScheduleSlot> findAfter(LocalDateTime startingFrom);

    SearchTags findTags(ScheduleId scheduleId);
}
