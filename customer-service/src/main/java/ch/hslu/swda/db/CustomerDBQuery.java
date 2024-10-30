package ch.hslu.swda.db;

import ch.hslu.swda.entities.Address;
import ch.hslu.swda.entities.ContactInfo;
import ch.hslu.swda.entities.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerDBQuery {
    public static final Logger LOG = LoggerFactory.getLogger(CustomerDBQuery.class);
    private final DBConnector db;

    //dummy db
    private final List<Customer> dummyDb = new ArrayList<>();

    public CustomerDBQuery(DBConnector db) {
        this.db = db;
        dummyDb.addAll(List.of(
                new Customer(UUID.fromString("3759e709-c40c-4592-a978-35c6a6b338b2"), "Mick", "King", new Address("Muster Strasse", "20a", "Luzern", 6014), new ContactInfo("mail.me@gmail.com", "+41 76 123 45 67")),
                new Customer(UUID.fromString("0a8a30d1-ecac-4e03-9419-b04158bc7178"), "Jack", "Daniel", new Address("Zentral Strasse", "13c", "Luzern", 6004), new ContactInfo("jack.daniel@tennessee.us", "+1 122 122 12 12")),
                new Customer(UUID.fromString("d96933e4-9fcd-45f4-b3ac-e99e26ab3789"), "Chris", "Mastree", new Address("Zuger Strasse", "111", "Zug", 6601), new ContactInfo("chris.mastree@santa.com", "+41 56 123 12 34")),
                new Customer(UUID.fromString("b5e798d9-fcc6-4200-8073-58ae0895acd4"), "Lea", "Fischer", new Address("Zellweg", "12p", "Luzern", 6014), new ContactInfo("lea.fischer@bluewin.ch", "+41 79 678 87 45"))
        ));
    }

    public Customer getCustomerById(UUID id) {
        for (Customer cust : dummyDb) {
            if (cust.getId().equals(id)) {
                return cust;
            }
        }
        // I don't know this is a little bit sketchy
        return null;
    }

    public List<Customer> getAllCustomer() {
        return dummyDb;
    }

}
