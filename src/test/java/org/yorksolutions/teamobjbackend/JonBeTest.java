package org.yorksolutions.teamobjbackend;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Equality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.controllers.AccountController;
import org.yorksolutions.teamobjbackend.controllers.ProductController;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.AccountDTO;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.CartChangeDTO;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.OrderDTO;
import org.yorksolutions.teamobjbackend.dtos.ProductDTO;
import org.yorksolutions.teamobjbackend.dtos.RequestDTO;
import org.yorksolutions.teamobjbackend.entities.Account;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;
import org.yorksolutions.teamobjbackend.services.AccountService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class JonBeTest {

    private AccountController accountController;
    private ProductController productController;

    @Autowired
    public JonBeTest(AccountController accountController, ProductController productController)
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
    public void testAccountUpdates()
    {
        accountController.ClearAllExceptAdmin();
        //Test admin login
        String adminID = login("admin","admin");
        String customerID1 = createUser(null,"customer1","1234",null);
        String shopkeeperID1 = createUser(adminID,"shopkeeper1","1234",AccountPermission.SHOPKEEPER);
        String adminID1 = createUser(adminID,"admin1","1234",AccountPermission.ADMIN);

        updateUser(customerID1,"customer1new","12345",AccountPermission.CUSTOMER);

        //no need to assert failure check since any account can be continually updated

    }

    @Test
    public void testGetUsers() throws Exception {
        accountController.ClearAllExceptAdmin();
        String adminID = login("admin", "admin");
        //Test admin login
        String adminID2 = createUser(adminID,"admin2","12345",AccountPermission.ADMIN);
        String customerID1 = createUser(null,"customer1","1234",null);
        String shopkeeperID1 = createUser(adminID,"shopkeeper1","1234",AccountPermission.SHOPKEEPER);
        String adminID1 = createUser(adminID,"admin1","1234",AccountPermission.ADMIN);
        getUsers(adminID, "shopkeeper1");

        assert ResponseFailureCheck( () -> {
            // We are asserting that if we try to search for a non-existent user, we will get an exception
            getUsers(adminID, "admin25");

        }, HttpStatus.NOT_FOUND) : "Customer does not exist.";
//        updated to not found request
        assert ResponseFailureCheck( () ->{
            getUsers(shopkeeperID1, "shopkeeper1");
        }, HttpStatus.CONFLICT) : "Only admins can get other accounts";

    }
    //how to retrieve list of all accounts if parameters specify userID

    @Test
    public void testFindUser() throws Exception {
        accountController.ClearAllExceptAdmin();
        String adminID = login("admin", "admin");
        //Test admin login
        String customerID1 = createUser(null,"customer1","1234",null);
        String shopkeeperID1 = createUser(adminID,"shopkeeper1","1234",AccountPermission.SHOPKEEPER);
        String adminID1 = createUser(adminID,"admin1","1234",AccountPermission.ADMIN);

        findUser(adminID, "customer1");
        //testing finduser for each account

        assert ResponseFailureCheck( () -> {
            // We are asserting that if we try to search for a non-existent user, we will get an exception
            findUser(adminID, "customer1new");
        }, HttpStatus.NOT_FOUND) : "Email and/or user does not exist.";
//        updated to not found request
        assert ResponseFailureCheck( () ->{
            findUser(shopkeeperID1, "shopkeeper1");
        }, HttpStatus.FORBIDDEN) : "Only admins can get other accounts";
        //returned 403 vs 409
    }

    @Test
    public void testFindUser1() throws Exception {
        String adminID = login("admin", "admin");
        //Test admin login
        String customerID1 = createUser(null,"customer12","1234",null);
        findUser(adminID, "shopkeeper1");
        //testing finduser for each account

        assert ResponseFailureCheck( () -> {
            // We are asserting that if we try to search for a non-existent user, we will get an exception
            findUser(adminID, "customer1new");
        }, HttpStatus.NOT_FOUND) : "Email and/or user does not exist.";
//        updated to not found request
        assert ResponseFailureCheck( () ->{
            findUser(customerID1, "customer1");
        }, HttpStatus.FORBIDDEN) : "Only admins can get other accounts";
        //returned 403 vs 409
    }

    @Test
    public void testFindUser2() {
        String adminID = login("admin", "admin");
        //Test admin login
        String adminID1 = createUser(adminID,"admin1","1234",AccountPermission.ADMIN);
        findUser(adminID1, "admin1");
        //testing finduser for each account
        //already tested response failure above
    }

    @Test
    public void testDeleteUser() throws Exception {
        accountController.ClearAllExceptAdmin();
        String adminID = login("admin", "admin");
//        Test admin login
//        Removing and recreating users for each test
        String customerID1 = createUser("customerID1", "customer1", "12345", AccountPermission.CUSTOMER);
        String shopkeeperID1 = createUser(adminID, "shopkeeper1", "1234", AccountPermission.SHOPKEEPER);

        deleteUser(adminID, "customer1", "12345", AccountPermission.CUSTOMER);
        deleteUser(adminID,  "shopkeeper1", "1234", AccountPermission.SHOPKEEPER);
        //delete customer1 and shopkeeper1 which was previously updated

        assert ResponseFailureCheck(() ->
        {
            // User already deleted
            // We are asserting that if we try to delete the same user, we will get an exception
            deleteUser("customerID1", "customer1", "12345", AccountPermission.CUSTOMER);

        }, HttpStatus.BAD_REQUEST) : "Customer does not exist.";
        //updated to bad request

        //must be an admin to delete accounts
        assert ResponseFailureCheck(() ->
        {
            deleteUser(shopkeeperID1, "shopkeeper1", "1234", AccountPermission.SHOPKEEPER);

        }, HttpStatus.BAD_REQUEST) : "Must be an Admin to delete accounts";

        //admin cannot delete their own account
        assert ResponseFailureCheck(() ->
        {
            deleteUser(adminID, "admin", "123", AccountPermission.ADMIN);

        }, HttpStatus.FORBIDDEN) : "Admin cannot delete their own account";
    }
    @Test
    public void testGetOrders() throws Exception {
        accountController.ClearAllExceptAdmin();
        String adminID = login("admin", "admin");
        String customerID1 = createUser(null, "customer1new", "12345", AccountPermission.CUSTOMER);
        //currentCustomer will hold the returned account from findUser
        Account currentCustomer = findUser(adminID, "customer1new");
        //Now we use getId() method from the Account entities.
        //Retrieve the userId and assign to String customerUserId
        String customerUserId = currentCustomer.getId();
        // We pass the actual userId through to getOrders
        // getOrders calls the controller then service and uses the userId to retrieve orders.
        getOrders(customerUserId);

        assert ResponseFailureCheck( () ->
                {
                    // Asserting we must have a userID to request an account
                    getOrders(null);

                }, HttpStatus.BAD_REQUEST) : "userID must be provided";
        assert ResponseFailureCheck( () ->
                {
                    // Asserting we must have a valid userID
                    getOrders("invalid");

                }, HttpStatus.BAD_REQUEST) : "userID is invalid";

    }

    @Test
    public void testPermission() throws Exception {
        accountController.ClearAllExceptAdmin();
        String adminID = login("admin", "admin");
        String shopkeeperID1 = createUser(adminID, "shopkeeper1", "1234", AccountPermission.SHOPKEEPER);
        //currentCustomer will hold the returned account from findUser
        Account currentCustomer = findUser(adminID, "shopkeeper1");
        //Now we use getId() method from the Account entities.
        //Retrieve the userId and assign to String customerUserId
        String customerUserId = currentCustomer.permissionAsString();
        // We pass the actual userId through to permissionAsString
        // getPermission calls the controller then service and uses the userId to retrieve orders.
        getPermission(adminID);

        assert ResponseFailureCheck( () ->
        {
            // Asserting we must have a userID to request an account
            getPermission(null);

        }, HttpStatus.BAD_REQUEST) : "userID must be provided";
        assert ResponseFailureCheck( () ->
        {
            // Asserting we must have a valid userID
            getPermission("invalid");

        }, HttpStatus.BAD_REQUEST) : "userID is invalid";
    }

    //**TODO Need to add to car to test checkout
    @Test
    public void testCheckout() throws Exception{
        accountController.ClearAllExceptAdmin();
        RequestDTO dto = new RequestDTO();
        String adminID = login("admin", "admin");
        String shopkeeperID1 = createUser(adminID, "shopkeeper1", "1234", AccountPermission.SHOPKEEPER);

        getCheckout(shopkeeperID1);

        assert ResponseFailureCheck( () ->
        {
            // Assert we must have items in cart to checkout
            getCheckout(shopkeeperID1);

        }, HttpStatus.BAD_REQUEST) : "Cannot check out with 0 items";


    }
    //**TODO need to confirm how to add to cart
    @Test
    public void testAddToCart() throws ResponseStatusException{
        accountController.ClearAllExceptAdmin();
        productController.ClearAll();
        //logging in
        String adminID = login("admin", "admin");

        //creating users
//        String customerID1 = createUser(null,"customer1","1234",null);
        String shopkeeperID1 = createUser(adminID,"shopkeeper1","1234",AccountPermission.SHOPKEEPER);
//        String adminID1 = createUser(adminID,"admin1","1234",AccountPermission.ADMIN);

        //Grab the shopkeepers userID since it is a randomly generated id
        //This is used to pass into addToCart()
        //We need this because the AddToCart function in the AccountService calls GetRequesterAccount, which needs userID
        Account currentShopkeeper = findUser(adminID, "shopkeeper1");
        String shopkeeperUserId = currentShopkeeper.permissionAsString();

        //Grab the admin1's userID since it is a randomly generated id
//        Account currentAdmin = findUser(adminID, "admin1");
//        String adminUserId = currentAdmin.permissionAsString();

        //creating products using shopkeeperID1 object
        String p1 = createProduct(shopkeeperID1, "product1", "firstProduct", 1000L);
//        String p2 = createProduct(adminID1, "product2", "secondProduct", 2000L);

        //We pass in the actual userID
        addToCart(shopkeeperUserId);

    }

    @Test
    public void testGetCart() throws Exception{
        accountController.ClearAllExceptAdmin();
        String adminID = login("admin", "admin");
        String adminID2 = createUser(adminID,"admin2","12345",AccountPermission.ADMIN);
        String customerID1 = createUser(null,"customer1","1234",null);
        String shopkeeperID1 = createUser(adminID,"shopkeeper1","1234",AccountPermission.SHOPKEEPER);
        String adminID1 = createUser(adminID,"admin1","1234",AccountPermission.ADMIN);
        getCart(adminID);

        assert ResponseFailureCheck( () ->
        {
            // Asserting we must have a userID to request an account
            getCart(null);

        }, HttpStatus.BAD_REQUEST) : "userID must be provided";
        assert ResponseFailureCheck( () ->
        {
            // Asserting we must have a valid userID
            getCart("invalid");

        }, HttpStatus.BAD_REQUEST) : "userID is invalid";

    }
    //Functions to run tests
    public void deleteUser(String userID, String email, String password, AccountPermission permission) throws ResponseStatusException{
        AccountDTO dto = new AccountDTO();
        dto.userID = userID;
        dto.email = email;
        dto.password = password;
        dto.permission = permission != null ? permission.name() : null;

        this.accountController.DeleteAccount(dto);
    }


    public OrderDTO getCart(String userID) throws ResponseStatusException{
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;
        return this.accountController.GetCart(userID);
    }

    public void addToCart(String userID) throws ResponseStatusException{
        CartChangeDTO dto = new CartChangeDTO();
        dto.userID = userID;
        this.accountController.ChangeCart(dto);
    }

    public List<OrderDTO> getOrders(String userID) throws ResponseStatusException{
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;
        return this.accountController.GetOrderHistory(userID);
    }

    public void getCheckout(String userID) throws ResponseStatusException{
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;
        this.accountController.Checkout(dto);
    }

    public String getPermission(String userID) throws ResponseStatusException{
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;

        return this.accountController.GetMyPermissionLevel(userID);
    }
    public List<Account> getUsers(String userID, String email) {
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;

        return this.accountController.GetAllAccounts(userID);
    }

    public Account findUser(String userID, String email){
        AccountDTO dto = new AccountDTO();
        dto.userID = userID;
        dto.email = email;
        return this.accountController.FindAccount(userID, email);
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
    public String createUser(String userID, String email, String password, AccountPermission perm) throws
    ResponseStatusException
    {
        AccountDTO dto = new AccountDTO();
        dto.userID = userID;
        dto.email = email;
        dto.password = password;
        if (perm != null) {
            dto.permission = perm.name();
        }

        return this.accountController.RegisterNewUser(dto);
    }

    public boolean ResponseFailureCheck (NoArgResponseOperator lambda, HttpStatus status) throws Exception
    {
        try {
            lambda.op();
            return false;
        } catch (ResponseStatusException exception) {
            if (exception.getStatus() != status) {
                throw new Exception("Exception status" + exception.getStatus() + " and " + status + " did not match");
            }
            return exception.getStatus() == status;
        }
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
    public boolean ResponseSuccessCheck (NoArgResponseOperator lambda)
    {
        try {
            lambda.op();
            return true;
        } catch (ResponseStatusException exception) {
            exception.printStackTrace();
            return false;
        }
        }

    }






