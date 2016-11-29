package com.falco.appointment.scheduling;

import com.falco.testsupport.DateRandomizer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.falco.testsupport.PerformanceUtils.logTime;
import static com.falco.testsupport.PerformanceUtils.runSpeedCheck;


public class ReservationPerformanceRestTest {

    private RestTemplate restTemplate;
    private DateRandomizer dateRandomizer = new DateRandomizer(700000);

    @BeforeMethod
    public void setUp() throws Exception {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    public void shouldReserve() throws Exception {
        int threadsCount = 8;
        int runningTimeSecs = 10;
        for (int i = 1; i <= threadsCount; ) {
            run(i, runningTimeSecs);
            i = i + 2;
        }
    }

    private void run(int threadsCount, int runningTimeSecs) {
        logTime("threads", threadsCount);
        runSpeedCheck(this::initTestData, "initTestData", runningTimeSecs, threadsCount);
        runSpeedCheck(this::searchFree, "searchFree", runningTimeSecs, threadsCount);
        runSpeedCheck(this::reserveFirstFree, "reserve first", runningTimeSecs, threadsCount);
    }

    private String initTestData() {
        return runRest("initTestData", new HashMap<>(), String.class);
    }

    private List searchFree() {
        LocalDateTime start = dateRandomizer.randomDate();
        HashMap<String, String> params = new HashMap<>();
        params.put("startingFrom", start.toString());
        return runRest("searchFree", params, List.class);
    }


    private void reserveFirstFree() {
        List<Map<String, String>> found = searchFree();
        Map<String, String> params = new HashMap<>(found.get(0));
        params.put("start", params.get("start"));
        params.put("patientId", "7da9af23-a1c5-412d-aa1c-ad08b0c52de0");
        runRest("reserve", params, String.class);
    }


    private <T> T runRest(String action, Map<String, String> urlVariables, Class<T> clazz) {
        String baseUrl = "http://localhost:8080/" + action;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
        for (Map.Entry<String, String> params : urlVariables.entrySet()) {
            builder = builder.queryParam(params.getKey(), params.getValue());
        }
        return restTemplate.getForObject(builder.build().toUri(), clazz);
    }

}
