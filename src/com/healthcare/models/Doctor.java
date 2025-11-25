package com.healthcare.models;

/**
 * Doctor model extending User.
 * Corresponds to:
 *  - users table (id, username, password, role='DOCTOR')
 *  - doctors table (name, specialization)
 */
public class Doctor extends User {

    private String name;
    private String specialization;

    public Doctor() {
        // Ensure role is set correctly for all Doctor objects
        setRole("DOCTOR");
    }

    public Doctor(int id, String username, String password,
                  String name, String specialization) {
        super(id, username, password, "DOCTOR");
        this.name = name;
        this.specialization = specialization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", name='" + name + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
