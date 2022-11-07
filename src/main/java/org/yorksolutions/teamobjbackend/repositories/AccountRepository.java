package org.yorksolutions.teamobjbackend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yorksolutions.teamobjbackend.entities.Account;

@Repository
public interface AccountRepository extends CrudRepository<Account,String>
{
}
