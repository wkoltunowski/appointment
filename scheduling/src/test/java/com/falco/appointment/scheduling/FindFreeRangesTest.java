package com.falco.appointment.scheduling;

import com.falco.appointment.Application;
import com.falco.appointment.scheduling.application.DefineNewScheduleService;
import com.falco.appointment.scheduling.application.FindFreeRangesService;
import com.falco.appointment.scheduling.domain.SearchTags;
import com.falco.appointment.scheduling.domain.TagValue;
import com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange;
import com.falco.appointment.scheduling.domain.freescheduleranges.SearchCriteria;
import com.falco.appointment.scheduling.domain.schedule.ScheduleId;
import com.falco.appointment.scheduling.domain.schedule.WorkingHours;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.falco.testsupport.DateTestUtils.*;
import static com.falco.appointment.scheduling.domain.freescheduleranges.ScheduleRange.scheduleRange;
import static com.falco.appointment.scheduling.domain.schedule.WorkingHours.ofHours;
import static java.time.Duration.ofMinutes;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class FindFreeRangesTest {

    private FindFreeRangesService freeSlots;
    private DefineNewScheduleService defineNewScheduleService;


    @BeforeMethod
    public void setUp() throws Exception {
        Application app = new Application();
        defineNewScheduleService = app.defineNewScheduleService();
        freeSlots = app.findFreeSlots(10);
    }

    @Test
    public void shouldFindFirst10Appointments() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-16:00"), ofMinutes(15));
        assertThat(search(todayAt(8, 0)), startsWith(
                scheduleRange(todayBetween("08:00-08:15"), scheduleId),
                scheduleRange(todayBetween("08:15-08:30"), scheduleId),
                scheduleRange(todayBetween("08:30-08:45"), scheduleId),
                scheduleRange(todayBetween("08:45-09:00"), scheduleId),
                scheduleRange(todayBetween("09:00-09:15"), scheduleId),
                scheduleRange(todayBetween("09:15-09:30"), scheduleId),
                scheduleRange(todayBetween("09:30-09:45"), scheduleId),
                scheduleRange(todayBetween("09:45-10:00"), scheduleId),
                scheduleRange(todayBetween("10:00-10:15"), scheduleId),
                scheduleRange(todayBetween("10:15-10:30"), scheduleId)));
    }

    @Test
    public void shouldFindAppointmentForSecondSlot() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));

        assertThat(search(todayAt(8, 1)), startsWith(scheduleRange(todayBetween("08:15-08:30"), scheduleId)));
        assertThat(search(todayAt(8, 10)), startsWith(scheduleRange(todayBetween("08:15-08:30"), scheduleId)));
        assertThat(search(todayAt(8, 15)), startsWith(scheduleRange(todayBetween("08:15-08:30"), scheduleId)));
    }

    @Test
    public void shouldFindAppointmentWhenInMiddleRequested() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:30"), ofMinutes(15));
        assertThat(search(todayAt(8, 10)), startsWith(scheduleRange(todayBetween("08:15-08:30"), scheduleId)));
    }

    @Test
    public void shouldFindAppointmentOverNight() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("23:30-00:30"), ofMinutes(30));

        assertThat(search(todayAt(23, 0)),
                startsWith(
                        scheduleRange(todayBetween("23:30-00:00"), scheduleId),
                        scheduleRange(tommorrow("00:00-00:30"), scheduleId)));
    }

    @Test
    public void shouldFindNextDay() throws Exception {
        ScheduleId scheduleId = defineNewScheduleService.addDailySchedule(ofHours("08:00-08:20"), ofMinutes(15));

        assertThat(search(todayAt(8, 20)), startsWith(scheduleRange(tommorrow("08:00-08:15"), scheduleId)));
    }

    @Test
    public void shouldFindEmptyForNoSchedules() throws Exception {
        assertThat(search(todayAt(8, 0)), hasSize(0));
    }

    @Test
    public void shouldFindOrderedAppointmentsForTwoSchedules() throws Exception {
        ScheduleId schedule1 = defineNewScheduleService.addDailySchedule(ofHours("15:00-16:00"), ofMinutes(10));
        ScheduleId schedule2 = defineNewScheduleService.addDailySchedule(ofHours("15:05-16:00"), ofMinutes(10));

        assertThat(search(todayAt(15, 40)), startsWith(
                scheduleRange(todayBetween("15:40-15:50"), schedule1),
                scheduleRange(todayBetween("15:45-15:55"), schedule2),
                scheduleRange(todayBetween("15:50-16:00"), schedule1)
        ));
    }

    @Test
    public void shouldFindSlotByTag() throws Exception {
        ScheduleId howardSchedule = givenSchedule(ofHours("08:00-15:00"), ofMinutes(15), drHoward());
        ScheduleId smithSchedule = givenSchedule(ofHours("08:00-15:00"), ofMinutes(15), drSmith());

        assertThat(search(tommorrowAt(8, 0), drHoward()), startsWith(scheduleRange(tommorrow("08:00-08:15"), howardSchedule)));
    }

    @Test
    public void shouldNotFindSlotByTag() throws Exception {
        givenSchedule(ofHours("08:00-15:00"), ofMinutes(15), drHoward());
        assertThat(search(tommorrowAt(8, 0), drSmith()), hasSize(0));
    }


    @Test
    public void shouldFindSlotBy2Tags() throws Exception {
        givenSchedule(ofHours("08:00-15:00"), ofMinutes(15), drHoward(), surgery());
        ScheduleId howardConsultation = givenSchedule(ofHours("08:00-15:00"), ofMinutes(15), drHoward(), consultation());
        givenSchedule(ofHours("08:00-15:00"), ofMinutes(15), drSmith(), consultation());

        assertThat(search(tommorrowAt(8, 0), drHoward(), consultation()), startsWith(scheduleRange(tommorrow("08:00-08:15"), howardConsultation)));
    }

    private TagValue surgery() {
        return serviceTag("SURGERY");
    }

    private TagValue drHoward() {
        return docTag("HOWARD");
    }

    private TagValue consultation() {
        return serviceTag("CONSULTATION");
    }

    private TagValue drSmith() {
        return docTag("SMITH");
    }

    @Test
    public void shouldNotFindTooBigAppointment() throws Exception {
        givenSchedule(ofHours("08:00-08:10"), ofMinutes(15), drSmith());

        assertThat(search(tommorrowAt(8, 0), drSmith()), hasSize(0));
    }


    private TagValue serviceTag(String service) {
        return new TagValue("SERVICE", service);
    }

    private TagValue docTag(String howard) {
        return new TagValue("DOC", howard);
    }

    private List<ScheduleRange> search(LocalDateTime startingAt, TagValue... tags) {
        SearchCriteria crit = new SearchCriteria(startingAt).withTagValue(tags);

        return freeSlots.findFirstFree(crit);
    }

    private ScheduleId givenSchedule(WorkingHours workingHours, Duration duration, TagValue... tags) {
        return defineNewScheduleService.addDailySchedule(workingHours, duration, SearchTags.ofTags(tags));
    }

    private <T> Matcher<? super List<T>> startsWith(T element, T... elements) {
        List<Object> elementsList = new ArrayList<>();
        elementsList.add(element);
        elementsList.addAll(asList(elements));
        return new BaseMatcher<List<T>>() {
            @Override
            public boolean matches(Object item) {
                return ((List<T>) item).subList(0, elements.length + 1).equals(elementsList);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(elementsList);
            }
        };
    }


}
