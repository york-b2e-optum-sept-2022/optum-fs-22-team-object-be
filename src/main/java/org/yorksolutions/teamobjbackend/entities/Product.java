package org.yorksolutions.teamobjbackend.entities;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
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
    final
    List<DateRanged<Double>> mapList = new ArrayList<>();

    @JsonIgnore
    @ElementCollection
    final
    List<DateRanged<Double>> pricesList = new ArrayList<>();

    public String getProductName()
    {
        return productName;
    }

    @JsonIgnore
    @ElementCollection
    final
    List<DateRanged<Double>> salesList = new ArrayList<>();

    @JsonIgnore
    @ElementCollection
    final
    List<Coupon> couponList = new ArrayList<>();


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
    final
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

    public Double getDefaultPrice()
    {
        return defaultPrice;
    }

    public Double getDefaultMAP()
    {
        return defaultMAP;
    }

    public List<String> getCategories()
    {
        return categories;
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

    public Double GetMAP(Long date)
    {
        Double map = defaultMAP;
        for(var dr : mapList)
        {
            if(dr.InRange(date))
            {
                map = dr.item;
                break;
            }
        }
        return map;
    }
    public Double GetBasePrice(Long date)
    {
        Double price = defaultPrice;
        for(var dr : pricesList)
        {
            if(dr.InRange(date))
            {
                price = dr.item;
                break;
            }
        }
        return price;
    }
    public Double GetRealPrice(Long date, String couponCode)
    {
        Double price = defaultPrice;
        for(var dr : pricesList)
        {
            if(dr.InRange(date))
            {
                price = dr.item;
                break;
            }
        }
        Double sale = 0.0;
        for(var dr : salesList)
        {
            if(dr.InRange(date))
            {
                sale = dr.item;
                break;
            }
        }
        Double couponValue = 0.0;
        for(var dr : couponList)
        {
            if(dr.InRange(date) && dr.code.equals(couponCode))
            {
                couponValue = dr.sale;
            }
        }
        return price * (1-sale) * (1-couponValue);
    }
    public Boolean validCoupon(Long date, String couponCode)
    {
        for(var dr : couponList)
        {
            if(dr.InRange(date) && dr.code.equals(couponCode))
            {
                return true;
            }
        }
        return false;
    }
    public void Obfuscate()
    {
        this.defaultPrice = null;
        this.defaultMAP = null;
    }


    @Transient
    @JsonProperty("CurrentListPrice")
    Double ListPrice;


    @Transient
    @JsonProperty("CurrentRealPrice")
    Double RealPrice;

    @JsonGetter("CurrentRealPrice")
    public Double JsonGetRealPrice()
    {
        if(defaultPrice == null)
        {
            return null;
        }
        return GetRealPrice(System.currentTimeMillis(),null);
    }
    @JsonGetter("CurrentListPrice")
    public Double JsonGetListPrice()
    {
        if(defaultPrice == null)
        {
            return null;
        }
        return GetBasePrice(System.currentTimeMillis());
    }
}
