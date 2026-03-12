package org.eidd.gl.projet_genieLogiciel.metier;

public class Project {

    private static int counter = 0;
    private final int id;
    private final String name;

    public Project(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nom du projet obligatoire");
        }
        this.id = ++counter;
        this.name = name;
    }

    
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
