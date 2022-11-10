package org.yorksolutions.teamobjbackend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.*;
import org.yorksolutions.teamobjbackend.dtos.RequestDTO;
import org.yorksolutions.teamobjbackend.entities.Account;
import org.yorksolutions.teamobjbackend.services.AccountService;

import java.util.List;

/**
 * Account controller endpoints
 * endpoint: /api/user/
 * @author MaxFeige
 */
@RestController
@RequestMapping("/api/user/")
@CrossOrigin
public class AccountController
{
    private final AccountService accountService;
    @Autowired
    public AccountController(AccountService accountService)
    {
        this.accountService = accountService;
    }


    /**
     * POST /api/user/register
     * {@link AccountService#AttemptRegister(AccountDTO)}
     * @param dto DTO
     * @return userID
     */
    @PostMapping("register")
    public String RegisterNewUser(@RequestBody AccountDTO dto)
    {
        return this.accountService.AttemptRegister(dto);
    }
    /**
     * POST /api/user/login
     * {@link AccountService#AttemptLogin(AccountDTO)}
     * @param dto DTO
     * @return userID
     */
    @PostMapping("login")
    public String Login(@RequestBody AccountDTO dto)
    {
        return this.accountService.AttemptLogin(dto);
    }

    /**
     * PUT /api/user/update
     * {@link AccountService#EditAccount(AccountDTO)}
     * @param dto DTO
     * @return userID
     */
    @PutMapping("update")
    public String ChangeAccountInfo(@RequestBody AccountDTO dto)
    {
        return this.accountService.EditAccount(dto);
    }
    /**
     * PUT /api/user/update/admin
     * {@link AccountService#EditAccountAdmin(AdminAccountChangeDTO)}
     * @param dto DTO
     * @return userID
     */
    @PutMapping("update/admin")
    public String AdminChangeAccountInfo(@RequestBody AdminAccountChangeDTO dto)
    {
        return this.accountService.EditAccountAdmin(dto);
    }
    /**
     * DELETE /api/user/delete
     * {@link AccountService#DeleteAccount(AccountDTO)}
     * @param dto DTO
     */
    @DeleteMapping("delete")
    public void DeleteAccount(@RequestBody AccountDTO dto)
    {
        this.accountService.DeleteAccount(dto);
    }
    /**
     * GET /api/user/find
     * {@link AccountService#GetAccountByEmail(AccountDTO)}
     * @param userID user makeing request
     * @param email email of user to find
     * @return Account of user
     */
    @GetMapping("find")
    public Account FindAccount(@RequestParam("userID") String userID, @RequestParam("email") String email)
    {
        AccountDTO dto = new AccountDTO();
        dto.userID = userID;
        dto.email = email;
        return this.accountService.GetAccountByEmail(dto);
    }
    /**
     * GET /api/user/all
     * {@link AccountService#GetAllAccounts(RequestDTO)}
     * @param userID user making request
     * @return all accounts
     */
    @GetMapping("all")
    public List<Account> GetAllAccounts(@RequestParam("userID") String userID)
    {
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;

        return this.accountService.GetAllAccounts(dto);
    }
    /**
     * GET /api/user/permission
     * {@link AccountService#GetPermissionLevel(RequestDTO)}
     * @param userID ID of user to get permission level of
     * @return {@link org.yorksolutions.teamobjbackend.entities.AccountPermission} permission as string
     */
    @GetMapping("permission")
    public String GetMyPermissionLevel(@RequestParam("userID") String userID)
    {
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;

        return this.accountService.GetPermissionLevel(dto);
    }
    /**
     * PUT /api/user/checkout
     * {@link AccountService#Checkout(RequestDTO)}
     * @param dto DTO
     */
    @PostMapping("checkout")
    public void Checkout(@RequestBody RequestDTO dto)
    {
        this.accountService.Checkout(dto);
    }
    /**
     * GET /api/user/orders
     * {@link AccountService#GetHistory(RequestDTO)}
     * @param userID ID of user
     * @return List of orders of given user
     */
    @GetMapping("orders")
    public List<OrderDTO> GetOrderHistory(@RequestParam("userID") String userID)
    {
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;
        return this.accountService.GetHistory(dto);
    }
    /**
     * PUT /api/user/cart
     * {@link AccountService#ChangeCart(CartChangeDTO)}
     * @param dto DTO
     */
    @PutMapping("cart")
    public void ChangeCart(@RequestBody CartChangeDTO dto)
    {
        this.accountService.ChangeCart(dto);
    }
    /**
     * GET /api/user/cart
     * {@link AccountService#GetCart(RequestDTO)}
     * @param userID ID of user
     * @return The current cart of the user, with null total and null date
     */
    @GetMapping("cart")
    public OrderDTO GetCart(@RequestParam("userID") String userID)
    {
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;
        return this.accountService.GetCart(dto);
    }


    public void ClearAllExceptAdmin()
    {
        this.accountService.TestClear();
    }

}
