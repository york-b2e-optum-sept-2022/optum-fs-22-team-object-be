package org.yorksolutions.teamobjbackend.dtos;


/**
 * DTO parent class containing start/end dates
 *
 * @author Max Feige
 */
@SuppressWarnings("unused")
public class RangedDTO extends ProductIDDTO
{
    /**
     * Start date of the range in milliseconds since epoch
     */
    public Long startDate;
    /**
     * End date of the range in milliseconds since epoch
     */
    public Long endDate;
}
