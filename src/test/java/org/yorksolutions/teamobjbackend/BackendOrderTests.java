package org.yorksolutions.teamobjbackend;

import org.apache.coyote.Request;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.controllers.AccountController;
import org.yorksolutions.teamobjbackend.controllers.ProductController;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.AccountDTO;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.CartChangeDTO;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.OrderDTO;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
import org.yorksolutions.teamobjbackend.dtos.RequestDTO;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class BackendOrderTests
{
    private final AccountController accountController;
    private final ProductController productController;
    @Autowired
    public BackendOrderTests(AccountController accountController, ProductController productController)
    {
        this.accountController = accountController;
        this.productController = productController;
    }

    @Test
    public void CartTesting()
    {
        this.accountController.ClearAllExceptAdmin();
        this.productController.ClearAll();

        String adminID = login("admin","admin");

        String p1 = createProduct(adminID, "p1", "p1desc", 1000L, 2.0);
        String p2 = createProduct(adminID, "p2", "p2desc", 2000L, 3.0);
        String p3 = createProduct(adminID,"p3","p3desc",2000L, 5.0);

        SetCart(adminID,p1,3);
        SetCart(adminID,p2,5);
        SetCart(adminID,p2,0);


        var cart = GetCart(adminID);

        assert cart.productAmounts.size() == 1 : "Correct cart number";
        assert cart.productAmounts.get(0).product.getProductName().equals("p1") : "Correct product ID";
        assert cart.productAmounts.get(0).amount == 3 : "correct amount";
        assert  cart.price == 2.0 * 3 : "correct price";

        RequestDTO dto = new RequestDTO();
        dto.userID = adminID;
        this.accountController.Checkout(dto,null);
        cart = GetCart(adminID);

        assert cart.productAmounts.size() == 0;

        List<OrderDTO> orders = this.accountController.GetOrderHistory(adminID);

        assert orders.size() == 1;
        assert orders.get(0).price == 2.0 * 3;
        assert orders.get(0).date != null;


    }

    public OrderDTO GetCart(String userID)
    {
        return this.accountController.GetCart(userID,null);
    }

    public void SetCart(String userID, String productID, Integer number)
    {
        CartChangeDTO dto = new CartChangeDTO();
        dto.userID = userID;
        dto.productID = productID;
        dto.number = number;
        this.accountController.ChangeCart(dto);
    }
    public String login(String email, String password) throws ResponseStatusException
    {
        AccountDTO dto = new AccountDTO();
        dto.email = email;
        dto.password = password;
        String val = this.accountController.Login(dto);
        return val;
    }



    public String createProduct(String userID, String productName, String description,Long date, Double price) throws ResponseStatusException
    {
        ProductDTO dto = new ProductDTO();
        dto.defaultPrice = price;
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

}
