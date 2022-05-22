package com.igarashiisrael.dscatalog.repositories;

import com.igarashiisrael.dscatalog.entities.Category;
import com.igarashiisrael.dscatalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
