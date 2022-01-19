package edu.yu.cs.intro.orderManagement;

import java.util.*;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;


/**
 * Takes orders, manages the warehouse as well as service providers
 */
public class OrderManagementSystem {
    
    int defaultProductStockLevel;
    Set<Product> products;
    Set<ServiceProvider> serviceProviders;
    Warehouse warehouse;
    
    Map<Service, Set<ServiceProvider>> serviceToServiceProviders;
    Set<Item> discontinueItem;


    /**
     * Creates a new Warehouse instance and calls the other constructor
     *
     * @param products
     * @param defaultProductStockLevel
     * @param serviceProviders
     */
    public OrderManagementSystem(Set<Product> products, int defaultProductStockLevel, Set<ServiceProvider> serviceProviders) {
        this(products,defaultProductStockLevel,serviceProviders,new Warehouse());

    }

    /**
     * 1) populate the warehouse with the products.
     * 2) retrieve set of services provided by the ServiceProviders, to save it as the set of services the business can provide
     * 3) create map of services to the List of service providers that provide them
     *
     * @param products                 - set of products to populate the warehouse with
     * @param defaultProductStockLevel - the default number of products to stock for any product
     * @param serviceProviders         - set of service providers and the services they provide, to make up the services arm of the business
     * @param warehouse                - the warehouse that we will store our products in
     */
    public OrderManagementSystem(Set<Product> products, int defaultProductStockLevel, Set<ServiceProvider> serviceProviders, Warehouse warehouse) {
        this.defaultProductStockLevel = defaultProductStockLevel;
        this.products = products;
        this.serviceProviders = serviceProviders;
        this.warehouse = warehouse;
        this.serviceToServiceProviders = new HashMap<>();
        this.discontinueItem = new HashSet<>();

        for(Product product : products){
            warehouse.addNewProductToWarehouse(product, defaultProductStockLevel);
        }
        for(ServiceProvider serviceProvider : serviceProviders){
        
                addServiceProvider(serviceProvider);
                
        }
        
        
    }


    /**
     * Accept an order:
     * 1) See if we have ServiceProviders for all Services in the order. If not, reject the order.
     * 2) See if we can fulfill all Items in the order. If so, place the product orders with the warehouse and handle the service orders inside this class
     * 2a) We CAN fulfill a product order if either the warehouse currently has enough quantity in stock OR if the product is NOT on the "do not restock" list.
     *  In the case that the current quantity of a product is < the quantity in the order AND the product is NOT on the "do not restock" list, the order management system should
     *  first instruct the warehouse to restock the item, and then tell the warehouse to fulfill this order.
     * 3) Mark the order as completed
     * 4) Update the busy status of service providers involved...
     * @throws IllegalArgumentException if any part of the order for PRODUCTS can't be fulfilled
     * @throws IllegalStateException if any part of the order for SERVICES can't be fulfilled
     */
    public void placeOrder(Order order) {
        
        Collection<Service> services = new HashSet<>();
        Collection<Product> products = new HashSet<>();
        for(Item item : order.getItems()){
            if(item instanceof Service){
                services.add((Service)item);
            }else{
                products.add((Product)item);
            }
        }
        if(validateProducts(products,order) != 0){
            throw new IllegalArgumentException();
        }

        if(validateServices(services,order) != 0){

            throw new IllegalStateException();
        }



        for(Product product : products){

            if(warehouse.getStockLevel(product.getItemNumber()) < order.getQuantity(product)){
                warehouse.restock(product.getItemNumber(), order.getQuantity(product));

            }
            
            warehouse.fulfill(product.getItemNumber(),order.getQuantity(product));

        }

        for(Service service : services){

            int counter = order.getQuantity(service);
            for(ServiceProvider serviceProvider : serviceToServiceProviders.get(service)){
                if(!serviceProvider.isAssigned()){
                    serviceProvider.assignToCustomer();
                    counter--;
                    if(counter == 0){
                        break;
                    }
                }

            }

        }
        order.setCompleted(true);
        updateServiceProviderCounts();

        
    }


