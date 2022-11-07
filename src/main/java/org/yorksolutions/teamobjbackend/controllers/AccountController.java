package org.yorksolutions.teamobjbackend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yorksolutions.teamobjbackend.dtos.AccountDTO;
import org.yorksolutions.teamobjbackend.entities.Account;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;
import org.yorksolutions.teamobjbackend.repositories.AccountRepository;
import org.yorksolutions.teamobjbackend.repositories.ProductOrderRepository;
import org.yorksolutions.teamobjbackend.utils.YorkUtils;

import java.util.Optional;

@RestController
@RequestMapping("/api/user/")
public class AccountController
{

    private final AccountRepository accountRepository;
    private final ProductOrderRepository productOrderRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository, ProductOrderRepository productOrderRepository)
    {
        this.accountRepository = accountRepository;
        this.productOrderRepository = productOrderRepository;
    }

    @PostMapping("register")
    public Account RegisterNewUser(@RequestBody AccountDTO dto)
    {
        Optional<Account> existingAccount = accountRepository.findAccountByEmail(dto.email);
        if(existingAccount.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Account with email already exists");
        }
        AccountPermission permissionLevel = AccountPermission.CUSTOMER;
        //Guard for creating something of a high level
        if(dto.permission != null)
        {
            var requestedPerm = AccountPermission.valueOf(dto.permission);
            if(requestedPerm != AccountPermission.CUSTOMER)
            {
                var creator = accountRepository.findById(dto.userID);
                if(creator.isEmpty() || creator.get().getPermission() != AccountPermission.ADMIN)
                {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Account creation of high permission must be made by administrator");
                }
                permissionLevel = requestedPerm;
            }
        }
        Account newAccount = new Account(YorkUtils.GenerateUUID(),permissionLevel,dto.email,dto.password);
        productOrderRepository.save(newAccount.getCart());
        accountRepository.save(newAccount);
        return newAccount;

    }
}
