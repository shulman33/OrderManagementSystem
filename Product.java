package edu.yu.cs.intro.orderManagement;

/**
 * A "physical" item that is "stocked" in the warehouse.
 */
public class Product implements Item {
    
    private String name;
    private double price;
    private int productID;

    public Product(String name, double price, int productID){

        this.name = name;
        this.price = price;
        this.productID = productID;
    }

    @Override
    public int getItemNumber() {
        return this.productID;
    }

    @Override
    public String getDescription() {
        return this.name;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        
        Product otherProduct = (Product)o;
        return (this.productID == otherProduct.productID);
        
    }

    @Override
    public int hashCode() {
        return this.productID;
    }
}
