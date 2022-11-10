package org.yorksolutions.teamobjbackend.embeddables;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * POJO Coupon that can be embedded by JPA
 *
 * @author Max Feige
 */
@SuppressWarnings("unused")
@Embeddable
public class Coupon
{
    /**
     * String coupon code for coupon
     */
    @JsonProperty
    public String code;
    /**
     * Percent off the coupon gives
     */
    @JsonProperty
    public Double sale;

    /**
     * Start date of the coupon
     */
    @JsonProperty
    public Long startDate;
    /**
     * End date of the coupon
     */
    @JsonProperty
    public Long endDate;

    /**
     * Checks if there is overlap in the date ranges between this coupon and other
     * @param other the coupon to check for overlap
     * @return true if there is overlap, false otherwise
     */
    public boolean Overlaps(Coupon other)
    {
        return startDate <= other.endDate && endDate >= other.startDate;
    }

    /**
     * Checks if the given date is in the range of this coupon
     * @param date Date to check if in range
     * @return true if in range, false otherwise
     */
    public boolean InRange(Long date)
    {
        return date >= startDate && date <= endDate;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coupon coupon = (Coupon) o;
        return code.equals(coupon.code);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(code);
    }
}