    /**
     * Validate that all the services being ordered can be provided. Make sure to check how many instances of a given service are being requested in the order, and see if we have enough providers for them.
     * @param services the set of services which are being ordered inside the order
     * @param order the order whose services we are validating
     * @return itemNumber of the first requested service encountered that we either do not have a provider for at all, or for which we do not have an available provider. Return 0 if all services are valid.
     */
    protected int validateServices(Collection<Service> services, Order order) {
        Set<ServiceProvider> pretendAssign = new HashSet<>();
        
        for(Service service : services){
            if(!serviceToServiceProviders.containsKey(service)){
                return service.getItemNumber();
            }
            int counter = order.getQuantity(service);
            for(ServiceProvider serviceProvider : serviceToServiceProviders.get(service)){
                if(!serviceProvider.isAssigned() && !pretendAssign.contains(serviceProvider)){
                    pretendAssign.add(serviceProvider);
                    counter--;
                    if(counter == 0){
                        break;
                    }
                }

            }
            if(counter > 0){
                return service.getItemNumber();
            }
        }
        return 0;
    }

    /**
     * validate that the requested quantity of products can be fulfilled
     * @param products being ordered in this order
     * @param order the order whose products we are validating
     * @return itemNumber of product which is either not in the catalog or which we have insufficient quantity of. Return 0 if we can fulfill.
     */
    protected int validateProducts(Collection<Product> products, Order order) {

        for(Product product : products){

            if(order.getQuantity(product) > warehouse.getStockLevel(product.getItemNumber()) && !warehouse.isRestockable(product.getItemNumber())){
                return product.getItemNumber();

            }

        }
        return 0;
        
    }

    /**
     * Adds new Products to the set of products that the warehouse can ship/fulfill
     * @param products the products to add to the warehouse
     * @return set of products that were actually added (don't include any products that were already in the warehouse before this was called!)
     */
    protected Set<Product> addNewProducts(Collection<Product> products) {
        
        Set<Product> completed = new HashSet<>();
        for(Product product : products){
            if(discontinueItem.contains(product)){
                continue;
            }
            try{
                warehouse.addNewProductToWarehouse(product,defaultProductStockLevel);
                completed.add(product);
            }catch(IllegalArgumentException e){

            }
            
        }
        return completed;
    }

    /**
     * Adds an additional ServiceProvider to the system. Update all relevant data about which Services are offered and which ServiceProviders provide which services are offered and which ServiceProviders provide
which services
     * @param provider the provider to add
     */
    protected void addServiceProvider(ServiceProvider serviceProvider) {
        for(Service service : serviceProvider.getServices()){
            
            if(discontinueItem.contains(service)){
                continue;
            }
                
                if(serviceToServiceProviders.containsKey(service)){
                    Set<ServiceProvider> value = serviceToServiceProviders.get(service);
                    value.add(serviceProvider);

                }else{
                    Set<ServiceProvider> value = new HashSet<>();
                    value.add(serviceProvider);
                    serviceToServiceProviders.put(service,value);
                }
            }
            this.serviceProviders.add(serviceProvider);

    }

    /**
     *
     * @return get the set of all the products offered/sold by this business
     */
    public Set<Product> getProductCatalog() {
        return warehouse.getAllProductsInCatalog();
    }

    /**
     * @return get the set of all the Services offered/sold by this business
     */
    public Set<Service> getOfferedServices() {
        return serviceToServiceProviders.keySet();
    }

    /**
     * Discontinue Item, i.e. stop selling a Service or Product.
     * Also prevent the Item from being added in the future.
     * If it's a Service - remove it from the set of provided services.
     * If it's a Product - still sell whatever instances of this Product are in stock, but do not restock it.
     * @param item the item to discontinue see {@link Item}
     */
    protected void discontinueItem(Item item) {
        discontinueItem.add(item);
        if(item instanceof Service){
            serviceToServiceProviders.remove(item);
        }else{
            warehouse.doNotRestock(item.getItemNumber());
        }
    }

    /**
     * Set the default product stock level for the given product
     * @param prod
     * @param level
     */
    protected void setDefaultProductStockLevel(Product prod, int level) {
        warehouse.setDefaultStockLevel(prod.getItemNumber(),level);
    }
    private void updateServiceProviderCounts(){
        for(ServiceProvider serviceProvider : serviceProviders){
            serviceProvider.updateCount();
        }
    }
}