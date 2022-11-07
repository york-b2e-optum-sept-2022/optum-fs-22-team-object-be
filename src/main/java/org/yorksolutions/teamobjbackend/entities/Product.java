package org.yorksolutions.teamobjbackend.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Product
{
    @Id
    String id;

    @JsonIgnore
    @ElementCollection
    List<DateRanged<Double>> mapList;

    @JsonIgnore
    @ElementCollection
    List<DateRanged<Double>> pricesList;

    @JsonIgnore
    @ElementCollection
    List<DateRanged<Double>> salesList;

    @JsonIgnore
    @ElementCollection
    List<Coupon> couponList;


    @JsonProperty
    String productName;
    @JsonProperty
    String description;
    @JsonProperty
    @ElementCollection
    List<String> images;
    @JsonProperty
    Boolean discontinued;
    @JsonProperty
    @ElementCollection
    List<String> categories;
    @JsonProperty
    Long startDate;
    @JsonProperty
    Double defaultPrice;
    @JsonProperty
    Double defaultMap;
}
