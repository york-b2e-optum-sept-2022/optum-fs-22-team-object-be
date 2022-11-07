package org.yorksolutions.teamobjbackend.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;

@RestController
@RequestMapping("/api/product/")
public class ProductController
{

    @PostMapping
    public ProductDTO CreateProduct(@RequestBody ProductDTO productData)
    {
        return productData;
    }
}
