package org.yorksolutions.teamobjbackend.dtos.AccountInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.yorksolutions.teamobjbackend.entities.Product;

/**
 * DTO representing a product and an amount of tthat product
 *
 * @author Max Feige
 */
@SuppressWarnings("unused")
public class ProductAmountDTO
{
    /**
     * The Product this DTO represents
     */
    @JsonProperty
    public Product product;

    public ProductAmountDTO(Product product, Integer amount)
    {
        this.product = product;
        this.amount = amount;
    }

    /**
     * The amount of product contained
     */
    @JsonProperty
    public Integer amount;
}
