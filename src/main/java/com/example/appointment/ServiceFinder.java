package com.example.appointment;

import com.example.appointment.domain.schedule.ServiceId;

public interface ServiceFinder {
    String findServiceName(ServiceId serviceId);

    ServiceId findServiceId(String serviceName);
}
