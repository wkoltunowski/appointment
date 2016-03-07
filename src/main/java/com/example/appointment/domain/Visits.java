package com.example.appointment.domain;

import static com.sun.deploy.util.StringUtils.*;
import static java.util.Arrays.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Visits visits1 = (Visits) o;

    return visits.equals(visits1.visits);

  }

  @Override
  public int hashCode() {
    return visits.hashCode();
  }

  @Override
  public String toString() {

    return "Visits{" +
        "visits:\n" + join(visits.stream().map(Visit::toString).collect(Collectors.toList()), ",\n") +
        '}';
  }

  public Visits withVisitAdded(Visit visit) {
    ArrayList<Visit> newVisits = new ArrayList<>(this.visits);
    newVisits.add(visit);
    return new Visits(newVisits);
  }
}
