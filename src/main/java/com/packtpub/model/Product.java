package com.packtpub.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="product")
public class Product {
	@Id
	@JdbcTypeCode(SqlTypes.CHAR)
	private String prodnr;
	private String prodname;
	private String prodtype;
	private Integer availableQuantity;
	
	public Product() {
		
	}
	public Product(String prodnr, String prodname, String prodtype, Integer availableQuantity) {
		this.prodnr = prodnr;
		this.prodname = prodname;
		this.prodtype = prodtype;
		this.availableQuantity = availableQuantity;
	}
	public String getProdnr() {
		return prodnr == null ? null : prodnr.trim();
	}
	public void setProdnr(String prodnr) {
		this.prodnr = prodnr;
	}
	public String getProdname() {
		return prodname;
	}
	public void setProdname(String prodname) {
		this.prodname = prodname;
	}
	public String getProdtype() {
		return prodtype;
	}
	public void setProdtype(String prodtype) {
		this.prodtype = prodtype;
	}
	public Integer getAvailableQuantity() {
		return availableQuantity;
	}
	public void setAvailableQuantity(Integer availableQuantity) {
		this.availableQuantity = availableQuantity;
	}
	

}
