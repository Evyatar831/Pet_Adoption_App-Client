package model;

import java.io.Serializable;
import java.util.UUID;


public class Pet implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String species;
    private String breed;
    private int age;
    private String gender;
    private String status; // "Available", "Pending", "Adopted"

    public Pet() {
        this.id = UUID.randomUUID().toString();
        this.status = "Available"; // Default status for new pets
    }

    public Pet(String id, String name, String species, String breed, int age, String gender) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.status = "Available";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isAvailable() {
        return "Available".equals(status);
    }

    @Override
    public String toString() {
        return name + " (" + species + ", " + breed + ")";
    }
}