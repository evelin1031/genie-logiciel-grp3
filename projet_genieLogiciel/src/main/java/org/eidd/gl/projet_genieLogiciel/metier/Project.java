package org.eidd.gl.projet_genieLogiciel.metier;

import java.util.*;

public class Project {

    private static int counter = 0;
    private int id;
    private String name;
    private String description;
    private List<Task> tasks;

    
    // Constructeur, vérifie que les champs soient valides puis instancie l'objet
    public Project(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nom du projet obligatoire");
        }
        this.id = ++counter;
        this.name = name;
        this.description = description;
    }
    
    //Constructeur pour json
    public Project() {
        this.tasks = new ArrayList<>();
    }
    
    // Getteurs
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getDescription() {
    	return description;
    }
    
    public List<Task> getTasks(){
    	return tasks;
    }
    
    // Setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
    
    //Methodes
    public void addTask(Task task) {
    	tasks.add(task);
    }
    
}
