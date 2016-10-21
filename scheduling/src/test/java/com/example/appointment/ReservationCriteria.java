package com.example.appointment;

import com.example.appointment.scheduling.domain.SearchTags;
import com.example.appointment.scheduling.domain.TagValue;

import java.time.LocalDateTime;

public class ReservationCriteria {
    private final LocalDateTime startingFrom;
    private final SearchTags searchTags;

    public ReservationCriteria() {
        this.startingFrom = null;
        this.searchTags = SearchTags.empty();
    }


    public ReservationCriteria(LocalDateTime startingFrom, SearchTags searchTags) {
        this.startingFrom = startingFrom;
        this.searchTags = searchTags;
    }


    public LocalDateTime getStartingFrom() {
        return startingFrom;
    }

    public ReservationCriteria withTag(TagValue service) {
        SearchTags searchTags = this.searchTags.withTagAdded(service);
        return withTags(searchTags);
    }

    public ReservationCriteria withTags(SearchTags searchTags) {
        return new ReservationCriteria(startingFrom, searchTags);
    }

    public ReservationCriteria withTags(TagValue... tags) {
        return withTags(searchTags.withTags(tags));
    }

    public ReservationCriteria startingFrom(LocalDateTime dateTime) {
        return new ReservationCriteria(dateTime, searchTags);
    }

    public SearchTags searchTags() {
        return searchTags;
    }

    @Override
    public String toString() {
        return "ReservationCriteria{" +
                "searchTags='" + searchTags + '\'' +
                ", startingFrom=" + startingFrom +
                '}';
    }
}
