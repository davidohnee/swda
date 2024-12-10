package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.Address;
import ch.hslu.swda.common.entities.ContactInfo;
import ch.hslu.swda.common.entities.Customer;
import ch.hslu.swda.common.entities.Employee;
import ch.hslu.swda.common.entities.Employee.RoleEnum;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.UUID;

public class DataInitializer {
    private final EmployeeDAO employeeDAO;
    private final CustomerDAO customerDAO;

    public DataInitializer(MongoDatabase database) {
        this.employeeDAO = new EmployeeDAO(database);
        this.customerDAO = new CustomerDAO(database);
    }

    public void initializeData() {
        initializeEmployees();
        initializeCustomers();
    }

    private void initializeEmployees() {
        List<Employee> predefinedEmployees = List.of(
            new Employee(UUID.fromString("11111111-1111-1111-1111-111111111111"), "John", "Doe", RoleEnum.SALES),
            new Employee(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Jane", "Smith", RoleEnum.MANAGER),
            new Employee(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Alice", "Johnson", RoleEnum.DATA_TYPIST),
            new Employee(UUID.fromString("44444444-4444-4444-4444-444444444444"), "Bob", "Brown", RoleEnum.SYSTEM_ADMIN)
        );

        for (Employee employee : predefinedEmployees) {
            Employee existingEmployee = employeeDAO.findByUUID(employee.getId());
            if (existingEmployee == null) {
                employeeDAO.create(employee);
            }
        }
    }

    private void initializeCustomers() {
        List<Customer> predefinedCustomers = List.of(
                new Customer(UUID.fromString("3759e709-c40c-4592-a978-35c6a6b338b2"), "Mick", "King", new Address("Muster Strasse", "20a", "6014", "Luzern"), new ContactInfo("mail.me@gmail.com", "+41 76 123 45 67")),
                new Customer(UUID.fromString("0a8a30d1-ecac-4e03-9419-b04158bc7178"), "Jack", "Daniel", new Address("Zentral Strasse", "13c", "6004", "Luzern"), new ContactInfo("jack.daniel@tennessee.us", "+1 122 122 12 12")),
                new Customer(UUID.fromString("d96933e4-9fcd-45f4-b3ac-e99e26ab3789"), "Chris", "Mastree", new Address("Zuger Strasse", "111", "6601", "Zug"), new ContactInfo("chris.mastree@santa.com", "+41 56 123 12 34")),
                new Customer(UUID.fromString("b5e798d9-fcc6-4200-8073-58ae0895acd4"), "Lea", "Fischer", new Address("Zellweg", "12p", "6014", "Luzern"), new ContactInfo("lea.fischer@bluewin.ch", "+41 79 678 87 45"))
        );

        for (Customer customer : predefinedCustomers) {
            Customer existingCustomer = customerDAO.read(customer.getId());
            if (existingCustomer == null) {
                customerDAO.create(customer);
            }
        }
    }
}