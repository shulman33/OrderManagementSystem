package edu.yu.cs.intro.orderManagement;

import java.util.*;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Stocks products, fulfills product orders, manages stock of products.
 */
public class Warehouse {
     private Map<Integer,Integer> desiredStockLevel;
     private Set<Product> allProducts;
     private Set<Integer> doNotRestock;
     private Map<Integer,Integer> currentStockLevel;

    /**
     * create a warehouse, initialize all the instance variables
     */
    protected Warehouse(){
        this.desiredStockLevel = new HashMap<>();
        this.allProducts = new HashSet<>();
        this.doNotRestock = new HashSet<>();
        this.currentStockLevel = new HashMap<>();

    }

    /**
     * @return all unique Products stocked in the warehouse
     */
    protected Set<Product> getAllProductsInCatalog(){
        return this.allProducts;
    }

    /**
     * Add a product to the warehouse, at the given stock level.
     * @param product
     * @param desiredStockLevel the number to stock initially, and also to restock to when subsequently restocked
     * @throws IllegalArgumentException if the product is in the "do not restock" set, or if the product is already in the warehouse
     */
    protected void addNewProductToWarehouse(Product product, int desiredStockLevel){
        if(allProducts.contains(product) || doNotRestock.contains(product.getItemNumber())){
            throw new IllegalArgumentException();
        }else{
            this.desiredStockLevel.put(product.getItemNumber(), desiredStockLevel);
            this.currentStockLevel.put(product.getItemNumber(), desiredStockLevel);
            this.allProducts.add(product);

        }
    }

    /**
     * If the actual stock is already >= the minimum, do nothing. Otherwise, raise it to the minimum level.
     * 
     * 
     * Otherwise, raise it to minimum OR
the default stock level, whichever is greater question
     * @param productNumber
     * @param minimum
     * @throws IllegalArgumentException if the product is in the "do not restock" set, or if it is not in the catalog
     */
    protected void restock(int productNumber, int minimum){
        Set<Integer> productNumbers = new HashSet<>();
        for(Product product : allProducts){
            productNumbers.add(product.getItemNumber());
        }
        if(!productNumbers.contains(productNumber) || doNotRestock.contains(productNumber)){
            throw new IllegalArgumentException();
        }else{
            if(currentStockLevel.get(productNumber) < minimum){
                if(minimum < desiredStockLevel.get(productNumber)){
                    currentStockLevel.put(productNumber, desiredStockLevel.get(productNumber));
                }else{
                    currentStockLevel.put(productNumber, minimum);
                }
            }
        }
        
    }

    /**
     * Set the new default stock level for the given product
     * @param productNumber
     * @param quantity
     * @return the old default stock level
     * @throws IllegalArgumentException if the product is in the "do not restock" set, or if it is not in the catalog
     */
    protected int setDefaultStockLevel(int productNumber, int quantity){
        if(!desiredStockLevel.keySet().contains(productNumber) || doNotRestock.contains(productNumber)){
            throw new IllegalArgumentException();
        }
        int oldStockLevel = desiredStockLevel.get(productNumber);
        desiredStockLevel.put(productNumber, quantity);
        return oldStockLevel;
    }

    /**
     * @param productNumber
     * @return how many of the given product we have in stock, or zero if it is not stocked
     */
    protected int getStockLevel(int productNumber){
        
        return currentStockLevel.getOrDefault(productNumber,0);
    }

    /**
     * @param itemNumber
     * @return true if the given item number is in the warehouse's catalog, false if not
     */
    protected boolean isInCatalog(int itemNumber){
        return desiredStockLevel.keySet().contains(itemNumber);
    }

    /**
     *
     * @param itemNumber
     * @return false if it's not in catalog or is in the "do not restock" set. Otherwise true.
     */
    protected boolean isRestockable(int itemNumber){
        return isInCatalog(itemNumber) && !doNotRestock.contains(itemNumber);
    }

    /**
     * add the given product to the "do not restock" set
     * @param productNumber
     * @return the current actual stock level of the product
     */
    protected int doNotRestock(int productNumber){

        this.doNotRestock.add(productNumber);

        return currentStockLevel.get(productNumber);
    }

    /**
     * can the warehouse fulfill an order for the given amount of the given product?
     * @param productNumber
     * @param quantity
     * @return false if the product is not in the catalog or there are fewer than quantity of the products in the catalog. Otherwise true.
     */
    protected boolean canFulfill(int productNumber, int quantity){
        return isInCatalog(productNumber) && currentStockLevel.get(productNumber) >= quantity;
    }

    /**
     * Fulfill an order for the given amount of the given product, i.e. lower the stock levels of the product by the given amount
     * @param productNumber
     * @param quantity
     * @throws IllegalArgumentException if {@link #canFulfill(int, int)} returns false
     */
    protected void fulfill(int productNumber, int quantity){
        if(!canFulfill(productNumber,quantity)){
            throw new IllegalArgumentException();
        }else{
            int currentQuant = currentStockLevel.get(productNumber);
            currentStockLevel.put(productNumber, currentQuant - quantity);
        }
    }
}
