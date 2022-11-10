package org.yorksolutions.teamobjbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO for creating products
 * @author Max Feige
 */
public class ProductDTO extends ProductIDDTO
{
    /**
     * Name of the new product
     */
    @JsonProperty
    public String productName;
    /**
     * Description of the product
     */
    @JsonProperty
    public String description;
    /**
     * List of images pertaining to the product
     */
    @JsonProperty
    public List<String> images;
    /**
     * The date the product goes on sale
     */
    @JsonProperty
    public Long startDate;
    /**
     * The default price of the product
     */
    @JsonProperty
    public Double defaultPrice;
    /**
     * The default MAP of the product
     */
    @JsonProperty
    public Double defaultMAP;
}
