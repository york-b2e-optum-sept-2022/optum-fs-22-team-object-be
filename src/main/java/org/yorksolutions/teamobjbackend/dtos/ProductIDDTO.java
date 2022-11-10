package org.yorksolutions.teamobjbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO that contains a product ID, extended
 * @author Max Feige
 */
public class ProductIDDTO extends RequestDTO
{
    /**
     * ID of the relevant product
     */
    @JsonProperty
    public String productID;
}
