package org.yorksolutions.teamobjbackend;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.controllers.AccountController;
import org.yorksolutions.teamobjbackend.controllers.ProductController;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.AccountDTO;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;

@SpringBootTest
public class BackendHttpTests
{
    private AccountController accountController;
    private ProductController productController;

    @Autowired
    public BackendHttpTests(AccountController accountController, ProductController productController)
    {
        this.accountController = accountController;
        this.productController = productController;
    }

    @Test
    public void testLogin() throws Exception
    {
        String val = login("admin","admin");
        assert(val != null);
    }

    @Test
    public void testUserRegisterAndLogin() throws Exception
    {
        accountController.ClearAllExceptAdmin();
        //Test admin login
        String adminID = login("admin","admin");
        //Test random user register - null Account perms and non null
        String customerID1 = createUser(null,"customer1","1234",AccountPermission.CUSTOMER);
        String customerID2 = createUser(null,"customer2","1234",null);
        //Test admin register user
        String shopkeeperID1 = createUser(adminID,"customer3","1234",AccountPermission.CUSTOMER);
        //Test admin register user null AP
        String customerID4 = createUser(adminID,"customer4","1234",null);
        //Test admin register shopkeeper
        String shopkeerpID1 = createUser(adminID,"shopkeeper1","1234",AccountPermission.SHOPKEEPER);
        //Test admin create admin
        String adminID2 = createUser(adminID,"admin2","1234",AccountPermission.ADMIN);


        //Now for invalid registers

        //Test non-unique email
        assert ResponseFailureCheck( () ->
        {
            createUser(null,"customer1","1234",AccountPermission.CUSTOMER);

        },HttpStatus.CONFLICT) : "Created customer with existing email";

        //Test admin create existing email
        assert ResponseFailureCheck( () ->
        {
            createUser(adminID,"customer1","1234",AccountPermission.CUSTOMER);

        },HttpStatus.CONFLICT) : "Created customer with existing email";

        //Test user creation elevated permission
        assert ResponseFailureCheck( () ->
        {
            createUser(null,"badShopkeeper1","1234",AccountPermission.SHOPKEEPER);

        },HttpStatus.FORBIDDEN) : "Non-admin created user with elevated permissions";

        //test login with one of accounts
        login("customer1","1234");


    }
    @Test
    public void TestAccountUpdates()
    {
        accountController.ClearAllExceptAdmin();
        //Test admin login
        String adminID = login("admin","admin");
        String customerID1 = createUser(null,"customer1","1234",null);
        String shopkeeperID1 = createUser(adminID,"shopkeeper1","1234",AccountPermission.SHOPKEEPER);
        String adminID1 = createUser(adminID,"admin1","1234",AccountPermission.ADMIN);

        updateUser(customerID1,"customer1new","12345",AccountPermission.CUSTOMER);


    }

    public void updateUser(String userID, String email, String password, AccountPermission permission)
    {
        AccountDTO dto = new AccountDTO();
        dto.userID = userID;
        dto.email = email;
        dto.password = password;
        dto.permission = permission != null ? permission.name() : null;
        this.accountController.ChangeAccountInfo(dto);
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
    public boolean ResponseSuccessCheck(NoArgResponseOperator lambda)
    {
        try
        {
            lambda.op();
            return true;
        }
        catch(ResponseStatusException exception)
        {
            exception.printStackTrace();
            return false;
        }
    }





}
