package org.yorksolutions.teamobjbackend.embeddables;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;


@Embeddable
public class DateRanged<T>
{
    @JsonProperty
    public Double startDate;
    @JsonProperty
    public Double endDate;
    @JsonProperty
    public T t;


    public boolean Overlaps(DateRanged<T> other)
    {
        return startDate <= other.endDate && endDate >= other.startDate;
    }
    public boolean InRange(Double date)
    {
        return date >= startDate && date <= endDate;
    }
}
