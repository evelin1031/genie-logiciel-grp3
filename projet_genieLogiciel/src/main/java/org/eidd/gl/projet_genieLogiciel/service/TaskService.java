package org.eidd.gl.projet_genieLogiciel.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.eidd.gl.projet_genieLogiciel.metier.Priority;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.metier.TaskStatus;
import org.eidd.gl.projet_genieLogiciel.metier.User;
import org.eidd.gl.projet_genieLogiciel.persistance.TaskRepository;

public class TaskService {
    // gere les actions simples sur les taches

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(Task task) {
        taskRepository.save(task);
    }

    // construit une tache a partir des choix de la cli
    public Task createTask(
            String title,
            String description,
            String priorityValue,
            String statusValue,
            Date deadline,
            User creator,
            User assignedUser
    ) {
        Priority priority = "1".equals(priorityValue)
                ? Priority.LOW
                : "3".equals(priorityValue) ? Priority.HIGH : Priority.MEDIUM;
        Task task = new Task(title, description, priority, deadline, creator);
        task.setCreatedBy(creator);
        if ("2".equals(statusValue)) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        } else if ("3".equals(statusValue)) {
            task.setStatus(TaskStatus.DONE);
        }
        task.setAssignedUser(assignedUser);
        return task;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(int id) {
        return taskRepository.findById(id);
    }

    // convertit une saisie texte en recherche d identifiant
    public Optional<Task> getTaskByInput(String value) {
        try {
            return taskRepository.findById(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public void deleteTask(int id) {
        taskRepository.delete(id);
    }

    public void updateTaskStatus(int id, TaskStatus status) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            task.get().setStatus(status);
        }
    }

    // met a jour le statut depuis le choix numerique
    public void updateTaskStatusFromChoice(int id, String value) {
        if ("1".equals(value)) {
            updateTaskStatus(id, TaskStatus.TODO);
        } else if ("2".equals(value)) {
            updateTaskStatus(id, TaskStatus.IN_PROGRESS);
        } else if ("3".equals(value)) {
            updateTaskStatus(id, TaskStatus.DONE);
        }
    }

    public void updateTaskPriority(int id, Priority priority) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            task.get().setPriority(priority);
        }
    }

    // met a jour la priorite depuis le choix numerique
    public void updateTaskPriorityFromChoice(int id, String value) {
        if ("1".equals(value)) {
            updateTaskPriority(id, Priority.LOW);
        } else if ("2".equals(value)) {
            updateTaskPriority(id, Priority.MEDIUM);
        } else if ("3".equals(value)) {
            updateTaskPriority(id, Priority.HIGH);
        }
    }

    public void editTask(int id, String title, String description) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            task.get().updateTitle(title);
            task.get().updateDescription(description);
        }
    }

    public void updateTaskDeadline(int id, java.util.Date deadline) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            task.get().setDeadline(deadline);
        }
    }

    public void updateTaskAssignee(int id, User user) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            task.get().setAssignedUser(user);
        }
    }
}
