package org.yorksolutions.teamobjbackend.embeddables;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.util.Objects;

@Embeddable
public class Coupon
{
    @JsonProperty
    public String code;
    @JsonProperty
    public Double sale;

    @JsonProperty
    public Long startDate;
    @JsonProperty
    public Long endDate;

    public boolean Overlaps(Coupon other)
    {
        return startDate <= other.endDate && endDate >= other.startDate;
    }
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
