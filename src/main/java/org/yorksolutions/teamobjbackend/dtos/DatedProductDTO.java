package org.yorksolutions.teamobjbackend.dtos;


/**
 * DTO used to remove an item that represents a date range in a corresponding product
 * @author Max Feige
 *
 */
@SuppressWarnings("unused")
public class DatedProductDTO extends ProductIDDTO
{
    /**
     * A date in the range of the object we want to remove
     */
    public Long date;
}
