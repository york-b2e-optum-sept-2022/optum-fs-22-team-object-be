package org.yorksolutions.teamobjbackend.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.entities.Product;

import java.util.Set;

@Repository
public interface ProductRepository extends CrudRepository<Product,String>
{
    public Iterable<Product> findAllByProductIDIn(Iterable<String> productIDs);

    public Iterable<Product> findAllByCouponListContaining(Coupon c);

}
