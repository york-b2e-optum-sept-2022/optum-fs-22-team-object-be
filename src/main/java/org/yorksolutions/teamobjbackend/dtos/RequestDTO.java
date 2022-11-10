package org.yorksolutions.teamobjbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RequestDTO is a generic parent class for all requests.  It contains a userID used to validate authorization
 *
 * @author Max Feige
 *
 */
public class RequestDTO
{
    @JsonProperty
    public String userID;
}
