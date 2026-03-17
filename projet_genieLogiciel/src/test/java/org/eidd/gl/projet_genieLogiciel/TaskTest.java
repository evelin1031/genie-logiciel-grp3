package org.eidd.gl.projet_genieLogiciel;

import org.eidd.gl.projet_genieLogiciel.metier.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class TaskTest {

    @Test
    public void testConstructorValid() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        Task task = new Task("Test Task", "Description", Priority.HIGH, deadline, user);

        assertNotNull(task);
        assertEquals("Test Task", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(deadline, task.getDeadline());
        assertEquals(user, task.getAssignedUser());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertNotNull(task.getCreatedAt());
        assertTrue(task.getId() > 0);
    }

    @Test
    public void testConstructorNullTitle() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        assertThrows(IllegalArgumentException.class, () -> {
            new Task(null, "Description", Priority.HIGH, deadline, user);
        });
    }

    @Test
    public void testConstructorBlankTitle() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        assertThrows(IllegalArgumentException.class, () -> {
            new Task("", "Description", Priority.HIGH, deadline, user);
        });
    }

    @Test
    public void testConstructorNullPriority() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        assertThrows(IllegalArgumentException.class, () -> {
            new Task("Test Task", "Description", null, deadline, user);
        });
    }

    @Test
    public void testUpdateTitleValid() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        Task task = new Task("Test Task", "Description", Priority.HIGH, deadline, user);

        task.updateTitle("New Title");
        assertEquals("New Title", task.getTitle());
    }

    @Test
    public void testUpdateTitleNull() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        Task task = new Task("Test Task", "Description", Priority.HIGH, deadline, user);

        assertThrows(IllegalArgumentException.class, () -> {
            task.updateTitle(null);
        });
    }

    @Test
    public void testUpdateTitleBlank() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        Task task = new Task("Test Task", "Description", Priority.HIGH, deadline, user);

        assertThrows(IllegalArgumentException.class, () -> {
            task.updateTitle("");
        });
    }

    @Test
    public void testUpdateDescription() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        Task task = new Task("Test Task", "Description", Priority.HIGH, deadline, user);

        task.updateDescription("New Description");
        assertEquals("New Description", task.getDescription());
    }

    @Test
    public void testSetStatusValid() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        Task task = new Task("Test Task", "Description", Priority.HIGH, deadline, user);

        task.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    public void testSetStatusNull() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        Task task = new Task("Test Task", "Description", Priority.HIGH, deadline, user);

        assertThrows(IllegalArgumentException.class, () -> {
            task.setStatus(null);
        });
    }

    @Test
    public void testSetDeadline() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        Task task = new Task("Test Task", "Description", Priority.HIGH, deadline, user);

        Date newDeadline = new Date(System.currentTimeMillis() + 86400000); // +1 day
        task.setDeadline(newDeadline);
        assertEquals(newDeadline, task.getDeadline());
    }

    @Test
    public void testSetAssignedUser() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        Task task = new Task("Test Task", "Description", Priority.HIGH, deadline, user);

        User newUser = new User("Jane Doe", "jane@example.com");
        task.setAssignedUser(newUser);
        assertEquals(newUser, task.getAssignedUser());
    }

    @Test
    public void testUniqueIds() {
        User user = new User("John Doe", "john@example.com");
        Date deadline = new Date();
        Task task1 = new Task("Task 1", "Desc", Priority.HIGH, deadline, user);
        Task task2 = new Task("Task 2", "Desc", Priority.HIGH, deadline, user);

        assertNotEquals(task1.getId(), task2.getId());
    }
}