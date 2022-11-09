package org.yorksolutions.teamobjbackend;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.controllers.AccountController;
import org.yorksolutions.teamobjbackend.controllers.ProductController;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.AccountDTO;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.AdminAccountChangeDTO;
import org.yorksolutions.teamobjbackend.entities.Account;
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
    public void TestAccountUpdates() throws Exception
    {
        accountController.ClearAllExceptAdmin();
        //Test admin login
        String adminID = login("admin","admin");
        String customerID1 = createUser(null,"customer1","1234",null);
        String shopkeeperID1 = createUser(adminID,"shopkeeper1","1234",AccountPermission.SHOPKEEPER);
        String adminID1 = createUser(adminID,"admin1","1234",AccountPermission.ADMIN);

        //Test update and login
        updateUser(customerID1,"customer1new","1234new",null);
        login("customer1new","1234new");
        //Test admin update and login
        updateUserAdmin(adminID1,"customer1newA","1234newA",null,customerID1);
        login("customer1newA","1234newA");
        //Test admin update permission
        updateUserAdmin(adminID1,"customer1newA","1234newA",AccountPermission.SHOPKEEPER,customerID1);
        assert this.accountController.GetMyPermissionLevel(customerID1) == AccountPermission.SHOPKEEPER.name();


        //Test failures:
        //Change email to taken email
        assert ResponseFailureCheck( () ->
        {
            updateUser(customerID1,"admin","1234",null);
        },HttpStatus.CONFLICT) : "User changed to email that already exists, shouldn't be allowed";

        assert  ResponseFailureCheck( () ->
        {
            updateUser("a","b","c",null);

        },HttpStatus.BAD_REQUEST) : "Updating an an unknown user should fail";

        assert ResponseFailureCheck( () ->
        {
            updateUserAdmin(shopkeeperID1,"admin1BAD","1234BAD",null,adminID);
        }, HttpStatus.FORBIDDEN) : "only admins should be able to update accounts";

        assert ResponseFailureCheck( () ->
        {
            updateUserAdmin(adminID,"admin1BAD","1234BAD",null,"abc");
        }, HttpStatus.NOT_FOUND) : "only should fail on unknown account";


    }

    @Test
    public void TestAccountDeletion() throws Exception
    {
        accountController.ClearAllExceptAdmin();
        String adminID = login("admin","admin");
        String customerID1 = createUser(null,"customer1","1234",null);
        String shopkeeperID1 = createUser(adminID,"shopkeeper1","1234",AccountPermission.SHOPKEEPER);
        String adminID1 = createUser(adminID,"admin1","1234",AccountPermission.ADMIN);


        //Test self delete
        deleteUser(customerID1,null);
        //Test admin delete
        deleteUser(adminID,"admin1");


        //Test admin delete self
        assert ResponseFailureCheck( () ->
        {
            deleteUser(adminID,"admin");
        },HttpStatus.FORBIDDEN) : "Admin shouldn't be able to delete self";
        assert ResponseFailureCheck( () ->
        {
            deleteUser(adminID,null);
        },HttpStatus.FORBIDDEN) : "Admin shouldn't be able to delete self";
        //Test admin delete unknown
        assert ResponseFailureCheck( () ->
        {
            deleteUser(adminID,"abc");
        },HttpStatus.NOT_FOUND) : "Shouldn't be able to delete unknown user";

        String customerID2 = createUser(null,"customer1","1234",null);
        //Test non admin delete admin
        assert ResponseFailureCheck( () ->
        {
            deleteUser(customerID2,"admin");
        },HttpStatus.FORBIDDEN) : "non-admin shouldn't be able to delete other users";

    }

    @Test
    public void testGetUser() throws Exception
    {
        accountController.ClearAllExceptAdmin();
        String adminID = login("admin","admin");
        String customerID1 = createUser(null,"customer1","1234",null);

        getUser(adminID,"customer1");


        assert ResponseFailureCheck( () ->
        {
            getUser(adminID,"adfhawret");
        },HttpStatus.NOT_FOUND) : "Should be not found on non-existent user";

        assert ResponseFailureCheck( () ->
        {
            getUser(customerID1, "admin");
        },HttpStatus.FORBIDDEN) : "Should only admins can get other userIDs";

        assert this.accountController.GetAllAccounts(adminID).size() == 2;

        assert  ResponseFailureCheck( () ->
        {
            this.accountController.GetAllAccounts(customerID1);
        },HttpStatus.FORBIDDEN) : "Only admins should be able to get all accounts";

    }

    public Account getUser(String userID, String email)
    {
        return this.accountController.FindAccount(userID,email);
    }
    public void deleteUser(String userID, String email)
    {
        AccountDTO dto = new AccountDTO();
        dto.userID = userID;
        dto.email = email;

        this.accountController.DeleteAccount(dto);
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
    public void updateUserAdmin(String userID, String email, String password, AccountPermission permission, String accountToChangeID)
    {
        AdminAccountChangeDTO dto = new AdminAccountChangeDTO();
        dto.userID = userID;
        dto.email = email;
        dto.password = password;
        dto.permission = permission != null ? permission.name() : null;
        dto.accountChangeID = accountToChangeID;
        this.accountController.AdminChangeAccountInfo(dto);
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
