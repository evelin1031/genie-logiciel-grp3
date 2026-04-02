package org.eidd.gl.projet_genieLogiciel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.eidd.gl.projet_genieLogiciel.metier.Priority;
import org.eidd.gl.projet_genieLogiciel.metier.Project;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.metier.TaskStatus;
import org.eidd.gl.projet_genieLogiciel.service.StatisticsService;
import org.junit.jupiter.api.Test;

public class StatisticsServiceTest {

    private final StatisticsService statisticsService = new StatisticsService();

    // Méthode utilitaire pour créer rapidement une tâche avec un statut donné
    private Task createTask(String title, TaskStatus status) {
        Task task = new Task(title, "desc", Priority.MEDIUM, new Date(), null);
        task.setStatus(status);
        return task;
    }

    @Test
    void shouldReturnZeroCompletedTasksWhenProjectIsNull() {
        assertEquals(0, statisticsService.countCompletedTasks(null));
    }

    @Test
    void shouldReturnZeroPendingTasksWhenProjectIsNull() {
        assertEquals(0, statisticsService.countPendingTasks(null));
    }

    @Test
    void shouldReturnZeroCompletionRateWhenProjectIsNull() {
        assertEquals(0.0, statisticsService.calculateCompletionRate(null));
    }

    @Test
    void shouldReturnZeroWhenProjectHasNoTasks() {
        Project project = new Project("Projet test", "Description");

        assertEquals(0, statisticsService.countCompletedTasks(project));
        assertEquals(0, statisticsService.countPendingTasks(project));
        assertEquals(0.0, statisticsService.calculateCompletionRate(project));
    }

    @Test
    void shouldCountCompletedTasksCorrectly() {
        Project project = new Project("Projet test", "Description");

        Task task1 = createTask("Task 1", TaskStatus.DONE);
        Task task2 = createTask("Task 2", TaskStatus.DONE);
        Task task3 = createTask("Task 3", TaskStatus.TODO);

        project.addTask(task1);
        project.addTask(task2);
        project.addTask(task3);

        // Deux tâches sont terminées
        assertEquals(2, statisticsService.countCompletedTasks(project));
    }

    @Test
    void shouldCountPendingTasksCorrectly() {
        Project project = new Project("Projet test", "Description");

        Task task1 = createTask("Task 1", TaskStatus.DONE);
        Task task2 = createTask("Task 2", TaskStatus.TODO);
        Task task3 = createTask("Task 3", TaskStatus.IN_PROGRESS);

        project.addTask(task1);
        project.addTask(task2);
        project.addTask(task3);

        // Deux tâches ne sont pas terminées
        assertEquals(2, statisticsService.countPendingTasks(project));
    }

    @Test
    void shouldCalculateCompletionRateCorrectly() {
        Project project = new Project("Projet test", "Description");

        Task task1 = createTask("Task 1", TaskStatus.DONE);
        Task task2 = createTask("Task 2", TaskStatus.DONE);
        Task task3 = createTask("Task 3", TaskStatus.TODO);
        Task task4 = createTask("Task 4", TaskStatus.IN_PROGRESS);

        project.addTask(task1);
        project.addTask(task2);
        project.addTask(task3);
        project.addTask(task4);

        // 2 tâches terminées sur 4 => 0.5
        assertEquals(0.5, statisticsService.calculateCompletionRate(project));
    }

    @Test
    void shouldIgnoreNullTaskForCompletedTasks() {
        Project project = new Project("Projet test", "Description");

        Task task1 = createTask("Task 1", TaskStatus.DONE);

        project.addTask(task1);
        project.getTasks().add(null);

        // La tâche null ne doit pas être comptée
        assertEquals(1, statisticsService.countCompletedTasks(project));
    }

    @Test
    void shouldIgnoreNullTaskForPendingTasks() {
        Project project = new Project("Projet test", "Description");

        Task task1 = createTask("Task 1", TaskStatus.TODO);

        project.addTask(task1);
        project.getTasks().add(null);

        // La tâche null ne doit pas être comptée
        assertEquals(1, statisticsService.countPendingTasks(project));
    }
}