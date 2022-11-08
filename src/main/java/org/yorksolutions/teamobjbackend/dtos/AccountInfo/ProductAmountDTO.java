package org.yorksolutions.teamobjbackend.dtos.AccountInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.yorksolutions.teamobjbackend.entities.Product;

@SuppressWarnings("unused")
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
