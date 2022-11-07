package org.yorksolutions.teamobjbackend.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Product
{
    @JsonProperty
    @Id
    String id;
    public String getId()
    {
        return id;
    }
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

    public Boolean getDiscontinued()
    {
        return discontinued;
    }

    @JsonProperty
    @ElementCollection
    List<String> categories;
    @JsonProperty
    Long startDate;
    @JsonProperty
    Double defaultPrice;

    public void setDiscontinued(Boolean discontinued)
    {
        this.discontinued = discontinued;
    }

    @JsonProperty
    Double defaultMAP;

    public void update(ProductDTO pdto)
    {
        if(pdto.productName != null)
        {
            this.productName = pdto.productName;
        }
        if(pdto.description != null)
        {
            this.description = pdto.description;
        }
        if(pdto.images != null)
        {
            this.images = pdto.images;
        }
        if(pdto.startDate != null)
        {
            this.startDate = pdto.startDate;
        }
        if(pdto.defaultMAP != null)
        {
            this.defaultMAP = pdto.defaultMAP;
        }
        if(pdto.defaultPrice != null)
        {
            this.defaultPrice = pdto.defaultPrice;
        }
    }

}
