package org.yorksolutions.teamobjbackend.dtos;

/**
 * DTO containing all price data of a product.
 * This is sent from server -> client, unlike others
 * @author Max Feige
 */
public class PriceDTO
{
    /**
        The MAP
     */
    public Double map;
    /**
     * Price of a product ignoring coupons/sales
     */
    public Double basePrice;
    /**
     * Price of a product including coupons/sales
     */
    public Double realPrice;
}
