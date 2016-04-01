package com.example.appointment;

import com.google.common.collect.Range;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

public class Slot {
    private final Range<LocalDateTime> visitRange;
    private final String service;
    private final String doctor;

    public Slot(Range<LocalDateTime> visitRange, String service, String doctor) {
        this.visitRange = visitRange;
        this.service = service;
        this.doctor = doctor;
    }

    public static Slot slotFor(Range<LocalDateTime> visitRange, String doctor, String service) {
        return new Slot(visitRange, service, doctor);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "Slot{" +
                "doctor='" + doctor + '\'' +
                ", visitRange=" + visitRange +
                ", service='" + service + '\'' +
                '}';
    }
}
