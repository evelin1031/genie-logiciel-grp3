package org.eidd.gl.projet_genieLogiciel;

import org.eidd.gl.projet_genieLogiciel.metier.Priority;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.metier.TaskStatus;
import org.eidd.gl.projet_genieLogiciel.metier.User;
import org.eidd.gl.projet_genieLogiciel.persistance.InMemoryTaskRepository;
import org.eidd.gl.projet_genieLogiciel.persistance.TaskRepository;
import org.eidd.gl.projet_genieLogiciel.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    private TaskService taskService;
    private TaskRepository taskRepository;
    private User testUser;

    @BeforeEach
    public void setUp() {
        taskRepository = new InMemoryTaskRepository();
        taskService = new TaskService(taskRepository);
        testUser = new User("John Doe", "john@example.com");
    }

    @Test
    public void testCreateTask() {
        Task task = new Task("Test Task", "Description", Priority.HIGH, new Date(), testUser);
        taskService.createTask(task);

        List<Task> tasks = taskService.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task("Task 1", "Description 1", Priority.HIGH, new Date(), testUser);
        Task task2 = new Task("Task 2", "Description 2", Priority.LOW, new Date(), testUser);

        taskService.createTask(task1);
        taskService.createTask(task2);

        List<Task> tasks = taskService.getAllTasks();
        assertEquals(2, tasks.size());
    }

    @Test
    public void testGetTaskById() {
        Task task = new Task("Test Task", "Description", Priority.MEDIUM, new Date(), testUser);
        taskService.createTask(task);

        Optional<Task> retrievedTask = taskService.getTaskById(task.getId());
        assertTrue(retrievedTask.isPresent());
        assertEquals("Test Task", retrievedTask.get().getTitle());
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task("Test Task", "Description", Priority.HIGH, new Date(), testUser);
        taskService.createTask(task);

        taskService.deleteTask(task.getId());

        List<Task> tasks = taskService.getAllTasks();
        assertEquals(0, tasks.size());
    }

    @Test
    public void testUpdateTaskStatus() {
        Task task = new Task("Test Task", "Description", Priority.HIGH, new Date(), testUser);
        taskService.createTask(task);

        assertEquals(TaskStatus.TODO, task.getStatus());

        taskService.updateTaskStatus(task.getId(), TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());

        taskService.updateTaskStatus(task.getId(), TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, task.getStatus());
    }
}
