package org.yorksolutions.teamobjbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yorksolutions.teamobjbackend.dtos.CategoryDTO;
import org.yorksolutions.teamobjbackend.dtos.CouponDTO;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
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
    @GetMapping("get/categories")
    public List<Product> GetProductsInCategory(@RequestParam("categoryName") String categoryName)
    {
        CategoryDTO dto = new CategoryDTO();
        dto.categoryName = categoryName;
        return this.productService.GetProductsInCategory(dto.categoryName);
    }

}
