package org.yorksolutions.teamobjbackend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.yorksolutions.teamobjbackend.entities.ProductOrder;

@Repository
public interface ProductOrderRepository extends CrudRepository<ProductOrder,String>
{
}
