package org.yorksolutions.teamobjbackend.dtos.AccountInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.yorksolutions.teamobjbackend.entities.Product;
import org.yorksolutions.teamobjbackend.entities.ProductOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OrderDTO represents a cart or a previous order tha a user has made
 * @author Max Feige
 */
@SuppressWarnings("unused")
public class OrderDTO
{
    /**
     * Cart data - A list of ProductAmounts (product, number of that product)
     */
    @JsonProperty
    public final List<ProductAmountDTO> productAmounts = new ArrayList<>();

    /**
     * Date order was made, or null for an unmade order
     */
    @JsonProperty
    public Long date;
    /**
     * final price of the order, or null for an unmade order
     */
    @JsonProperty
    public Double price;
    private OrderDTO()
    {}


    /**
     * Creates an OrderDTO from a product order and a map that maps strings to actual products.
     * @param po The ProductOrder to convert
     * @param productMap A map containing all relevant productIDs and the product they correspond to
     * @return a filled out orderDTO;
     */
    public static OrderDTO FromProductOrder(ProductOrder po, Map<String, Product> productMap)
    {
        OrderDTO ndto = new OrderDTO();
        var keys = po.getProductsOrdered().keySet();
        for(var key : keys)
        {
            ndto.productAmounts.add(new ProductAmountDTO(productMap.get(key),po.getProductsOrdered().get(key)));
        }
        ndto.date = po.getDate();
        ndto.price = po.getTotal();
        return ndto;
    }
}
