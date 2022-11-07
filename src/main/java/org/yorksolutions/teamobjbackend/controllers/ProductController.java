package org.yorksolutions.teamobjbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
import org.yorksolutions.teamobjbackend.entities.Product;
import org.yorksolutions.teamobjbackend.services.ProductService;

@RestController
@RequestMapping("/api/product/")
@CrossOrigin
public class ProductController
{
    private ProductService productService;

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
    public void DeleteProduct(@RequestBody ProductDTO productData)
    {
        this.productService.DeleteProduct(productData);
    }

}
