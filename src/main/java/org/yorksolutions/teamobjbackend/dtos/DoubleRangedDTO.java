package org.yorksolutions.teamobjbackend.dtos;


/**
 * A RangedDTO that contains a double - used for modifying MAP lists, price lists, and sale lists
 * @author Max Feige
 *
 */
@SuppressWarnings("unused")
public class DoubleRangedDTO extends RangedDTO
{
    /**
     * The value of whatever we are modifying.  For a price list this would represent the price, while for a sale list this would represent the percent off
     */
    public Double value;
}
