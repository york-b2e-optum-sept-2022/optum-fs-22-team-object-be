package org.yorksolutions.teamobjbackend.repositories;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yorksolutions.teamobjbackend.entities.Product;


/**
 * Rrepository with a few custom operations for interacting with product database
 * @author Max Feige
 */
@Repository
public interface ProductRepository extends CrudRepository<Product,String>
{
    Iterable<Product> findAllByProductIDIn(Iterable<String> productIDs);

    /**
     * Runs the native query <code>SELECT * FROM product WHERE productid IN (SELECT product_productid FROM product_coupon_list WHERE code = :code)</code>
     * @param code the coupon code to access
     * @return all products with ag iven coupon code
     */
    @Query(value = "SELECT * FROM product WHERE productid IN (SELECT product_productid FROM product_coupon_list WHERE code = :code)", nativeQuery = true)
    Iterable<Product> findAllWithCouponCode(@Param("code") String code);

    Iterable<Product> findAllByDiscontinuedIsFalseAndCategories(String category);

}
