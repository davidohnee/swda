package ch.hslu.swda.common.database;

import ch.hslu.swda.common.entities.Employee;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import java.util.UUID;

public class EmployeeDAO extends GenericDAO<Employee> {
    public EmployeeDAO(MongoDatabase database) {
        super(database, "employees", Employee.class);
    }

    public Employee findByUUID(UUID uuid) {
        return collection.find(eq("id", uuid)).first();
    }
}