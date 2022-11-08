package org.yorksolutions.teamobjbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yorksolutions.teamobjbackend.dtos.*;
import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;
import org.yorksolutions.teamobjbackend.entities.Product;
import org.yorksolutions.teamobjbackend.services.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/product/")
@CrossOrigin
public class ProductController
{
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }

    @PostMapping("create")
    public Product CreateProduct(@RequestBody ProductDTO productData)
    {
        return this.productService.CreateProduct(productData);
    }
    @PutMapping("edit")
    public Product EditProduct(@RequestBody ProductDTO productData)
    {
        return this.productService.EditProduct(productData);
    }
    @DeleteMapping("delete")
    public ResponseEntity<String> DeleteProduct(@RequestBody ProductDTO productData)
    {
        this.productService.DeleteProduct(productData);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @PostMapping("create/coupon")
    public ResponseEntity<String> AddCoupon(@RequestBody CouponDTO dto)
    {
        this.productService.AddCoupon(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @DeleteMapping("delete/coupon")
    public ResponseEntity<String> DeleteCoupon(@RequestBody CouponDTO dto)
    {
        this.productService.DeleteCoupon(dto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @PutMapping("edit/categories")
    public ResponseEntity<String> AddCategoriesToProducts(@RequestBody CategoryDTO dto)
    {
        this.productService.AddCategories(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }
    @DeleteMapping("delete/categories")
    public ResponseEntity<String> RemoveCategoriesToProducts(@RequestBody CategoryDTO dto)
    {
        this.productService.DeleteCategories(dto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    @GetMapping("get/allCategories")
    public List<Product> GetProductsInCategory(@RequestParam("categoryName") String categoryName)
    {
        CategoryDTO dto = new CategoryDTO();
        dto.categoryName = categoryName;
        return this.productService.GetProductsInCategory(dto.categoryName);
    }
    @PutMapping("edit/maps")
    public List<DateRanged<Double>> AddMAP(DoubleRangedDTO dto)
    {
        return this.productService.AddMAPRange(dto);
    }
    @PutMapping("edit/prices")
    public List<DateRanged<Double>> AddPrice(DoubleRangedDTO dto)
    {
        return this.productService.AddPriceRange(dto);
    }
    @PutMapping("edit/sales")
    public List<DateRanged<Double>> AddSale(DoubleRangedDTO dto)
    {
        return this.productService.AddSaleRange(dto);
    }
    @DeleteMapping("delete/map")
    public void DeleteMAP(DatedProductDTO dto)
    {
        this.productService.DeleteMAPRange(dto);
    }
    @DeleteMapping("delete/sale")
    public void DeleteSale(DatedProductDTO dto)
    {
        this.productService.DeleteSaleRange(dto);
    }
    @DeleteMapping("delete/price")
    public void DeletePrice(DatedProductDTO dto)
    {
        this.productService.DeletePriceRange(dto);
    }
    @GetMapping("get/maps")
    public List<DateRanged<Double>> GetMAPs(@RequestParam("userID") String userID, @RequestParam("productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.productID = productID;
        dto.userID = userID;
        return this.productService.GetMAPRanges(dto);
    }
    @GetMapping("get/sales")
    public List<DateRanged<Double>> GetSales(@RequestParam("userID") String userID, @RequestParam("productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.productID = productID;
        dto.userID = userID;
        return this.productService.GetSaleRanges(dto);
    }
    @GetMapping("get/prices")
    public List<DateRanged<Double>> GetPrices(@RequestParam("userID") String userID, @RequestParam("productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.productID = productID;
        dto.userID = userID;
        return this.productService.GetPriceRanges(dto);
    }
    @GetMapping("get/categories")
    public List<String> GetProductCategories(@RequestParam("productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.productID = productID;
        return this.productService.GetCategories(dto);
    }
    @GetMapping("get/coupons")
    public List<Coupon> GetCoupons(@RequestParam("productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.productID = productID;
        return this.productService.GetCoupons(dto);
    }

}
