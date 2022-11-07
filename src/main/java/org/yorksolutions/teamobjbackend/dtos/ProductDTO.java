package org.yorksolutions.teamobjbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ProductDTO extends ProductIDDTO
{
    @JsonProperty
    public String productName;
    @JsonProperty
    public String description;
    @JsonProperty
    public List<String> images;
    @JsonProperty
    public Long startDate;
    @JsonProperty
    public Double defaultPrice;
    @JsonProperty
    public Double defaultMAP;
}
