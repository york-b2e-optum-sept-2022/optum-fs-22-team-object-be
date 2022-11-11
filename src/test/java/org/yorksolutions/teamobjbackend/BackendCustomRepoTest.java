package org.yorksolutions.teamobjbackend;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yorksolutions.teamobjbackend.repositories.AccountRepository;
import org.yorksolutions.teamobjbackend.repositories.ProductRepository;

@SpringBootTest
public class BackendCustomRepoTest
{
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    @Autowired
    public BackendCustomRepoTest(AccountRepository accountRepository, ProductRepository productRepository)
    {
        this.accountRepository = accountRepository;
        this.productRepository = productRepository;
    }



    @Test
    public void testCategories()
    {
        var x = this.productRepository.getAllCategories();
        System.out.println(getIterableSize(x));
    }

    private <T> int getIterableSize(Iterable<T> it)
    {
        int num = 0;
        for(T t : it)
        {
            num++;
        }
        return num;
    }
}
