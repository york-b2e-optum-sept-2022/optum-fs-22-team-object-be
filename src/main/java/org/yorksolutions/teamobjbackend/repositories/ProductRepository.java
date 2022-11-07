package org.yorksolutions.teamobjbackend.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yorksolutions.teamobjbackend.entities.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product,String>
{
}
