package org.yorksolutions.teamobjbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.dtos.*;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.ProductAmountDTO;
import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;
import org.yorksolutions.teamobjbackend.entities.Account;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;
import org.yorksolutions.teamobjbackend.entities.Product;
import org.yorksolutions.teamobjbackend.repositories.AccountRepository;
import org.yorksolutions.teamobjbackend.repositories.ProductRepository;
import org.yorksolutions.teamobjbackend.utils.YorkUtils;

import java.util.*;

/**
 * Service used to manage product and product related information
 * All DTOs contain a userID field to authorize
 * @author Max Feige
 */
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

    /**
     * Create a product
     * @param dto DTO containing all product data (productID ignored)
     * @return Product - all product details of newly created product
     * @throws ResponseStatusException - FORBIDDEN on bad userID
     */
    public Product CreateProduct(ProductDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = new Product(YorkUtils.GenerateUUID());
        p.update(dto);
        this.productRepository.save(p);
        return p;
    }

    /**
     * Edit a product
     * @param dto DTO containing all product data to be updated - fields set to null ignored.  Must provide productID
     * @return returns the product with the new information
     * @throws ResponseStatusException NOT_FOUND on unknown productID BAD_REQUEST on invalid productID, FORBIDDEN on bad userID
     */
    public Product EditProduct(ProductDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        p.update(dto);
        this.productRepository.save(p);
        return p;
    }

    /**
     * Deletes (i.e. sets the discontinued status to true) a product
     * @param dto DTO containing requester and product ID to be deleted
     * @throws ResponseStatusException NOT_FOUND on unknown productID BAD_REQUEST on invalid productID, FORBIDDEN on bad userID, BAD_REQUEST on deleting already deleted product
     */
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

    /**
     * Delets a coupon by its code on a list of products
     * @param dto DTO containing the coupon code to be deleted, and list of productIDs to delete from
     * @throws ResponseStatusException NOT_FOUND on any productID being non-existent, FORBIDDEN on bad userID
     */
    public void DeleteCoupon(CouponDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Coupon c = new Coupon();
        c.code = dto.code;
        List<Product> relevantProducts = IterableToList(this.productRepository.findAllWithCouponCode(c.code));
        //remove products not given to us in list
        relevantProducts.removeIf( (p) -> !dto.productIDs.contains(p.getProductID()));
        if(relevantProducts.size() == 0)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No products containing that coupon code were found");
        }
        for(Product p : relevantProducts)
        {
            p.getCouponList().removeIf( (coup)->coup.code.equals(dto.code));
        }

        this.productRepository.saveAll(relevantProducts);
    }

    /**
     * Adds a coupon to a list of products
     * @param dto DTO containing coupon information
     * @throws ResponseStatusException ResponseStatusException NOT_FOUND on any productID being non-existent, FORBIDDEN on bad userID, CONFLICT on date range overlap or coupon code in use on any given product
     */
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

    /**
     * Adds a map range to a products
     * @param dto a DTO containing the map, start date, end date, and productID
     * @return All map ranges
     * @throws ResponseStatusException FORBIDDEN on bad userID, NOT_FOUND on invalid productID
     */
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

    /**
     * Deletes a map range from a product
     * @param dto DTO containing the a date in the range of the MapRange you want to remove
     * @throws ResponseStatusException FORBIDDEN on bad userID, NOT_FOUND on invalid product ID
     */
    public void DeleteMAPRange(DatedProductDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        findAndRemoveRangeContaining(p.getMapList(),dto.date);
        this.productRepository.save(p);
    }
    /**
     * Adds a price range to a product
     * @param dto a DTO containing the price, start date, end date, and productID
     * @return All price ranges
     * @throws ResponseStatusException FORBIDDEN on bad userID, NOT_FOUND on invalid productID
     */
    public List<DateRanged<Double>> AddPriceRange(DoubleRangedDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        DateRanged<Double> dr = dateRangedFromDTO(dto);
        addIfNoOverlapAndCheckRange(p.getPricesList(),dr);
        this.productRepository.save(p);
        return p.getPricesList();

    }

    /**
     * Deletes a price rrange on a product
     * @param dto containing the date of the range you want removed
     * @throws ResponseStatusException FORBIDDEN on bad userID, NOT_FOUND on invalid productID
     */
    public void DeletePriceRange(DatedProductDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        findAndRemoveRangeContaining(p.getPricesList(),dto.date);
        this.productRepository.save(p);
    }
    /**
     * Adds a sale range to a products
     * @param dto a DTO containing the sale, start date, end date, and productID
     * @return All sale ranges
     * @throws ResponseStatusException FORBIDDEN on bad userID, NOT_FOUND on invalid productID
     */
    public List<DateRanged<Double>> AddSaleRange(DoubleRangedDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        DateRanged<Double> dr = dateRangedFromDTO(dto);
        addIfNoOverlapAndCheckRange(p.getSalesList(),dr);
        this.productRepository.save(p);
        return p.getSalesList();
    }
    /**
     * Deletes a sale range on a product
     * @param dto containing the date of the range you want removed
     * @throws ResponseStatusException FORBIDDEN on bad userID, NOT_FOUND on invalid productID
     */
    public void DeleteSaleRange(DatedProductDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        findAndRemoveRangeContaining(p.getSalesList(),dto.date);
        this.productRepository.save(p);
    }

    /**
     * Adds a category to a list of products
     * @param dto DTO containnig category name and list of products to add said category to
     * @throws ResponseStatusException FORBIDDEN on bad userID, CONFLICT on already existing product, NOT_FOUND on some product IDs being non-existent
     */
    public void AddCategories(CategoryDTO dto) throws ResponseStatusException
    {
        verify(dto);
        List<Product> products = GetProductsFromStringList(dto.productIDs);
        if(products.size() != dto.productIDs.size())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not all product IDs could be found");
        }
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

    /**
     * Delete categories from a product list
     * @param dto category name and list of product IDs to remove that category from
     * @throws ResponseStatusException FORBIDDEN on bad user ID, NOT_FOUND on any prodcut IDs being not found, CONFLICT on productID not having the category you want to remove
     */
    public void DeleteCategories(CategoryDTO dto) throws ResponseStatusException
    {
        verify(dto);
        List<Product> products = GetProductsFromStringList(dto.productIDs);
        if(products.size() != dto.productIDs.size())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not all product IDs could be found");
        }
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

    /**
     * Gets all categories
     * @return list of all categories (no duplicates
     */
    public List<String> GetAllCategories()
    {
        return IterableToList(this.productRepository.getAllCategories());
    }
    /**
     * Gets all products in a category
     * @param category Category name
     * @return List of products in give ncategory (could be empty)
     */
    public List<Product> GetProductsInCategory(String category)
    {
        return IterableToList(this.productRepository.findAllByDiscontinuedIsFalseAndCategories(category));
    }

    /**
     * Gets all map ranges of a product
     * @param dto DTO containing the productID you want the ranges of
     * @return List of map ranges
     * @throws ResponseStatusException FORBIDDEN on bad userID, NOT_FOUND on invalid productID
     */
    public List<DateRanged<Double>> GetMAPRanges(ProductIDDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        return p.getMapList();
    }
    /**
     * Gets all sale ranges of a product
     * @param dto DTO containing the productID you want the ranges of
     * @return List sale map ranges
     * @throws ResponseStatusException FORBIDDEN on bad userID, NOT_FOUND on invalid productID
     */
    public List<DateRanged<Double>> GetSaleRanges(ProductIDDTO dto)throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        return p.getSalesList();

    }
    /**
     * Gets all price ranges of a product
     * @param dto DTO containing the productID you want the ranges of
     * @return List of price ranges
     * @throws ResponseStatusException FORBIDDEN on bad userID, NOT_FOUND on invalid productID
     */
    public List<DateRanged<Double>> GetPriceRanges(ProductIDDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        return p.getPricesList();

    }

    /**
     * Gets all categories of a given product
     * @param dto DTO containing the product ID you want the categories of
     * @return returns all categories of given product
     * @throws ResponseStatusException NOT_FOUND on invalid productID
     */
    public List<String> GetCategories(ProductIDDTO dto)
    {
        Product p = GetProduct(dto);
        return p.getCategories();

    }

    /**
     * Gets all coupons that a product has
     * @param dto DTO containing the product ID you want the coupons for
     * @return all coupons for given product
     * @throws ResponseStatusException NOT_FOUND on invalid productID
     */
    public List<Coupon> GetCoupons(ProductIDDTO dto) throws ResponseStatusException
    {
        verify(dto);
        Product p = GetProduct(dto);
        return p.getCouponList();

    }

    /**
     * Calculates all relevant prices
     * @param productID productID to check
     * @param userID userID that should be shopkeeper or admin if date is given
     * @param coupon coupon code or null
     * @param date date or null
     * @return Three Prices (MAP, ListedPrice,Real Price) as a DTO
     * @throws ResponseStatusException NOT_FOUND on invalid productID or coupon code
     */
    public PriceDTO CalculatePrice(String productID, String userID, String coupon, Long date) throws ResponseStatusException
    {
        if(date == null)
        {
            date = System.currentTimeMillis();
        }
        //check if they want a date taht is not right now
        else
        {
            RequestDTO dto = new RequestDTO();
            dto.userID = userID;
            verify(dto);
        }

        ProductIDDTO temp = new ProductIDDTO();
        temp.productID = productID;
        Product p = GetProduct(temp);

        if(coupon != null && !p.validCoupon(date,coupon))
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
        if(dto.userID == null)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Must provide a userID");
        }
        Optional<Account> acct = accountRepository.findById(dto.userID);
        if(acct.isEmpty() || acct.get().getPermission() == AccountPermission.CUSTOMER)
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Account id requesting this operation is either non-existent or has insufficient permissions");
        }
    }

    /**
     * Gets all products
     * @param userID id of user (optional), obfuscates if insufficient level
     * @return list of products
     */
    public List<Product> GetProducts (String userID)
    {
        List<Product> products = IterableToList(this.productRepository.findAllByDiscontinuedIsFalse());
        boolean obfs = true;
        if(userID != null)
        {
            Optional<Account> acc = this.accountRepository.findById(userID);
            if(acc.isPresent())
            {
                Account account = acc.get();
                if(account.getPermission() == AccountPermission.ADMIN || account.getPermission() == AccountPermission.SHOPKEEPER)
                {
                    obfs = false;
                }
            }
        }
        if(obfs)
        {
            for(Product p : products)
            {
                p.Obfuscate();;
            }
        }
        return products;
    }

    /**
     * Gets (potentially obfuscated) product details
     * @param dto DTO containing product ID, and optionally a userID of high permission value
     * @return Product details of the requested product
     * @throws ResponseStatusException NOT_FOUND on invalid product ID
     */
    public Product GetProduct(ProductIDDTO dto) throws ResponseStatusException
    {
        if(dto.productID == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Product id not given for editing");
        }
        Optional<Product> p = this.productRepository.findById(dto.productID);
        if(p.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Did not find product with given id");
        }
        Product prod = p.get();
        //If a customer is the one asking for  this product we obfuscate it, removing sensitive price details
        if(dto.userID != null)
        {
            Optional<Account> oacc = this.accountRepository.findById(dto.userID);
            if (oacc.isEmpty() || oacc.get().getPermission() == AccountPermission.CUSTOMER)
            {
                prod.Obfuscate();
            }
        }
        else
        {
            prod.Obfuscate();
        }
        return prod;
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

    /**
     * Calculates quantities at cost discount
     * @param pdto the product w/ amount
     * @param currentPrice the current calculated price
     * @return the new price with QAC discount
     *
     * @author jonthoj
     */
    public static double updateDefaultPrice(ProductAmountDTO pdto,Double currentPrice) {
        Long lowDiscountQuantity = 5L;
        Long mediumDiscountQuantity = 10L;
        Long maxDiscountQuantity = 20L;

        Double lowSale = 0.05;
        Double mediumSale = 0.10;
        Double highSale = 0.20;


        //Will need to add a quantity property to ProductDTO and Entity
        // Add Get method for getting the current quantity
        if (pdto.amount >= lowDiscountQuantity) {
//            double lowDiscountPrice = pdto.defaultPrice - (pdto.defaultPrice * 0.05);
            return currentPrice * (1.0-lowSale);
            // returning new lowDiscountPrice, which is 5% off.
            // Use the setter to updateDefault price ->  public void update(ProductDTO pdto)
            // Pass lowDiscountPrice as dpto.defaultPrice so it becomes this.defaultPrice;

            // return defaultPrice (as lowDiscountPrice)
        }
        if (pdto.amount >= mediumDiscountQuantity) {
            return currentPrice * (1.0-mediumSale);
//            double mediumDiscountPrice = pdto.defaultPrice - (pdto.defaultPrice * 0.10);
            // returning new lowDiscountPrice, which is 10% off.
            // Use the setter to updateDefault price ->  public void update(ProductDTO pdto)
            // Pass lowDiscountPrice as dpto.defaultPrice so it becomes this.defaultPrice;

            // return defaultPrice (as mediumDiscountPrice)
        }
        if (pdto.amount >= maxDiscountQuantity) {
            return currentPrice * (1.0 - highSale);
//            double maxDiscountPrice = pdto.defaultPrice - (pdto.defaultPrice * 0.20);
            // returning new lowDiscountPrice, which is 20% off.
            // Use the setter to updateDefault price ->  public void update(ProductDTO pdto)
            // Pass lowDiscountPrice as dpto.defaultPrice so it becomes this.defaultPrice;

            // return defaultPrice (as maxDiscountPrice)
        }

        return currentPrice;



    }

}
