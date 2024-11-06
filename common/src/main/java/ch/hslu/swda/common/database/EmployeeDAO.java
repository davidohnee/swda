package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.Employee;

import com.mongodb.client.MongoDatabase;

public class EmployeeDAO extends GenericDAO<Employee> {
    public EmployeeDAO(MongoDatabase database) {
        super(database, "employees", Employee.class);
    }
}