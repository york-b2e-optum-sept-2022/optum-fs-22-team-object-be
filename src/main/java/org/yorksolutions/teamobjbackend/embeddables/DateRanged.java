package org.yorksolutions.teamobjbackend.embeddables;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;


/**
 * Encapsulates a type with a start date and end date
 * @param <T> Type to be encapsulated
 */
@SuppressWarnings("unused")
@Embeddable
public class DateRanged<T>
{
    /**
     * Start date of the item
     */
    @JsonProperty
    public Long startDate;
    /**
     * End date of the item
     */
    @JsonProperty
    public Long endDate;
    /**
     * item being encapsulated
     */
    @JsonProperty
    public T item;

    /**
     * Checks if there is overlap in the date ranges between this DateRanged and other
     * @param other the DateRanged to check for overlap
     * @return true if there is overlap, false otherwise
     */
    public boolean Overlaps(DateRanged<T> other)
    {
        return startDate <= other.endDate && endDate >= other.startDate;
    }
    /**
     * Checks if the given date is in the range of this DateRanged
     * @param date Date to check if in range
     * @return true if in range, false otherwise
     */
    public boolean InRange(Long date)
    {
        return date >= startDate && date <= endDate;
    }
}
