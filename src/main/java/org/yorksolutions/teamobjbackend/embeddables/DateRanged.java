package org.yorksolutions.teamobjbackend.embeddables;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;


@SuppressWarnings("unused")
@Embeddable
public class DateRanged<T>
{
    @JsonProperty
    public Long startDate;
    @JsonProperty
    public Long endDate;
    @JsonProperty
    public T item;


    public boolean Overlaps(DateRanged<T> other)
    {
        return startDate <= other.endDate && endDate >= other.startDate;
    }
    public boolean InRange(Long date)
    {
        return date >= startDate && date <= endDate;
    }
}
