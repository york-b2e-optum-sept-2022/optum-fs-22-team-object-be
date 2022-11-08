package org.yorksolutions.teamobjbackend.repositories;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yorksolutions.teamobjbackend.embeddables.Coupon;
import org.yorksolutions.teamobjbackend.entities.Product;

import java.util.Set;

@Repository
public interface ProductRepository extends CrudRepository<Product,String>
{
    Iterable<Product> findAllByProductIDIn(Iterable<String> productIDs);

    @Query(value = "SELECT * FROM product WHERE productid IN (SELECT product_productid FROM product_coupon_list WHERE code = 'Test Coupon Code')", nativeQuery = true)
    Iterable<Product> findAllWithCouponCode(@Param("code") String code);

    Iterable<Product> findAllByDiscontinuedIsFalseAndCategories(String category);

}
