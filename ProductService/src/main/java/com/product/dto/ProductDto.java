package com.product.dto;

public class ProductDto {

    private int id;
    private String name;
    private String company;
    private int quantity;
    private double price;
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", company=" + company + ", quantity=" + quantity + ", price="
				+ price + "]";
	}
	public ProductDto(int id, String name, String company, int quantity, double price) {
		super();
		this.id = id;
		this.name = name;
		this.company = company;
		this.quantity = quantity;
		this.price = price;
	}
	public ProductDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	    
}
