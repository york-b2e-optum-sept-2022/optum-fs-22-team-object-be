package org.yorksolutions.teamobjbackend.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;


@Setter
@Getter
@ToString
@Entity
@Table
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
//why JsonIgnore? and what is ElementCollection?
    List<Coupon> couponList;

    public List<Coupon> getCouponList(){
        return couponList;
    }



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
    Double defaultMAP;
}
