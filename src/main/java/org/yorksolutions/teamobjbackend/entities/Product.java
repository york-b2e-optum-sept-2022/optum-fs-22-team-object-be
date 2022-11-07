package org.yorksolutions.teamobjbackend.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product
{



    @JsonProperty
    @Id
    String productID;
    public String getProductID()
    {
        return productID;
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
    protected Product()
    {

    }
    public Product(String id)
    {
        this.productID = id;
    }

    @JsonProperty
    @ElementCollection
    List<String> images;
    @JsonProperty
    Boolean discontinued = false;

    public Boolean getDiscontinued()
    {
        return discontinued;
    }

    @JsonProperty
    @ElementCollection
    List<String> categories = new ArrayList<>();
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

    public List<DateRanged<Double>> getMapList()
    {
        return mapList;
    }

    public List<DateRanged<Double>> getPricesList()
    {
        return pricesList;
    }

    public List<DateRanged<Double>> getSalesList()
    {
        return salesList;
    }

    public List<Coupon> getCouponList()
    {
        return couponList;
    }

    public void setMapList(List<DateRanged<Double>> mapList)
    {
        this.mapList = mapList;
    }

    public void setPricesList(List<DateRanged<Double>> pricesList)
    {
        this.pricesList = pricesList;
    }

    public void setSalesList(List<DateRanged<Double>> salesList)
    {
        this.salesList = salesList;
    }

    public void setCouponList(List<Coupon>couponList)
    {
        this.couponList = couponList;
    }

}
