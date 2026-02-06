package org.eidd.gl.projet_genieLogiciel.metier;

public class User {

    private static int counter = 0;
    private final int id;
    private final String name;
    private final String email;

    public User(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nom obligatoire");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email obligatoire");
        }
        this.id = ++counter;
        this.name = name;
        this.email = email;
    }

    
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
