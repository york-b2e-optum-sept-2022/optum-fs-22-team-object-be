package org.yorksolutions.teamobjbackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.controllers.AccountController;
import org.yorksolutions.teamobjbackend.controllers.ProductController;
import org.yorksolutions.teamobjbackend.dtos.*;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.AccountDTO;
import org.yorksolutions.teamobjbackend.embeddables.DateRanged;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class BackendProductTests
{
    private final AccountController accountController;
    private final ProductController productController;

    @Autowired
    public BackendProductTests(AccountController accountController, ProductController productController)
    {
        this.accountController = accountController;
        this.productController = productController;
    }


    @Test
    public void TestCreateProduct() throws Exception
    {
        this.accountController.ClearAllExceptAdmin();
        this.productController.ClearAll();


        String adminID = login("admin", "admin");
        String customerID1 = createUser(null, "customer", "1234", null);
        String shopkeeperID1 = createUser(adminID, "shopkeeper", "1234", AccountPermission.SHOPKEEPER);

        createProduct(shopkeeperID1, "s1", "s1desc", 1000L);
        createProduct(adminID, "a1", "a1desc", 2000L);

        assert ResponseFailureCheck(() ->
        {
            createProduct(customerID1, "c1", "c1desc", 2000L);
        }, HttpStatus.FORBIDDEN) : "Only shopkeepers or admins can create products";

    }

    @Test
    public void TestUpdateProduct() throws Exception
    {
        this.accountController.ClearAllExceptAdmin();
        this.productController.ClearAll();
        String adminID = login("admin", "admin");
        String customerID1 = createUser(null, "customer", "1234", null);
        String shopkeeperID1 = createUser(adminID, "shopkeeper", "1234", AccountPermission.SHOPKEEPER);

        String p1 = createProduct(shopkeeperID1, "s1", "s1desc", 1000L);
        String p2 = createProduct(adminID, "a1", "a1desc", 2000L);

        editProduct(shopkeeperID1, p1, "s1New2", "s1DescNew", 3000L);


        assert ResponseFailureCheck(() ->
        {
            editProduct(adminID, "abc", "s1New2Fail", "s1DescNewFail", 10000L);
        }, HttpStatus.NOT_FOUND) : "Cannot edit product that doesn't exist";
        assert ResponseFailureCheck(() ->
        {
            editProduct(customerID1, p1, "s1New2Fail", "s1DescNewFail", 10000L);
        }, HttpStatus.FORBIDDEN) : "Only shopkeepers or admins can Edit products";

    }

    @Test
    public void TestDeleteProduct() throws Exception
    {
        this.accountController.ClearAllExceptAdmin();
        this.productController.ClearAll();
        String adminID = login("admin", "admin");
        String customerID1 = createUser(null, "customer", "1234", null);
        String shopkeeperID1 = createUser(adminID, "shopkeeper", "1234", AccountPermission.SHOPKEEPER);

        String p1 = createProduct(shopkeeperID1, "s1", "s1desc", 1000L);
        String p2 = createProduct(adminID, "a1", "a1desc", 2000L);

        deleteProduct(shopkeeperID1, p1);

        assert ResponseFailureCheck(() ->
        {
            deleteProduct(shopkeeperID1, "abc");
        }, HttpStatus.NOT_FOUND) : "Cannot delete non-existent product";
        assert ResponseFailureCheck(() ->
        {
            deleteProduct(shopkeeperID1, p1);
        }, HttpStatus.BAD_REQUEST) : "Cannot delete already deleted product";
        assert ResponseFailureCheck(() ->
        {
            deleteProduct(customerID1, p2);

        }, HttpStatus.FORBIDDEN) : "Only shopkeepers or admins can delete products";

    }

    @Test
    public void testCoupons() throws Exception
    {
        this.accountController.ClearAllExceptAdmin();
        this.productController.ClearAll();
        String adminID = login("admin", "admin");
        String customerID1 = createUser(null, "customer", "1234", null);
        String shopkeeperID1 = createUser(adminID, "shopkeeper", "1234", AccountPermission.SHOPKEEPER);

        String p1 = createProduct(shopkeeperID1, "s1", "s1desc", 1000L);
        String p2 = createProduct(adminID, "a1", "a1desc", 2000L);

        addCoupon(adminID, Stream.of(p1, p2), "coup1", 100L, 500L);

        addCoupon(adminID, Stream.of(p2), "coup2", 501L, 600L);


        //Cannot add coupon code of existing
        assert ResponseFailureCheck(() ->
        {
            addCoupon(adminID, Stream.of(p1, p2), "coup1", 2000L, 2500L);
        }, HttpStatus.CONFLICT) : "Cannot add already existing coupon code";

        //Checking coupon overlap , all should fail
        assert ResponseFailureCheck(() ->
        {
            addCoupon(adminID, Stream.of(p1, p2), "c2", 100L, 500L);
        }, HttpStatus.CONFLICT) : "Cannot overlap coupons";

        assert ResponseFailureCheck(() ->
        {
            addCoupon(adminID, Stream.of(p1, p2), "c2", 90L, 110L);
        }, HttpStatus.CONFLICT) : "Cannot overlap coupons";

        assert ResponseFailureCheck(() ->
        {
            addCoupon(adminID, Stream.of(p1, p2), "c2", 490L, 510L);
        }, HttpStatus.CONFLICT) : "Cannot overlap coupons";

        assert ResponseFailureCheck(() ->
        {
            addCoupon(adminID, Stream.of(p1, p2), "c2", 110L, 490L);
        }, HttpStatus.CONFLICT) : "Cannot overlap coupons";


        assert ResponseFailureCheck(() ->
        {
            addCoupon(adminID, Stream.of(p1, p2, "1234", "1", "3", "2"), "c2", 100L, 500L);
        }, HttpStatus.NOT_FOUND) : "Should fail if any product IDs were not found";
        assert ResponseFailureCheck(() ->
        {
            addCoupon(customerID1, Stream.of(p1, p2), "coup3", 10000L, 50000L);
        }, HttpStatus.FORBIDDEN) : "Customers shouldn't be able to create coupons";


        deleteCoupon(adminID, "coup1");

        assert ResponseFailureCheck(() ->
        {
            deleteCoupon(customerID1, "coup2");
        }, HttpStatus.FORBIDDEN) : "Customers shouldn't be able to delete coupons";
        assert ResponseFailureCheck(() ->
        {
            deleteCoupon(adminID, "coupUnknown");
        }, HttpStatus.NOT_FOUND) : "Deletion should fail on unknown coupon";

    }

    @Test
    public void testCategories() throws Exception
    {
        this.accountController.ClearAllExceptAdmin();
        this.productController.ClearAll();
        String adminID = login("admin", "admin");
        String customerID1 = createUser(null, "customer", "1234", null);
        String shopkeeperID1 = createUser(adminID, "shopkeeper", "1234", AccountPermission.SHOPKEEPER);

        String p1 = createProduct(shopkeeperID1, "s1", "s1desc", 1000L);
        String p2 = createProduct(adminID, "a1", "a1desc", 2000L);

        this.addCategory(adminID, "Category1", Stream.of(p1));
        this.addCategory(adminID, "Category2", Stream.of(p2));
        this.addCategory(adminID, "CategoryAll", Stream.of(p1, p2));
        this.addCategory(adminID, "Category3", Stream.of(p1, p2));


        assert ResponseFailureCheck(() ->
        {
            this.addCategory(customerID1, "Category5", Stream.of(p1, p2));
        }, HttpStatus.FORBIDDEN) : "Only admins should be able to create categoires";
        assert ResponseFailureCheck(() ->
        {
            this.addCategory(adminID, "Category1", Stream.of(p1, p2));
        }, HttpStatus.CONFLICT) : "Should fail on duplicate category";

        assert ResponseFailureCheck(() ->
        {
            this.addCategory(adminID, "Category1", Stream.of(p1, "Not a product"));
        }, HttpStatus.NOT_FOUND) : "Should fail on product not being found";

        this.deleteCategory(adminID, "Category1", Stream.of(p1));
        this.deleteCategory(adminID, "Category3", Stream.of(p1));

        assert ResponseFailureCheck(() ->
        {
            this.deleteCategory(customerID1, "Category2", Stream.of(p2));
        }, HttpStatus.FORBIDDEN) : "Only admins should be able to delete categories";
        assert ResponseFailureCheck(() ->
        {
            this.deleteCategory(adminID, "Category2", Stream.of(p1, p2));
        }, HttpStatus.CONFLICT) : "Should fail on deleting category that product doesn't have";
    }

    @Test
    public void testMAP() throws Exception
    {
        this.accountController.ClearAllExceptAdmin();
        this.productController.ClearAll();
        String adminID = login("admin", "admin");
        String customerID1 = createUser(null, "customer", "1234", null);
        String shopkeeperID1 = createUser(adminID, "shopkeeper", "1234", AccountPermission.SHOPKEEPER);

        String p1 = createProduct(shopkeeperID1, "s1", "s1desc", 1000L);

        addMAP(adminID,p1,1.05,1000L,2000L);
        addMAP(adminID,p1,1.05,2001L,3000L);
        assert ResponseFailureCheck(()->
        {
            addMAP(customerID1,p1,1.05,4000L,5000L);
        },HttpStatus.FORBIDDEN) : "Only admins can edit map ranges";

        assert ResponseFailureCheck(()->
        {
            addMAP(adminID,p1,1.05,1500L,1800L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addMAP(adminID,p1,1.05,500L,1500L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addMAP(adminID,p1,1.05,1500L,2500L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addMAP(adminID,p1,1.05,500L,1000L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addMAP(adminID,p1,1.05,2000L,2500L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addMAP(adminID,p1,1.05,500L,200L);
        },HttpStatus.BAD_REQUEST) : "map with invalid date range sohuld fail";


        deleteMAP(adminID,p1,1500L);

        assert  ResponseFailureCheck( ()->
        {
            deleteMAP(shopkeeperID1,p1,1500L);
        },HttpStatus.NOT_FOUND) : "Should be unable to delete if no range found";

        assert ResponseFailureCheck( ()->
        {
            deleteMAP(customerID1,p1,2500L);
        },HttpStatus.FORBIDDEN) : "Customers should not be able to delete MAP ranges";

        assert this.productController.GetMAPs(adminID,p1).size() == 1 : "Should have 1 map remaining at end of tests";

    }

    @Test
    public void testPrice() throws Exception
    {
        this.accountController.ClearAllExceptAdmin();
        this.productController.ClearAll();
        String adminID = login("admin", "admin");
        String customerID1 = createUser(null, "customer", "1234", null);
        String shopkeeperID1 = createUser(adminID, "shopkeeper", "1234", AccountPermission.SHOPKEEPER);

        String p1 = createProduct(shopkeeperID1, "s1", "s1desc", 1000L);

        addPrice(adminID,p1,1.05,1000L,2000L);
        addPrice(adminID,p1,1.05,2001L,3000L);
        assert ResponseFailureCheck(()->
        {
            addPrice(customerID1,p1,1.05,4000L,5000L);
        },HttpStatus.FORBIDDEN) : "Only admins can edit map ranges";

        assert ResponseFailureCheck(()->
        {
            addPrice(adminID,p1,1.05,1500L,1800L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addPrice(adminID,p1,1.05,500L,1500L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addPrice(adminID,p1,1.05,1500L,2500L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addPrice(adminID,p1,1.05,500L,1000L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addPrice(adminID,p1,1.05,2000L,2500L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addPrice(adminID,p1,1.05,500L,200L);
        },HttpStatus.BAD_REQUEST) : "map with invalid date range sohuld fail";


        deletePrice(adminID,p1,1500L);

        assert  ResponseFailureCheck( ()->
        {
            deletePrice(shopkeeperID1,p1,1500L);
        },HttpStatus.NOT_FOUND) : "Should be unable to delete if no range found";

        assert ResponseFailureCheck( ()->
        {
            deletePrice(customerID1,p1,2500L);
        },HttpStatus.FORBIDDEN) : "Customers should not be able to delete MAP ranges";

        assert this.productController.GetPrices(adminID,p1).size() == 1 : "Should have 1 map remaining at end of tests";

    }

    @Test
    public void testSales() throws Exception
    {
        this.accountController.ClearAllExceptAdmin();
        this.productController.ClearAll();
        String adminID = login("admin", "admin");
        String customerID1 = createUser(null, "customer", "1234", null);
        String shopkeeperID1 = createUser(adminID, "shopkeeper", "1234", AccountPermission.SHOPKEEPER);

        String p1 = createProduct(shopkeeperID1, "s1", "s1desc", 1000L);

        addSale(adminID,p1,1.05,1000L,2000L);
        addSale(adminID,p1,1.05,2001L,3000L);
        assert ResponseFailureCheck(()->
        {
            addSale(customerID1,p1,1.05,4000L,5000L);
        },HttpStatus.FORBIDDEN) : "Only admins can edit map ranges";

        assert ResponseFailureCheck(()->
        {
            addSale(adminID,p1,1.05,1500L,1800L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addSale(adminID,p1,1.05,500L,1500L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addSale(adminID,p1,1.05,1500L,2500L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addSale(adminID,p1,1.05,500L,1000L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addSale(adminID,p1,1.05,2000L,2500L);
        },HttpStatus.CONFLICT) : "adding map to overlapping range should fail";
        assert ResponseFailureCheck(()->
        {
            addSale(adminID,p1,1.05,500L,200L);
        },HttpStatus.BAD_REQUEST) : "map with invalid date range sohuld fail";


        deleteSale(adminID,p1,1500L);

        assert  ResponseFailureCheck( ()->
        {
            deleteSale(shopkeeperID1,p1,1500L);
        },HttpStatus.NOT_FOUND) : "Should be unable to delete if no range found";

        assert ResponseFailureCheck( ()->
        {
            deleteSale(customerID1,p1,2500L);
        },HttpStatus.FORBIDDEN) : "Customers should not be able to delete MAP ranges";

        assert this.productController.GetSales(adminID,p1).size() == 1 : "Should have 1 map remaining at end of tests";

    }

    @Test
    public void ProductPriceCalculation()
    {
        this.accountController.ClearAllExceptAdmin();
        this.productController.ClearAll();
        String adminID = login("admin", "admin");
        String customerID1 = createUser(null, "customer", "1234", null);
        String shopkeeperID1 = createUser(adminID, "shopkeeper", "1234", AccountPermission.SHOPKEEPER);

        String p1 = createProduct(shopkeeperID1, "s1", "s1desc", 1000L);

        addMAP(adminID,p1,1.2,1000L,2000L);
        addPrice(adminID,p1,1.4,1000L,2000L);
        addSale(adminID,p1,0.1,1500L,2500L);
        addCoupon(adminID,Stream.of(p1),"code",1500L,2500L);

        PriceDTO dto = this.productController.GetPrice(p1,adminID,null,1700L);

        assert dto.map == 1.2;
        assert dto.basePrice == 1.4;
        assert dto.realPrice == (1-0.1) * 1.4;


        PriceDTO dtoC = this.productController.GetPrice(p1,adminID,"code",1700L);
        System.out.println(dtoC.realPrice);
        assert dtoC.map == 1.2 : "incorrect map for couponed";
        assert dtoC.basePrice == 1.4 : "incorrect base price for coupon";
        assert dtoC.realPrice == (1-0.1) * (1.0-0.12)*1.4 : "incorrect real price for coupon";


    }


    public PriceDTO GetPrices(String userID, String productID, Long date, String coupon)
    {
        return this.productController.GetPrice(productID,userID,coupon,date);
    }
    public void addMAP(String userID, String productID, Double map, Long start, Long end) throws ResponseStatusException
    {
        DoubleRangedDTO dto = createDoubleRanged(userID,productID,map,start,end);
        this.productController.AddMAP(dto);
    }
    public void addPrice(String userID, String productID, Double price, Long start, Long end)  throws ResponseStatusException
    {
        DoubleRangedDTO dto = createDoubleRanged(userID,productID,price,start,end);
        this.productController.AddPrice(dto);
    }
    public void addSale(String userID, String productID, Double sale, Long start, Long end)  throws ResponseStatusException
    {
        DoubleRangedDTO dto = createDoubleRanged(userID,productID,sale,start,end);
        this.productController.AddSale(dto);
    }
    public void deleteMAP(String userID, String productID, Long date)  throws ResponseStatusException
    {
        DatedProductDTO dto = createDated(userID,productID,date);
        this.productController.DeleteMAP(dto);
    }
    public void deletePrice(String userID, String productID, Long date)  throws ResponseStatusException
    {
        DatedProductDTO dto = createDated(userID,productID,date);
        this.productController.DeletePrice(dto);
    }
    public void deleteSale(String userID, String productID, Long date)  throws ResponseStatusException
    {
        DatedProductDTO dto = createDated(userID,productID,date);
        this.productController.DeleteSale(dto);
    }
    private DoubleRangedDTO createDoubleRanged(String userID, String productID, Double value, Long start, Long end)
    {
        DoubleRangedDTO dto = new DoubleRangedDTO();
        dto.userID = userID;
        dto.value = value;
        dto.productID = productID;
        dto.startDate = start;
        dto.endDate = end;
        return dto;
    }
    private DatedProductDTO createDated(String userID, String productID, Long date)
    {
        DatedProductDTO dto = new DatedProductDTO();
        dto.userID = userID;
        dto.productID=productID;
        dto.date = date;

        return dto;
    }
    public void deleteCategory(String userID, String category, Stream<String> ip)
    {
        List<String> products = ip.collect(Collectors.toList());
        CategoryDTO dto = new CategoryDTO();
        dto.categoryName = category;
        dto.userID = userID;
        dto.productIDs = products;
        this.productController.RemoveCategoriesToProducts(dto);
    }
    public void addCategory(String userID, String category, Stream<String> ip)
    {
        List<String> products = ip.collect(Collectors.toList());
        CategoryDTO dto = new CategoryDTO();
        dto.categoryName = category;
        dto.userID = userID;
        dto.productIDs = products;
        this.productController.AddCategoriesToProducts(dto);
    }

    public void deleteCoupon(String userID, String code)
    {
        CouponDTO dto = new CouponDTO();
        dto.userID = userID;
        dto.code = code;
        this.productController.DeleteCoupon(dto);
    }
    public void addCoupon(String userID, Stream<String> ip, String code, Long start, Long end)
    {
        List<String> products = ip.collect(Collectors.toList());
        CouponDTO dto = new CouponDTO();
        dto.userID = userID;
        dto.startDate = start;
        dto.endDate = end;
        dto.productIDs = products;
        dto.code = code;
        dto.sale = 0.12;
        this.productController.AddCoupon(dto);
    }
    public void deleteProduct(String userID, String productID)
    {
        ProductDTO dto = new ProductDTO();
        dto.userID = userID;
        dto.productID = productID;

        this.productController.DeleteProduct(dto);
    }
    public void editProduct(String userID, String productID, String productName, String description,Long date) throws ResponseStatusException
    {
        ProductDTO dto = new ProductDTO();
        dto.productName = productName ;
        dto.userID = userID ;
        dto.description =  description;
        dto.startDate = date;
        dto.productID = productID;
        this.productController.EditProduct(dto);
    }
    public String createProduct(String userID, String productName, String description,Long date) throws ResponseStatusException
    {
        ProductDTO dto = new ProductDTO();
        dto.defaultPrice = 1.0;
        dto.defaultMAP = 1.0;
        dto.productName = productName ;
        dto.userID = userID ;
        dto.description =  description;
        dto.images = new ArrayList<String>()
        {
            {
                add("testImage1");
                add("testImage2");
            }
        };
        dto.startDate = date;
        return this.productController.CreateProduct(dto).getProductID();
    }
    public String login(String email, String password) throws ResponseStatusException
    {
        AccountDTO dto = new AccountDTO();
        dto.email = email;
        dto.password = password;
        String val = this.accountController.Login(dto);
        return val;
    }
    public String createUser(String userID, String email, String password, AccountPermission perm) throws ResponseStatusException
    {
        AccountDTO dto = new AccountDTO();
        dto.userID = userID;
        dto.email = email;
        dto.password = password;
        if(perm != null)
        {
            dto.permission = perm.name();
        }

        return this.accountController.RegisterNewUser(dto);
    }
    public boolean ResponseFailureCheck(NoArgResponseOperator lambda, HttpStatus status) throws Exception
    {
        try
        {
            lambda.op();
            return false;
        }
        catch(ResponseStatusException exception)
        {
            if(exception.getStatus() != status)
            {
                throw new Exception("Exception status" + exception.getStatus() + " and " + status + " did not match");
            }
            return exception.getStatus() == status;
        }
    }
}
