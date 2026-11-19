package com.packtpub.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.packtpub.model.Product;
import com.packtpub.model.repository.ProductRepository;

//@Service registers this class with Spring so it can be injected into the controller
@Service
public class ProductServiceImpl implements ProductService{
	
	// The repository runs the actual database queries
	private final ProductRepository productRepository;
	
	// Spring passes in the repository when it creates this service
	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	@Override
	public List<Product> getAllProducts() {
		// Gets all rows, sorted by primary keys
		return productRepository.findAll(Sort.by("prodnr"));
	}

	@Override
	public Product getProduct(String prodnr) {
		// Return the product if it exists
		return productRepository.findById(prodnr)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + prodnr + " not found"));
	}

	@Override
	public void createProduct(String prodnr, String prodname, String prodtype, Integer availableQuantity) {
		// respond with a 409 if the primary key is taken; save() won't overwrite
		if (productRepository.existsById(prodnr)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Product " + prodnr + " already exists");
		}
		// respond with a 409 if the primary key is taken; save() won't overwrite
		if (productRepository.existsByProdname(prodname)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Product name " + prodname + " already exists");	
		}
		//insert a new row if both checks passed
		productRepository.save(new Product(prodnr, prodname, prodtype, availableQuantity));
	}

	@Override
	public void updateProduct(String prodnr, String prodname, String prodtype, Integer availableQuantity) {
		//load the row and check to see if it doesn't exist
		Product product = productRepository.findById(prodnr)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "product " + prodnr + " not found"));
		//make sure no other product has the name
		if (!product.getProdname().equals(prodname) && productRepository.existsByProdname(prodname)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Product name " + prodname + " already exist");
		}
		
		product.setProdnr(prodnr);
		product.setProdname(prodname);
		product.setProdtype(prodtype);
		product.setAvailableQuantity(availableQuantity);
		//save updated product to Database
		productRepository.save(product);
	}

	@Override
	public void deleteProduct(String prodnr) {
		// check to see is it exists
		if (!productRepository.existsById(prodnr))	{
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + prodnr + " not found");
		}
		//remove product
		productRepository.deleteById(prodnr);

	}

}
