package org.eidd.gl.projet_genieLogiciel.metier;

public class Task {

    private static int counter = 0;
    private final int id;
    private final String title;
    private TaskStatus status;
    private Priority priority;
    private User assignedUser;


    public Task(String title, Priority priority) {
    	
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Titre obligatoire");
        }

        if (priority == null) {
            throw new IllegalArgumentException("Priority obligatoire");
        }

        this.id = ++counter;
        this.title = title;
        this.priority = priority;
        this.status = TaskStatus.TODO;
    }

    
    
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }
}
