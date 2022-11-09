package org.yorksolutions.teamobjbackend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.*;
import org.yorksolutions.teamobjbackend.dtos.RequestDTO;
import org.yorksolutions.teamobjbackend.entities.Account;
import org.yorksolutions.teamobjbackend.services.AccountService;

import java.util.List;

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


    @PostMapping("register")
    public String RegisterNewUser(@RequestBody AccountDTO dto)
    {
        return this.accountService.AttemptRegister(dto);
    }
    @PostMapping("login")
    public String Login(@RequestBody AccountDTO dto)
    {
        return this.accountService.AttemptLogin(dto);
    }

    @PutMapping("update")
    public String ChangeAccountInfo(@RequestBody AccountDTO dto)
    {
        return this.accountService.EditAccount(dto);
    }
    @PutMapping("update/admin")
    public String AdminChangeAccountInfo(@RequestBody AdminAccountChangeDTO dto)
    {
        return this.accountService.EditAccountAdmin(dto);
    }
    @DeleteMapping("delete")
    public void DeleteAccount(@RequestBody AccountDTO dto)
    {
        this.accountService.DeleteAccount(dto);
    }
    @GetMapping("find")
    public Account FindAccount(@RequestParam("userID") String userID, @RequestParam("email") String email)
    {
        AccountDTO dto = new AccountDTO();
        dto.userID = userID;
        dto.email = email;
        return this.accountService.GetAccountByEmail(dto);
    }
    @GetMapping("all")
    public List<Account> GetAllAccounts(@RequestParam("userID") String userID)
    {
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;

        return this.accountService.GetAllAccounts(dto);
    }
    @GetMapping("permission")
    public String GetMyPermissionLevel(@RequestParam("userID") String userID)
    {
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;

        return this.accountService.GetPermissionLevel(dto);
    }
    @PostMapping("checkout")
    public void Checkout(@RequestBody RequestDTO dto)
    {
        this.accountService.Checkout(dto);
    }
    @GetMapping("orders")
    public List<OrderDTO> GetOrderHistory(@RequestParam("userID") String userID)
    {
        RequestDTO dto = new RequestDTO();
        dto.userID = userID;
        return this.accountService.GetHistory(dto);
    }
    @PutMapping("cart")
    public void ChangeCart(@RequestBody CartChangeDTO dto)
    {
        this.accountService.AddToCart(dto);
    }
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
