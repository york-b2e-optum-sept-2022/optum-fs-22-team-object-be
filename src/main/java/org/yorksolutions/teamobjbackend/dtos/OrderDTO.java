package org.yorksolutions.teamobjbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.yorksolutions.teamobjbackend.entities.Product;
import org.yorksolutions.teamobjbackend.entities.ProductOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDTO
{
    @JsonProperty
    public List<ProductAmountDTO> productAmounts = new ArrayList<>();

    @JsonProperty
    public Long date;
    @JsonProperty
    public Double price;
    private OrderDTO()
    {}


    public static OrderDTO FromProductOrder(ProductOrder po, Map<String, Product> productMap)
    {
        OrderDTO ndto = new OrderDTO();
        var keys = po.getProductsOrdered().keySet();
        for(var key : keys)
        {
            ndto.productAmounts.add(new ProductAmountDTO(productMap.get(key),po.getProductsOrdered().get(key)));
        }
        ndto.date = po.getDate();
        ndto.price = ndto.price;
        return ndto;
    }
}
