package com.packtpub.restapp;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.packtpub.service.ProductService;
import com.packtpub.model.Product;


// makes this class handle HTTP requests and return JSON;
@RestController
//sends every URL starting with /product here
@RequestMapping("/product")
public class ProductController {
	
	//injects the service that holds the rules
	@Autowired
	ProductService productService;
	
	// GET returns all products as a JSON array
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Product> getAllProducts(){
		return productService.getAllProducts();
	}
	
	// GET returns one product, if it exists
	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Product getProduct(@PathVariable("id") String id) {
		return productService.getProduct(id);
	}
	
	// POST creates a product;
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.POST)
	public Map<String, Object> createProduct(
		@RequestParam(value="prodnr") String prodnr,
		@RequestParam(value="prodname") String prodname,
		@RequestParam(value="prodtype", required=false) String prodtype,
		@RequestParam(value="availableQuantity", required=false) Integer availableQuantity
		){
		Map<String, Object> map = new LinkedHashMap<>();
		validateProduct(prodnr, prodname, prodtype);
		// Pass the values to the service, checks for duplicates, inserts the row
		productService.createProduct(prodnr, prodname, prodtype, availableQuantity);
		// Build the JSON reply
		map.put("result", "added");
		return map;
	}
	
	// PUT updates an existing product;
	@ResponseBody
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public Map<String, Object> updateProduct(
		@RequestParam(value="prodnr") String prodnr,
		@RequestParam(value="prodname") String prodname,
		@RequestParam(value="prodtype", required=false) String prodtype,
		@RequestParam(value="availableQuantity", required=false) Integer availableQuantity
		){
		Map<String, Object> map = new LinkedHashMap<>();
		validateProduct(prodnr, prodname, prodtype);
		// Pass the values to the service, finds the row, saves the changes
		productService.updateProduct(prodnr, prodname, prodtype, availableQuantity);
		// Build the JSON reply
		map.put("result" , "Updated");
		return map;
	}
	
	// DELETE removes a product, if it exists
	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Map<String, Object> deleteProduct(
		@PathVariable("id") String prodnr
		){
		Map<String, Object> map = new LinkedHashMap<>();
		// The service checks that the product exists, then deletes it
		productService.deleteProduct(prodnr);
		// Build the JSON reply
		map.put("result" , "Deleted");
		return map;
	}
	
	// Checks the request values against the table definition so the client gets a clearer error
	private void validateProduct(String prodnr, String prodname, String prodtype) {
		// PRODNR is char(6) NOT NULL: required, at most 6 characters
		if (prodnr.isBlank() || prodnr.length() > 6) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "prodnr must be 1 to 6 characters");
		}
		// PRODNAME is varchar(60) NOT NULL: required, at most 60 characters
		if (prodname.isBlank() || prodname.length() > 60) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "prodname must be 1 to 60 characters");
		}
		// PRODTYPE is varchar(15) and optional: only checked when it's provided
		if (prodtype != null && prodtype.length() > 15) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "prodtype must be at most 15 characters");
		}
	}
	
}
