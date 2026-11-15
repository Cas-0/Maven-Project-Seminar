package com.packtpub.service;

import java.util.List;
import com.packtpub.model.Product;

public interface ProductService {
	
	List<Product> getAllProducts();
	
	Product getProduct(String prodnr);
	
	void createProduct(String prodnr, String prodname, String prodtype, Integer availableQuantity);
	
	void updateProduct(String prodnr, String prodname, String prodtype, Integer availableQuantity);
	
	void deleteProduct(String prodnr);
}
