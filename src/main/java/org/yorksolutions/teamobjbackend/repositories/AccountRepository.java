package org.yorksolutions.teamobjbackend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yorksolutions.teamobjbackend.entities.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account,String>
{
    public Optional<Account> findAccountByEmail(String email);
}
