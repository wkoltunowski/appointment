package com.example.appointment.scheduling.domain.freescheduleranges;

import com.example.appointment.scheduling.domain.SearchTags;
import com.example.appointment.scheduling.domain.TagValue;

import java.time.LocalDateTime;

public class SearchCriteria {

    private final LocalDateTime startingFrom;
    private final SearchTags searchTags;

    public SearchCriteria(LocalDateTime startingFrom) {
        this(startingFrom, SearchTags.empty());
    }

    public SearchCriteria(LocalDateTime startingFrom, SearchTags searchTags) {
        this.startingFrom = startingFrom;
        this.searchTags = searchTags;
    }

    public SearchCriteria withTag(TagValue serviceTag) {
        return withTagValue(serviceTag);
    }


    public LocalDateTime getStartingFrom() {
        return startingFrom;
    }

    public SearchCriteria withTagValue(TagValue tagValue) {
        return withTags(searchTags.withTagAdded(tagValue));
    }

    public SearchCriteria withTags(SearchTags searchTags) {
        return new SearchCriteria(startingFrom, searchTags);
    }


    public SearchTags searchTags() {
        return this.searchTags;
    }
}
