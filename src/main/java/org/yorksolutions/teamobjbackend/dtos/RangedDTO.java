package org.yorksolutions.teamobjbackend.dtos;


/**
 * DTO parent class containing start/end dates
 *
 * @author Max Feige
 */
@SuppressWarnings("unused")
public class RangedDTO extends ProductIDDTO
{
    public Long startDate;
    public Long endDate;
}
