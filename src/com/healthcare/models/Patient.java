package com.healthcare.models;

/**
 * Patient model extending User.
 * Corresponds to:
 *  - users table (id, username, password, role='PATIENT')
 *  - patients table (name, contact)
 */
public class Patient extends User {

    private String name;
    private String contact;

    public Patient() {
        // Make sure all Patient objects have correct role
        setRole("PATIENT");
    }

    public Patient(int id, String username, String password,
                   String name, String contact) {
        // User should have a constructor: User(int id, String username, String password, String role)
        super(id, username, password, "PATIENT");
        this.name = name;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
