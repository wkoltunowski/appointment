package com.falco.appointment.scheduling.domain.freescheduleranges;

import com.falco.appointment.scheduling.domain.SearchTags;
import com.falco.appointment.scheduling.domain.TagValue;

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

    public SearchCriteria withTagValue(TagValue... tagValues) {
        SearchTags tags = searchTags;
        for (TagValue tagValue : tagValues) {
            tags = tags.withTagAdded(tagValue);
        }
        return withTags(tags);
    }


    public SearchCriteria withTags(SearchTags searchTags) {
        return new SearchCriteria(startingFrom, searchTags);
    }


    public SearchTags searchTags() {
        return this.searchTags;
    }
}
