package org.yorksolutions.teamobjbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
import org.yorksolutions.teamobjbackend.dtos.RequestDTO;
import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;
import org.yorksolutions.teamobjbackend.entities.Account;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;
import org.yorksolutions.teamobjbackend.entities.Product;
import org.yorksolutions.teamobjbackend.repositories.AccountRepository;
import org.yorksolutions.teamobjbackend.repositories.ProductRepository;
import org.yorksolutions.teamobjbackend.utils.YorkUtils;

import java.util.List;
import java.util.Optional;


@Service
public class ProductService
{

    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, AccountRepository accountRepository)
    {
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
    }

    public Product CreateProduct(ProductDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = new Product(YorkUtils.GenerateUUID());
        p.update(dto);
        this.productRepository.save(p);
        return p;
    }
    public Product EditProduct(ProductDTO dto)
    {
        verify(dto);
        Product p = GetProduct(dto);
        p.update(dto);
        this.productRepository.save(p);
        return p;
    }
    public void DeleteProduct(ProductDTO dto)
    {
        verify(dto);
        Product p = GetProduct(dto);
        if(p.getDiscontinued())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product already deleted");
        }
        p.setDiscontinued(true);
        this.productRepository.save(p);

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

    private void verify(RequestDTO dto) throws ResponseStatusException
    {
        Optional<Account> acct = accountRepository.findById(dto.userID);
        if(acct.isEmpty() || acct.get().getPermission() == AccountPermission.CUSTOMER)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Account id requesting this operation is either non-existent or has insufficient permissions");
        }
    }
    private Product GetProduct(ProductDTO dto)
    {
        if(dto.productID == null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Product id not given for editing");
        }
        Optional<Product> p = this.productRepository.findById(dto.productID);
        if(p.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Did not find product with given id");
        }
        return p.get();
    }
}
