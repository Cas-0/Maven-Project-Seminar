package com.packtpub.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.packtpub.model.Product;

public interface ProductRepository extends JpaRepository<Product, String>{
	boolean existsByProdname(String prodname);
}
