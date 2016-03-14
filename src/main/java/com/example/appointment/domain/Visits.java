package com.example.appointment.domain;

import static java.util.Arrays.asList;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Visits {

  private static final Visits EMPTY = new Visits(Collections.emptyList());
  private final TreeSet<Visit> visits;

  public TreeSet<Visit> getVisits() {
    return visits;
  }

  public Visits(Collection<Visit> visits) {
    Comparator<Visit> dateScheduleIdComparator = Comparator.comparing(Visit::getDateTime)
        .thenComparing(Comparator.comparing(v -> v.scheduleId().toString()));

    this.visits = new TreeSet<>(dateScheduleIdComparator);
    this.visits.addAll(visits);
  }

  public static Visits of(List<Visit> visits) {
    return new Visits(visits);
  }

  public static Visits of(Visit... visits) {
    return new Visits(asList(visits));
  }

  public static Visits empty() {
    return EMPTY;
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
    return ToStringBuilder.reflectionToString(this);
  }

  public Visits withVisitAdded(Visit visit) {
    ArrayList<Visit> newVisits = new ArrayList<>(this.visits);
    newVisits.add(visit);
    return new Visits(newVisits);
  }
}
