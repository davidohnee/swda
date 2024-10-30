package ch.hslu.swda.entities;

import java.util.Objects;
import java.util.UUID;

public class Person {
    private final UUID id;
    private final String firstName;
    private final String familyName;

    public Person(UUID id, String firstName, String familyName) {
        this.id = id;
        this.firstName = firstName;
        this.familyName = familyName;
    }

    public UUID getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getFamilyName() {
        return this.familyName;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof Person person)
                &&
                object.equals(this);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person [" +
                "id='" + this.id.toString() + '\'' +
                ", first_name='" + this.firstName + '\'' +
                ", family_name='" + this.familyName + '\'' +
                ']';
    }
}
