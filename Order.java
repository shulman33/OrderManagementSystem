package edu.yu.cs.intro.orderManagement;

import java.util.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an order placed by a customer. An item in the order can be an instance of either Product or Service
 */
public class Order {

    /**
     * initializes instance variables
     */
    private Map<Item,Integer> orders;
    private boolean completed;

    public Order(){
        this.orders = new HashMap<>();
        this.completed = false;
    }

    /**
     * @return all the items (products and services) in the order
     */
    public Item[] getItems(){
        Set<Item> keys = orders.keySet();
        Item[] array = keys.toArray(new Item[keys.size()]);
        return array;
    }

    /**
     * @param b
     * @return the quantity of the given item ordered in this order. Zero if the item is not in the order.
     */
    public int getQuantity(Item b){
        return orders.getOrDefault(b,0);
    }

    /**
     * Add the given quantity of the given item (product or service) to the order
     * @param item
     * @param quantity
     */
    public void addToOrder(Item item, int quantity){
        orders.put(item,quantity);
    }

    /**
     * Calculate the total price of PRODUCTS in the order. Must multiply each item's price by the quantity.
     * @return the total price of products in this order
     */
    public double getProductsTotalPrice(){
        double totalPrice = 0;
        for(Item item : orders.keySet()){
            if(item instanceof Product){
                totalPrice += item.getPrice() * orders.get(item);

            }
        }
        return totalPrice;

    }

    /**
     * Calculate the total price of the SERVICES in the order. Must multiply each item's price by the quantity.
     * @return the total price of products in this order
     */
    public double getServicesTotalPrice(){
        double totalPrice = 0;
        for(Item item : orders.keySet()){
            if(item instanceof Service){
                totalPrice += item.getPrice() * orders.get(item);

            }
        }
        return totalPrice;
    }

    /**
     * @return has the order been completed by the order management system?
     */
    public boolean isCompleted() {

        return this.completed;

    }

    /**
     * Indicate if the order has been completed by the order management system
     * @param completed
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}