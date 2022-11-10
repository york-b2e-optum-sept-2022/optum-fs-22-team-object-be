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

/**
 * Endpoints for products
 * /api/product/
 */
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

    /**
     * POST /api/product/create
     * {@link ProductService#CreateProduct(ProductDTO)}
     * @param productData DTO
     * @return Product data
     */
    @PostMapping("create")
    public Product CreateProduct(@RequestBody ProductDTO productData)
    {
        return this.productService.CreateProduct(productData);
    }

    /**
     * PUT /api/product/edit
     * {@link ProductService#EditProduct(ProductDTO)}
     * @param productData DTO
     * @return Product data
     */
    @PutMapping("edit")
    public Product EditProduct(@RequestBody ProductDTO productData)
    {
        return this.productService.EditProduct(productData);
    }

    /**
     * DELETE /api/product/delete
     * {@link ProductService#DeleteProduct(ProductDTO)}
     * @param productData DTO
     */
    @DeleteMapping("delete")
    public void DeleteProduct(@RequestBody ProductDTO productData)
    {
        this.productService.DeleteProduct(productData);
    }

    /**
     * /api/product/create/coupon
     * {@link ProductService#AddCoupon(CouponDTO)}
     * @param dto DTO
     */
    @PostMapping("create/coupon")
    public void AddCoupon(@RequestBody CouponDTO dto)
    {
        this.productService.AddCoupon(dto);
    }

    /**
     * DELETE /api/product/create/coupon
     * {@link ProductService#DeleteCoupon(CouponDTO)}
     * @param dto DTO
     */
    @DeleteMapping("delete/coupon")
    public void DeleteCoupon(@RequestBody CouponDTO dto)
    {
        this.productService.DeleteCoupon(dto);
    }

    /**
     * PUT /api/product/edit/categories
     * {@link ProductService#AddCategories(CategoryDTO)}
     * @param dto DTO
     */
    @PutMapping("edit/categories")
    public void AddCategoriesToProducts(@RequestBody CategoryDTO dto)
    {
        this.productService.AddCategories(dto);

    }

    /**
     * DELETE /api/product/delete/categories
     * {@link ProductService#DeleteCategories(CategoryDTO)}
     * @param dto DTO
     */
    @DeleteMapping("delete/categories")
    public void RemoveCategoriesToProducts(@RequestBody CategoryDTO dto)
    {
        this.productService.DeleteCategories(dto);
    }

    /**
     * GET /api/product/get
     * {@link ProductService#GetProduct(ProductIDDTO)}
     * @param userID user making request (optional)
     * @param productID product they want the details of
     * @return Product details
     */
    @GetMapping(value = "get")
    public Product GetProduct(@RequestParam(value="userID", required = false) String userID,@RequestParam(value="productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.userID = userID;
        dto.productID = productID;
        return this.productService.GetProduct(dto);
    }

    /**
     * GET /api/product/get/all
     * {@link ProductService#GetProducts(String)}
     * @param userID user making request (optional)
     * @return
     */
    @GetMapping(value="get/all")
    public List<Product> GetAllProducts(@RequestParam(value="userID", required = false) String userID)
    {
        return this.productService.GetProducts(userID);
    }

    /**
     * GET /api/product/get/allCategories
     * {@link ProductService#GetProductsInCategory(String)}
     * @param categoryName  name of category
     * @return All products in give ncategory
     */
    @GetMapping("get/allCategories")
    public List<Product> GetProductsInCategory(@RequestParam("categoryName") String categoryName)
    {
        CategoryDTO dto = new CategoryDTO();
        dto.categoryName = categoryName;
        return this.productService.GetProductsInCategory(dto.categoryName);
    }

    /**
     * PUT /api/product/edit/maps
     * {@link ProductService#GetMAPRanges(ProductIDDTO)}
     * @param dto DTO
     * @return list of all MAPs on product
     */
    @PutMapping("edit/maps")
    public List<DateRanged<Double>> AddMAP(DoubleRangedDTO dto)
    {
        return this.productService.AddMAPRange(dto);
    }
    /**
     * PUT /api/product/edit/prices
     * {@link ProductService#GetPriceRanges(ProductIDDTO)}
     * @param dto DTO
     * @return list of all Prices on product
     */
    @PutMapping("edit/prices")
    public List<DateRanged<Double>> AddPrice(DoubleRangedDTO dto)
    {
        return this.productService.AddPriceRange(dto);
    }
    /**
     * PUT /api/product/edit/sales
     * {@link ProductService#GetSaleRanges(ProductIDDTO)}
     * @param dto DTO
     * @return list of all sales on product
     */
    @PutMapping("edit/sales")
    public List<DateRanged<Double>> AddSale(DoubleRangedDTO dto)
    {
        return this.productService.AddSaleRange(dto);
    }

    /**
     * Removes MAP from product
     * DELETE /api/product/delete/map
     * {@link ProductService#DeleteMAPRange(DatedProductDTO)}
     * @param dto DTO
     */
    @DeleteMapping("delete/map")
    public void DeleteMAP(DatedProductDTO dto)
    {
        this.productService.DeleteMAPRange(dto);
    }

    /**
     * Remove Sale range from product
     * DELETE /api/product/delete/sale
     * {@link ProductService#DeleteSaleRange(DatedProductDTO)}
     * @param dto DTO
     */
    @DeleteMapping("delete/sale")
    public void DeleteSale(DatedProductDTO dto)
    {
        this.productService.DeleteSaleRange(dto);
    }

    /**
     * Remove price range from product
     * DELETE /api/product/delete/price
     * {@link ProductService#DeletePriceRange(DatedProductDTO)}
     * @param dto DTO
     */
    @DeleteMapping("delete/price")
    public void DeletePrice(DatedProductDTO dto)
    {
        this.productService.DeletePriceRange(dto);
    }

    /**
     * Get map ranges from product
     * GET /api/product/get/maps
     * {@link ProductService#GetMAPRanges(ProductIDDTO)}
     * @param userID user making request
     * @param productID product to check
     * @return all maps
     */
    @GetMapping("get/maps")
    public List<DateRanged<Double>> GetMAPs(@RequestParam("userID") String userID, @RequestParam("productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.productID = productID;
        dto.userID = userID;
        return this.productService.GetMAPRanges(dto);
    }
    /**
     * Get sales ranges from product
     * GET /api/product/get/sales
     * {@link ProductService#GetSaleRanges(ProductIDDTO)}
     * @param userID user making request
     * @param productID product to check
     * @return all saless
     */
    @GetMapping("get/sales")
    public List<DateRanged<Double>> GetSales(@RequestParam("userID") String userID, @RequestParam("productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.productID = productID;
        dto.userID = userID;
        return this.productService.GetSaleRanges(dto);
    }
    /**
     * Get prices ranges from product
     * GET /api/product/get/prices
     * {@link ProductService#GetPriceRanges(ProductIDDTO)}
     * @param userID user making request
     * @param productID product to check
     * @return all pricess
     */
    @GetMapping("get/prices")
    public List<DateRanged<Double>> GetPrices(@RequestParam("userID") String userID, @RequestParam("productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.productID = productID;
        dto.userID = userID;
        return this.productService.GetPriceRanges(dto);
    }
    /**
     * Get categories from product
     * GET /api/product/get/categories
     * {@link ProductService#GetCategories(ProductIDDTO)}
     * @param productID product to check
     * @return all categories on given product
     */
    @GetMapping("get/categories")
    public List<String> GetProductCategories(@RequestParam("productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.productID = productID;
        return this.productService.GetCategories(dto);
    }

    /**
     * Get all coupons on given product
     * GET /api/product/get/coupons
     * {@link ProductService#GetCoupons(ProductIDDTO)}
     * @param userID user making request
     * @param productID producdt ID
     * @return all coupons
     */
    @GetMapping("get/coupons")
    public List<Coupon> GetCoupons(@RequestParam("userID") String userID, @RequestParam("productID") String productID)
    {
        ProductIDDTO dto = new ProductIDDTO();
        dto.productID = productID;
        dto.userID = userID;
        return this.productService.GetCoupons(dto);
    }

    /**
     * Get price data of a product
     * GET /api/product/get/price
     * {@link ProductService#CalculatePrice(String, String, String, Long)}
     * @param productID product to check
     * @param userID user making request (only required when date not null)
     * @param coupon coupon code
     * @param date date (can be null)
     * @return product prices at given date
     */
    @GetMapping("get/price")
    public PriceDTO GetPrice(@RequestParam("productID") String productID,@RequestParam(value="userID",required = false) String userID,  @RequestParam(value = "coupon",required = false) String coupon, @RequestParam(value="date",required = false) Long date)
    {
        return this.productService.CalculatePrice(productID,userID,coupon,date);
    }

    public void ClearAll()
    {
        this.productService.Clear();
    }
}
