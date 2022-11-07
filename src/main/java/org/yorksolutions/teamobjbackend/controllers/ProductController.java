package org.yorksolutions.teamobjbackend.controllers;

import org.springframework.web.bind.annotation.*;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;

@RestController
@RequestMapping("/api/product/")
@CrossOrigin
public class ProductController
{

    @PostMapping
    public ProductDTO CreateProduct(@RequestBody ProductDTO productData)
    {
        return productData;
    }
}
