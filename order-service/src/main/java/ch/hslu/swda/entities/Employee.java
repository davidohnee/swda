package ch.hslu.swda.entities;

public final class Employee extends Person {
    private final Role role;

    public Employee(long id, String firstName, String lastName, Role role) {
        super(id, firstName, lastName);
        this.role = role;
    }

    public Role getRole() {
        return this.role;
    }
}
