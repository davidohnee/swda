package ch.hslu.swda.db;

import ch.hslu.swda.common.entities.Address;
import ch.hslu.swda.common.entities.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CustomerDBQuery {
    public static final Logger LOG = LoggerFactory.getLogger(CustomerDBQuery.class);
    private final DBConnector db;

    public CustomerDBQuery(DBConnector db) {
        this.db = db;
    }

    public ArrayList<Customer> getCustomerById(long id) {
        ArrayList<Customer> arr = new ArrayList<>();
        Customer dummy = new Customer(123, "Wayne", "Daniel", new Address("Muster Strasse", "20a", "Luzern", 6014));
        arr.add(dummy);
        return arr;
    }

    public ArrayList<Customer> getAllCustomer() {
        ArrayList<Customer> arr = new ArrayList<>();
        Customer dummy = new Customer(123, "Jack", "Daniel", new Address("Muster Strasse", "20a", "Luzern", 6014));
        arr.add(dummy);
        Customer dummy2 = new Customer(32, "Chris", "Mastree", new Address("Test Strasse", "14b", "Zug", 6300));
        arr.add(dummy2);
        return arr;
    }

}
