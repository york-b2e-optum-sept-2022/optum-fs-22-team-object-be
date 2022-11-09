package org.yorksolutions.teamobjbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.dtos.*;
import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;
import org.yorksolutions.teamobjbackend.entities.Account;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;
import org.yorksolutions.teamobjbackend.entities.Product;
import org.yorksolutions.teamobjbackend.repositories.AccountRepository;
import org.yorksolutions.teamobjbackend.repositories.ProductRepository;
import org.yorksolutions.teamobjbackend.utils.YorkUtils;

import java.util.ArrayList;
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
    public Product EditProduct(ProductDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        p.update(dto);
        this.productRepository.save(p);
        return p;
    }
    public void DeleteProduct(ProductDTO dto) throws ResponseStatusException
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

    public void DeleteCoupon(CouponDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Coupon c = new Coupon();
        c.code = dto.code;
        List<Product> relevantProducts = IterableToList(this.productRepository.findAllWithCouponCode(c.code));
        if(relevantProducts.size() == 0)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No products containing that coupon code were found");
        }
        for(Product p : relevantProducts)
        {
            p.getCouponList().removeIf( (coup)->coup.equals(c));
        }

        this.productRepository.saveAll(relevantProducts);
    }
    public void AddCoupon(CouponDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Coupon c = new Coupon();
        c.startDate = dto.startDate;
        c.endDate = dto.endDate;
        c.sale = dto.sale;
        c.code = dto.code;
        if(dto.endDate < dto.startDate)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Date range should have start before end");
        }

        List<Product> relevantProducts = GetProductsFromStringList(dto.productIDs);
        for(Product p : relevantProducts)
        {
            //if any product already has that coupon say "oh no don't do that please"
            if(p.getCouponList().contains(c))
            {
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Coupon code already in use");
            }
            for(Coupon coup : p.getCouponList())
            {
                if(coup.Overlaps(c))
                {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,"Coupon date range conflicts with existing coupon");
                }
            }
            p.getCouponList().add(c);
        }

        this.productRepository.saveAll(relevantProducts);
    }
    public List<DateRanged<Double>> AddMAPRange(DoubleRangedDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        DateRanged<Double> drd = new DateRanged<>();
        drd.startDate = dto.startDate;
        drd.endDate = dto.endDate;
        drd.item = dto.value;
        addIfNoOverlapAndCheckRange(p.getMapList(),drd);
        this.productRepository.save(p);
        return p.getMapList();
    }
    public void DeleteMAPRange(DatedProductDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        findAndRemoveRangeContaining(p.getMapList(),dto.date);
        this.productRepository.save(p);
    }
    public List<DateRanged<Double>> AddPriceRange(DoubleRangedDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        DateRanged<Double> dr = dateRangedFromDTO(dto);
        addIfNoOverlapAndCheckRange(p.getPricesList(),dr);
        this.productRepository.save(p);
        return p.getSalesList();

    }
    public void DeletePriceRange(DatedProductDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        findAndRemoveRangeContaining(p.getPricesList(),dto.date);
        this.productRepository.save(p);
    }
    public List<DateRanged<Double>> AddSaleRange(DoubleRangedDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        DateRanged<Double> dr = dateRangedFromDTO(dto);
        addIfNoOverlapAndCheckRange(p.getSalesList(),dr);
        this.productRepository.save(p);
        return p.getSalesList();
    }
    public void DeleteSaleRange(DatedProductDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        findAndRemoveRangeContaining(p.getSalesList(),dto.date);
        this.productRepository.save(p);
    }
    public void AddCategories(CategoryDTO dto) throws ResponseStatusException
    {
        verify(dto);
        List<Product> products = GetProductsFromStringList(dto.productIDs);
        for(Product p: products)
        {
            if(!p.getCategories().contains(dto.categoryName))
            {
                p.getCategories().add(dto.categoryName);
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Category already exists in product " + p.getProductName());
            }
        }
        this.productRepository.saveAll(products);
    }
    public void DeleteCategories(CategoryDTO dto) throws ResponseStatusException
    {
        verify(dto);
        List<Product> products = GetProductsFromStringList(dto.productIDs);
        for(Product p: products)
        {
            if(p.getCategories().contains(dto.categoryName))
            {
                p.getCategories().remove(dto.categoryName);
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Attempting to remove category that doesn't exist " + p.getProductName());
            }
        }
        this.productRepository.saveAll(products);
    }
    public List<Product> GetProductsInCategory(String category)
    {
        return IterableToList(this.productRepository.findAllByDiscontinuedIsFalseAndCategories(category));
    }
    public List<DateRanged<Double>> GetMAPRanges(ProductIDDTO dto)
    {
        verify(dto);
        Product p = GetProduct(dto);
        return p.getMapList();
    }
    public List<DateRanged<Double>> GetSaleRanges(ProductIDDTO dto)
    {
        verify(dto);
        Product p = GetProduct(dto);
        return p.getSalesList();

    }
    public List<DateRanged<Double>> GetPriceRanges(ProductIDDTO dto)
    {
        verify(dto);
        Product p = GetProduct(dto);
        return p.getPricesList();

    }
    public List<String> GetCategories(ProductIDDTO dto)
    {
        verify(dto);
        Product p = GetProduct(dto);
        return p.getCategories();

    }
    public List<Coupon> GetCoupons(ProductIDDTO dto)
    {
        verify(dto);
        Product p = GetProduct(dto);
        return p.getCouponList();

    }
    public PriceDTO CalculatePrice(String productID, String coupon, Long date)
    {
        if(date == null)
        {
            date = System.currentTimeMillis();
        }

        ProductIDDTO temp = new ProductIDDTO();
        temp.productID = productID;
        Product p = GetProduct(temp);

        if(!p.validCoupon(date,coupon))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid coupon code");
        }
        PriceDTO ret = new PriceDTO();
        ret.basePrice = p.GetBasePrice(date);

        ret.realPrice = p.GetRealPrice(date,coupon);
        ret.map = p.GetMAP(date);

        return ret;

    }

    private void verify(RequestDTO dto) throws ResponseStatusException
    {
        Optional<Account> acct = accountRepository.findById(dto.userID);
        if(acct.isEmpty() || acct.get().getPermission() == AccountPermission.CUSTOMER)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Account id requesting this operation is either non-existent or has insufficient permissions");
        }
    }
    private Product GetProduct(ProductIDDTO dto)
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

    private static <T> List<T> IterableToList(Iterable<T> it)
    {
        ArrayList<T> arrayList = new ArrayList<>();
        it.forEach(arrayList::add);
        return arrayList;
    }
    private List<Product> GetProductsFromStringList(List<String> ids) throws ResponseStatusException
    {
        List<Product> products = IterableToList(this.productRepository.findAllByProductIDIn(ids));
        if(products.size() != ids.size())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Not all ids could be mapped to products");
        }
        return products;
    }
    private <T> void findAndRemoveRangeContaining(List<DateRanged<T>> ls, Long date)
    {
        Integer foundIndex = null;
        for(int i = 0; i < ls.size();i++)
        {
            if(ls.get(i).InRange(date))
            {
                foundIndex = i;
                break;
            }
        }
        if(foundIndex != null)
        {
           ls.remove(foundIndex.intValue());
           return;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find a date range that contains given date");
    }
    private <T> void addIfNoOverlapAndCheckRange(List<DateRanged<T>> ls, DateRanged<T> dr)
    {
        if(dr.endDate < dr.startDate)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Date range should have start before end");
        }
        for(var existingDR : ls)
        {
            if(existingDR.Overlaps(dr))
            {
                throw new ResponseStatusException(HttpStatus.CONFLICT,"This MAP range overlaps an existing one");
            }
        }
        ls.add(dr);
    }

    private void validateDoubleDTO(DoubleRangedDTO dto) throws ResponseStatusException
    {
        if(dto.value == null || dto.startDate == null || dto.endDate == null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Must provide startDate,endDate,and value");
        }
    }
    private DateRanged<Double> dateRangedFromDTO(DoubleRangedDTO dto) throws ResponseStatusException
    {
        validateDoubleDTO(dto);
        DateRanged<Double> dr = new DateRanged<>();
        dr.item = dto.value;
        dr.startDate = dto.startDate;
        dr.endDate = dto.endDate;
        return dr;
    }

    public void Clear()
    {
        this.productRepository.deleteAll();
    }

}
