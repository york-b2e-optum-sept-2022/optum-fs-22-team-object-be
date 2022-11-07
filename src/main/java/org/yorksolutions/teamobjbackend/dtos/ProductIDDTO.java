package org.yorksolutions.teamobjbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductIDDTO extends RequestDTO
{
    @JsonProperty
    public String productID;
}
