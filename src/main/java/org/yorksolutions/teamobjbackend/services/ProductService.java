package org.yorksolutions.teamobjbackend.services;

import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;
import org.yorksolutions.teamobjbackend.entities.Product;
import org.yorksolutions.teamobjbackend.repositories.ProductRepository;

import java.util.List;

public class ProductService
{

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    //TODO:
    public void DeleteCoupon(String couponID)
    {

    }
    //TODO
    public void AddCoupon(Double salePercentage, List<String> productIDs, Long startDate, Long endDate)
    {

    }

    //TODO:
    public void AddMAPRange(String productID, Double MAP, Long startDate, Long endDate)
    {

    }
    //TODO
    public void DeleteMAPRange(String ProductID, Long date)
    {

    }
    //TODO:
    public void AddPriceRange(String productID, Double price, Long startDate, Long endDate)
    {

    }
    //TODO
    public void DeletePriceRange(String ProductID, Long date)
    {

    }
    //TODO:
    public void AddSaleRange(String productID, Double salePercent, Long startDate, Long endDate)
    {

    }
    //TODO
    public void DeleteSaleRange(String ProductID, Long date)
    {

    }
    //TODO:
    public void AddCategories(String categoryName, List<String> productIDs)
    {

    }
    //TODO
    public void DeleteCategories(String categoryName, List<String> productIDs)
    {

    }



    /*
    GETS
     */
    //TODO
    public List<Product> GetProductsInCategory(String category)
    {
        return null;
    }
    //TODO
    public List<DateRanged<Double>> GetMAPRanges(String productID)
    {
        return null;
    }
    //TODO
    public List<DateRanged<Double>> GetSaleRanges(String productID)
    {
        return null;
    }
    public List<DateRanged<Double>> GetPriceRanges(String productID)
    {
        return null;
    }
    //TODO
    public List<String> GetCategories(String productID)
    {
        return null;
    }
    public List<Coupon> GetCoupons(String productID)
    {
        return null;
    }


}
