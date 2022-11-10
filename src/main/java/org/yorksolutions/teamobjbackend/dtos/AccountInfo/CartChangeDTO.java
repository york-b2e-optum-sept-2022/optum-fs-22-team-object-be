package org.yorksolutions.teamobjbackend.dtos.AccountInfo;

import org.yorksolutions.teamobjbackend.dtos.RequestDTO;

/**
 * DTO to add or remove items from a cart
 */
@SuppressWarnings("unused")
public class CartChangeDTO extends RequestDTO
{
    /**
     * ID of the product to add/remove
     */
    public String productID;
    /**
     * Amount of product to remain in cart.  Set to 0 to remove from cart.
     */
    public Integer number;
}
