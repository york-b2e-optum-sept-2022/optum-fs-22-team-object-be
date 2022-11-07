package org.yorksolutions.teamobjbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.ElementCollection;
import java.util.List;

public class ProductDTO extends RequestDTO
{
    @JsonProperty
    public String productName;
    @JsonProperty
    public String description;
    @JsonProperty
    public List<String> images;
    @JsonProperty
    public Boolean discontinued;
    @JsonProperty
    public Long startDate;
    @JsonProperty
    public Double defaultPrice;
    @JsonProperty
    public Double defaultMAP;
}
