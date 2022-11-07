package org.yorksolutions.teamobjbackend.controllers;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yorksolutions.teamobjbackend.dtos.AccountDTO;
import org.yorksolutions.teamobjbackend.entities.Account;
import org.yorksolutions.teamobjbackend.entities.AccountPermission;
import org.yorksolutions.teamobjbackend.repositories.AccountRepository;

@RestController
@RequestMapping("/api/user/")
public class AccountController
{

    private final AccountRepository

    @PostMapping("register")
    public Account RegisterNewUser(@RequestBody AccountDTO dto)
    {
        if(dto.permission != null)
        {
            var ordinalPermissionValue = AccountPermission.valueOf(dto.permission);
            if(ordinalPermissionValue != AccountPermission.CUSTOMER)
            {

            }
        }

    }
}
