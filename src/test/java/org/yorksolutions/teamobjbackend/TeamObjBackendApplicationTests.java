package org.yorksolutions.teamobjbackend;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.yorksolutions.teamobjbackend.entities.Account;
import org.yorksolutions.teamobjbackend.entities.Product;
import org.yorksolutions.teamobjbackend.repositories.AccountRepository;
import org.yorksolutions.teamobjbackend.repositories.ProductOrderRepository;
import org.yorksolutions.teamobjbackend.repositories.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TeamObjBackendApplicationTests
{
    ProductRepository productRepository;
    AccountRepository accountRepository;
    ProductOrderRepository productOrderRepository;

    @Test
    public void testCreate(){
        Account a = new Account();
        a.setId("1L");
        a.getPastOrders();
        a.setEmail("hello@hello.com");
        a.getPermission();
        a.setPassword("hello");
        accountRepository.save();
        assertNotNull(accountRepository.findById("1L").get());
        //set account to see if a new account is created per the parameters above

    }

    @Test
    public void testGetAllAccounts(){
        List<Account> list = (List<Account>) accountRepository.findAll();
        assertThat(list).size().isGreaterThan(0);
        //retrieve all existing accounts
    }

    @Test
    public void testUpdateAcct(){
    }




}
