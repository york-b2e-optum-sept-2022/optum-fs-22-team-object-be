package org.yorksolutions.teamobjbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@Entity
public class ProductOrder
{

    @JsonIgnore
    @Id
    String id;


    @JsonProperty
    @ElementCollection
    Map<String,Integer> productsOrdered;



    @JsonProperty
    Double total;
    @JsonProperty
    Long date;

    public Double getTotal()
    {
        return total;
    }

    public ProductOrder()
    {
            productsOrdered = new HashMap<>();
    }
    public ProductOrder(String id)
    {
        this.id = id;
        productsOrdered = new HashMap<>();
    }

    public Map<String,Integer> getProductsOrdered()
    {
        return productsOrdered;
    }

    public void RemoveProduct(Product prod)
    {
        productsOrdered.remove(prod.productID);
    }
    public void SetProduct(Product prod, Integer number)
    {
        productsOrdered.put(prod.productID,number);
    }


    public void setTotal(Double total)
    {
        this.total = total;
    }

    public Long getDate()
    {
        return date;
    }

    public void setDate(Long date)
    {
        this.date = date;
    }



}
