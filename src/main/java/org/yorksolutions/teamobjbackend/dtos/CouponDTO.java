package org.yorksolutions.teamobjbackend.dtos;

import org.yorksolutions.teamobjbackend.embeddables.DateRanged;

import java.util.List;

@SuppressWarnings("unused")
public class CouponDTO extends RequestDTO
{
    public String code;
    public List<String> productIDs;
    public Long startDate;
    public Long endDate;
    public Double sale;
}
