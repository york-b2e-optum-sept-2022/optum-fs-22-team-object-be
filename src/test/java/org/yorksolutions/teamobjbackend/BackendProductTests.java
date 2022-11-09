package org.yorksolutions.teamobjbackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.controllers.AccountController;
import org.yorksolutions.teamobjbackend.controllers.ProductController;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.AccountDTO;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;
import org.yorksolutions.teamobjbackend.entities.Product;

import java.util.ArrayList;

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

        String adminID = login("admin","admin");
        String customerID1 = createUser(null,"customer","1234",null);
        String shopkeeperID1 = createUser(adminID,"shopkeeper","1234",null);

        createProduct(shopkeeperID1,"s1","s1desc",1000L);
        createProduct(adminID,"a1","a1desc",1000L);

        ResponseFailureCheck( ()->
        {

        },HttpStatus.FORBIDDEN) : "Only shopkeepers or admins can create products";




    }

    public String createProduct(String userID, String productName, String description,Long date)
    {
        ProductDTO dto = new ProductDTO();
        dto.defaultPrice = 1.0;
        dto.defaultMAP = 0.8;
        dto.productName = ;
        dto.userID = ;
        dto.description = "description";
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
