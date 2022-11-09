package org.yorksolutions.teamobjbackend.controllers;
import org.springframework.web.bind.annotation.*;
import org.yorksolutions.teamobjbackend.dtos.CouponDTO;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
import org.yorksolutions.teamobjbackend.entities.Coupon;
import org.yorksolutions.teamobjbackend.services.ProductService;

@RestController
@RequestMapping("/api/product/")
@CrossOrigin

public class ProductController {
    ProductService productService;
    public ProductController(ProductService productService){
        this.productService = productService;
    }


    @PostMapping
    public ProductDTO CreateProduct(@RequestBody ProductDTO productData)
    {
        return productData;
    }

    @GetMapping
    public void getCoupon(@RequestParam Long couponID){
        //       iterate through list of coupon ids?

    }

    @PostMapping("addcoupon")
    public Coupon addCoupon(@RequestBody CouponDTO couponDTO){
        return this.productService.addCoupon(couponDTO);
    }

    @PutMapping ("quantityatcost")
    public void quantityAtCost() {
    //placeholder for quanitity at cost

    }


    }







