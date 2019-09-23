package com.ckelly.vendingmachine.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Slot {

    @Id
    @GeneratedValue
    private Long id;
    private String productName;
    private BigDecimal price;
    private Integer currentQuantity;
    private Integer maxQuantity;
    
    public Slot(Long id, String productName, BigDecimal price, Integer currentQuantity, Integer maxQuantity) {
        this.id = id;
    	this.productName = productName;
        this.price = price;
        this.currentQuantity = currentQuantity;
        this.maxQuantity = maxQuantity;
    }
    
    public Slot() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Integer currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    @Override
    public String toString() {
        return "Id: " + id + " |Product Name: " + productName +
            " |Price: " + price.toString() + " |Number Remaining: " + currentQuantity;
    }
}
