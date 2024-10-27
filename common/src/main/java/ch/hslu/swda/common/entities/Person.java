package ch.hslu.swda.common.entities;

import java.util.Objects;

public class Person {
    private final long id;
    private final String firstName;
    private final String lastName;

    public Person(long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof Person person)
                &&
                (person.id == this.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person [" +
                "id='" + this.id + '\'' +
                ", first_name='" + this.firstName + '\'' +
                ", last_name='" + this.lastName + '\'' +
                ']';
    }
}
