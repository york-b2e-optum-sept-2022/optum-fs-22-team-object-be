package org.yorksolutions.teamobjbackend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yorksolutions.teamobjbackend.dtos.AccountInfo.*;
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
    public Account FindAccount(@RequestBody AccountDTO dto)
    {
        return this.accountService.GetAccountByEmail(dto);
    }
    @GetMapping("all")
    public List<Account> GetAllAccounts(@RequestBody RequestDTO dto)
    {
        return this.accountService.GetAllAccounts(dto);
    }
    @GetMapping("permission")
    public String GetMyPermissionLevel(@RequestBody RequestDTO dto)
    {
        return this.accountService.GetPermissionLevel(dto);
    }
    @PostMapping("checkout")
    public void Checkout(@RequestBody RequestDTO dto)
    {
        this.accountService.Checkout(dto);
    }
    @GetMapping("orders")
    public List<OrderDTO> GetOrderHistory(@RequestBody RequestDTO dto)
    {
        return this.accountService.GetHistory(dto);
    }
    @PutMapping("cart")
    public void ChangeCart(@RequestBody CartChangeDTO dto)
    {
        this.accountService.AddToCart(dto);
    }
    @GetMapping("cart")
    public OrderDTO GetCart(@RequestBody RequestDTO dto)
    {
        return this.accountService.GetCart(dto);
    }

}
