package com.example.appointment.scheduling.infrastructure.restapi;

import com.example.appointment.scheduling.application.FindFreeRangesService;
import com.example.appointment.scheduling.domain.SearchTags;
import com.example.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.example.appointment.scheduling.domain.freescheduleranges.SearchCriteria;
import com.example.appointment.scheduling.domain.schedule.Schedule;
import com.example.appointment.scheduling.domain.schedule.ScheduleRepository;
import com.example.appointment.visitreservation.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
public class SearchFreeController {
    @Autowired
    private FindFreeRangesService findFreeService;
    @Autowired
    private ScheduleRepository scheduleRepository;

    @RequestMapping(value = "/searchFree", method = RequestMethod.GET)
    public List<FreeRange> searchFree(
            @RequestParam(value = "startingFrom", required = false) String startingFromStr,
            @RequestParam(value = "serviceId", required = false) String serviceId,
            @RequestParam(value = "doctorId", required = false) String doctorId,
            @RequestParam(value = "locationId", required = false) String locationId) {
        Optional<LocalDateTime> startingFrom = ofEmpty(startingFromStr).map(ZonedDateTime::parse).map(ZonedDateTime::toLocalDateTime);
        LocalDateTime date = startingFrom.orElse(LocalDateTime.now());
        SearchTags searchTags = SearchTags.empty();
        if (ofEmpty(serviceId).isPresent()){
            searchTags = searchTags.withTagAdded(ServiceTag.serviceIs(ServiceId.of(ofEmpty(serviceId).get())));
        }
        if (ofEmpty(doctorId).isPresent()){
            searchTags = searchTags.withTagAdded(DoctorTag.doctorIs(DoctorId.of(ofEmpty(doctorId).get())));
        }
        if (ofEmpty(locationId).isPresent()){
            searchTags = searchTags.withTagAdded(LocationTag.locationIs(LocationId.of(ofEmpty(locationId).get())));
        }


        List<ScheduleRange> firstFree = findFreeService.findFirstFree(new SearchCriteria(date, searchTags));
        return firstFree.stream().map(this::transform).collect(toList());

    }

    private Optional<String> ofEmpty(String s) {
        return StringUtils.isBlank(s) ? Optional.empty() : Optional.of(s);
    }

    private FreeRange transform(ScheduleRange scheduleRange) {
        Schedule schedule = scheduleRepository.findById(scheduleRange.scheduleId());
        return new FreeRange()
                .withStart(scheduleRange.start().toString())
                .withDuration(scheduleRange.duration().toString())
                .withDoctorId(doctorId(schedule).orElse(null))
                .withServiceId(serviceId(schedule).orElse(null))
                .withScheduleId(schedule.scheduleId().asString())
                ;
    }

    private Optional<String> serviceId(Schedule schedule) {
        return schedule.searchTags().get(ServiceTag.key());
    }

    private Optional<String> doctorId(Schedule schedule) {
        return schedule.searchTags().get(DoctorTag.key());
    }
}
