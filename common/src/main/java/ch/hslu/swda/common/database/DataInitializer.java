package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.Employee;
import ch.hslu.swda.common.entities.Employee.RoleEnum;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import java.util.UUID;

public class DataInitializer {
    private final EmployeeDAO employeeDAO;

    public DataInitializer(MongoDatabase database) {
        this.employeeDAO = new EmployeeDAO(database);
    }

    public void initializeData() {
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
}