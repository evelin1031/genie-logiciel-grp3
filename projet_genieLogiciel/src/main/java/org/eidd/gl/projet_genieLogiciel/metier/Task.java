package org.eidd.gl.projet_genieLogiciel.metier;

public class Task {

    private static int counter = 0;
    private final int id;
    private final String title;
    private TaskStatus status;

    public Task(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Titre obligatoire");
        }
        this.id = ++counter;
        this.title = title;
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
}
