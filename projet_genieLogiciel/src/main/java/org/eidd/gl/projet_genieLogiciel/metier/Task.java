package org.eidd.gl.projet_genieLogiciel.metier;

import java.util.Date;

public class Task {

    private static int counter = 0;
    private final int id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private Date deadline;
    private Date createdAt;
    private User assignedUser;

    public Task(String title, String description, Priority priority, Date deadline, User assignedUser) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Titre obligatoire");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority obligatoire");
        }

        this.id = ++counter;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.assignedUser = assignedUser;
        this.status = TaskStatus.TODO;
        this.createdAt = new Date();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public Date getDeadline() {
        return deadline;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void updateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Titre obligatoire");
        }
        this.title = title;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status obligatoire");
        }
        this.status = status;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setAssignedUser(User user) {
        this.assignedUser = user;
    }
}
