package com.falco.appointment.scheduling.infrastructure.restapi;

import com.falco.appointment.scheduling.api.FindFreeRangesService;
import com.falco.appointment.scheduling.api.ScheduleRange;
import com.falco.appointment.scheduling.api.SearchTags;
import com.falco.appointment.visitreservation.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
public class SearchFreeController {
    @Autowired
    private FindFreeRangesService findFreeService;

    @RequestMapping(value = "/searchFree", method = RequestMethod.GET)
    public List<FreeRange> searchFree(
            @RequestParam(value = "startingFrom", required = false) String startingFromStr,
            @RequestParam(value = "serviceId", required = false) String serviceId,
            @RequestParam(value = "doctorId", required = false) String doctorId,
            @RequestParam(value = "locationId", required = false) String locationId) {
        Optional<LocalDateTime> startingFrom = ofEmpty(startingFromStr).map(LocalDateTime::parse);
        LocalDateTime date = startingFrom.orElse(LocalDateTime.now());
        SearchTags searchTags = SearchTags.empty();
        if (ofEmpty(serviceId).isPresent()) {
            searchTags = searchTags.withTagAdded(ServiceTag.serviceIs(ServiceId.of(ofEmpty(serviceId).get())));
        }
        if (ofEmpty(doctorId).isPresent()) {
            searchTags = searchTags.withTagAdded(DoctorTag.doctorIs(DoctorId.of(ofEmpty(doctorId).get())));
        }
        if (ofEmpty(locationId).isPresent()) {
            searchTags = searchTags.withTagAdded(LocationTag.locationIs(LocationId.of(ofEmpty(locationId).get())));
        }


        List<ScheduleRange> firstFree = findFreeService.findFirstFree(date, searchTags);
        return firstFree.stream().map(this::transform).collect(toList());

    }

    private Optional<String> ofEmpty(String s) {
        return StringUtils.isBlank(s) ? Optional.empty() : Optional.of(s);
    }

    private FreeRange transform(ScheduleRange scheduleRange) {
        return new FreeRange()
                .withStart(scheduleRange.start().toString())
                .withDuration(scheduleRange.duration().toString())
                .withDoctorId("docId")
                .withServiceId("serviceId")
                .withScheduleId(scheduleRange.scheduleId().asString())
                ;
    }


}
