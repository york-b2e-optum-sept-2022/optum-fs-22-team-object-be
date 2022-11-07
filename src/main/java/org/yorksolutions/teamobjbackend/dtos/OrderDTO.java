package org.yorksolutions.teamobjbackend.dtos;

import org.yorksolutions.teamobjbackend.entities.ProductOrder;

import java.util.HashMap;
import java.util.Map;

public class OrderDTO extends RequestDTO
{
    public Map<String,Integer> productsOrdered = new HashMap<>();
    public double total;
    public double date;
    public static OrderDTO fromOrder(ProductOrder o)
    {
        OrderDTO dto = new OrderDTO();
        dto.productsOrdered = o.getProductsOrdered();
        dto.total = o.getTotal();
        dto.date = o.getDate();
        return dto;
    }
}
