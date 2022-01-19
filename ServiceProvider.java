package edu.yu.cs.intro.orderManagement;

import java.util.Set;
import java.util.HashSet;

/**
 * 1) has a Set of services that it can provide
 * 2) can only work on one order at a time - once assigned to a customer, can't take another assignment until 3 other orders have been placed with the order management system
 * 3) is uniquely identified by its ID
 */
public class ServiceProvider implements Comparable<ServiceProvider>{
    /**
     *
     * @param name
     * @param id unique id of the ServiceProvider
     * @param services set of services this provider can provide
     */
    private String name;
    private int id;
    private Set<Service> services;
    private int count;
    private boolean currentlyAssigned;

    public ServiceProvider(String name, int id, Set<Service> services){
        this.name = name;
        this.id = id;
        this.services = new HashSet<Service>(services);
    }

    public String getName(){
        return this.name;
    }

    public int getId(){
        return this.id;
    }

    /**
     * Assign this provider to a customer. Record the fact that he is busy.
     * @throws IllegalStateException if the provider is currently assigned to a job
     */
    protected void assignToCustomer(){
        if(currentlyAssigned){
            throw new IllegalStateException();
        }
        currentlyAssigned = true;
        
    }

    /**
     * Free this provider up - is no longer assigned to a customer
     * @throws IllegalStateException if the provider is NOT currently assigned to a job
     */
    protected void endCustomerEngagement(){
        if(!currentlyAssigned){
            throw new IllegalStateException();
        }
        currentlyAssigned = false;
    }

    /**
     * @param s add the given service to the set of services this provider can provide
     * @return true if it was added, false if not
     */
    protected boolean addService(Service s){

        return services.add(s);
        
    }

    /**
     * @param s remove the given service from the set of services this provider can provide
     * @return true if it was removed, false if not
     */
    protected boolean removeService(Service s){
        return services.remove(s);
    

    }

    /**
     *
     * @return a COPY of the set of services. MUST NOT return the Set instance itself, since that would allow a caller to then add/remove services to/from the set
     */
    public Set<Service> getServices(){

        Set<Service> copy = new HashSet<>();
        copy.addAll(services);
        return copy;
        
    }
    protected boolean isAssigned(){
        return currentlyAssigned;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }

        ServiceProvider otherServices = (ServiceProvider)o;
        return (this.id == otherServices.id);
        
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public int compareTo(ServiceProvider other) {
        if(this.id > other.id){
            return 1;
        }else if(this.id < other.id){
            return -1;
        }else{
            return 0;
        }
    }
    protected void updateCount(){
        if(currentlyAssigned){
           count++;
           if(count == 4){
                count = 0;
                endCustomerEngagement();
           }
        }
       

    }
}