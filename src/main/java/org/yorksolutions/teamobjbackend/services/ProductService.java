package org.yorksolutions.teamobjbackend.services;
import org.yorksolutions.teamobjbackend.dtos.CouponDTO;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
import org.yorksolutions.teamobjbackend.entities.Coupon;
import org.yorksolutions.teamobjbackend.repositories.ProductRepository;

import java.util.List;

public class ProductService {

    private ProductRepository productRepository;
    private CouponDTO couponDTO;
    private ProductDTO productDTO;


    public List<Coupon> getCoupon(Long couponID){
        List<Coupon> Coupon = null;
        return null;
    }
    //       null is placeholder
    //       iterate through list of coupon ids?
    public Coupon addCoupon(CouponDTO couponDTO) {
        //addCoupon() goes into Controller
        Coupon newCoupon = new Coupon();
        return newCoupon;
    }
//        iterate through list of product IDs or Coupon IDs?
//        List of product IDs
//        Percentage off product
//        Start date, end date (date to strings)
//        SUCCESS: UUID for coupon
//        Failure: Permission level



    //TODO:
    public void deleteCoupon(Long couponId){
    //deleteCoupon() goes into Controller
    this.productRepository.deleteById();
//        DELETE (POST) coupon:
//    UUID for coupon
//    need coupon repository?
//        Failure: Not found, invalid permission level


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

    //TODO


    public static boolean isBetween(int value, int min, int max) {
        return ((value > min) && (value < max));
//        if((productQuantity >= 5) && (productQuantity <= 9)) {
//            discount=(500/100);

    }




        //    will need to determine where we will house the quantity
        //    if quantity is 0 to 4, no discount
        //    if quantity is 5-9, 5% discount
        //    if quantity is 10-19, 10% discount
        //    if quantity is 20-24, 15% discount
        //    if quantity is 25+, 20% discount


    }






    /*
    GETS
     */
    //TODO
//    public List<Product> GetProductsInCategory(String category)
//    {
//        return null;
//    }
//    //TODO
//    public List<DateRanged<Double>> GetMAPRanges(String productID)
//    {
//        return null;
//    }
//    //TODO
//    public List<DateRanged<Double>> GetSaleRanges(String productID)
//    {
//        return null;
//    }
//    public List<DateRanged<Double>> GetPriceRanges(String productID)
//    {
//        return null;
//    }
//    //TODO
//    public List<String> GetCategories(String productID)
//    {
//        return null;
//    }
//    public List<Coupon> GetCoupons(String productID)
//    {
//        return null;
//    }





