package org.yorksolutions.teamobjbackend.dtos;

import java.util.List;

/**
 * DTO representing the adding or removing of a coupon to some products
 */
@SuppressWarnings("unused")
public class CouponDTO extends RequestDTO
{
    /**
     * String representing the coupon code
     */
    public String code;
    /**
     * Products to add/remove the coupon from
     */
    public List<String> productIDs;
    /**
     * Date the coupon takes effect
     * Can be null when DTO is used for removing coupons from products
     */
    public Long startDate;
    /**
     * Date the coupon no longer has effect
     * Can be null when DTO is used for removing coupons from products
     */
    public Long endDate;
    /**
     * The percent off the coupon represents
     * Can be null when DTO is used for removing coupons from products
     */
    public Double sale;
    
}
