package com.example.appointment.scheduling.infrastructure.restapi;

import com.example.appointment.scheduling.application.FindFreeScheduleRangesService;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class SearchFreeController {
    @Autowired
    private FindFreeScheduleRangesService findFreeService;

    @RequestMapping(value = "/searchFree", method = RequestMethod.GET)
    public List<FreeRange> searchFree() {
        List<ScheduleRange> firstFree = findFreeService.findFirstFree(LocalDateTime.now());
        return firstFree.stream().map(this::transform).collect(toList());

    }

    private FreeRange transform(ScheduleRange scheduleRange) {
        return new FreeRange(scheduleRange.scheduleId(), scheduleRange.range());
    }
}
