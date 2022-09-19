package com.magnit.flux.common.repository;

import com.magnit.flux.model.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {

}
