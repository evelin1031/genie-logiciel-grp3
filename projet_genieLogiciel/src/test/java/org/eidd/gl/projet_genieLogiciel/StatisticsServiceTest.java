package org.eidd.gl.projet_genieLogiciel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import org.eidd.gl.projet_genieLogiciel.metier.Priority;
import org.eidd.gl.projet_genieLogiciel.metier.Project;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.metier.TaskStatus;
import org.eidd.gl.projet_genieLogiciel.presentation.Main;
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

    @Test
    void shouldDisplayTaskManagementDemoInMain() {
        ByteArrayInputStream input = new ByteArrayInputStream(
                "Eve\neve@mail.com\n1\n2\nNouvelle tache\nDescription test\n0\n".getBytes()
        );
        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setIn(input);
            System.setOut(new PrintStream(output));
            Main.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String consoleOutput = output.toString();

        assertTrue(consoleOutput.contains("GESTIONNAIRE DE TACHES"));
        assertTrue(consoleOutput.contains("Utilisateur : Eve"));
        assertTrue(consoleOutput.contains("1. Afficher les taches"));
        assertTrue(consoleOutput.contains("2. Ajouter une tache"));
        assertTrue(consoleOutput.contains("3. Changer le statut d'une tache"));
        assertTrue(consoleOutput.contains("4. Changer la priorite d'une tache"));
        assertTrue(consoleOutput.contains("5. Modifier une tache"));
        assertTrue(consoleOutput.contains("6. Supprimer une tache"));
        assertTrue(consoleOutput.contains("Description :"));
        assertTrue(consoleOutput.contains("Creee par : Eve"));
        assertTrue(consoleOutput.contains("Tache ajoutee."));
        assertTrue(consoleOutput.contains("Priorite : HIGH"));
        assertTrue(consoleOutput.contains("Statut : IN_PROGRESS"));
        assertTrue(consoleOutput.contains("Fermeture de l'application."));
    }
}
