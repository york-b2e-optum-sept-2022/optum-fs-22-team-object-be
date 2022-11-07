package org.yorksolutions.teamobjbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.yorksolutions.teamobjbackend.entities.Product;

public class ProductAmountDTO
{
    @JsonProperty
    public Product product;

    public ProductAmountDTO(Product product, Integer amount)
    {
        this.product = product;
        this.amount = amount;
    }

    @JsonProperty
    public Integer amount;
}
