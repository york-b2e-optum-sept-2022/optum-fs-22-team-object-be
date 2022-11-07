package org.yorksolutions.teamobjbackend.embeddables;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Coupon extends DateRanged<Double>
{
    @JsonProperty
    String id;

}
