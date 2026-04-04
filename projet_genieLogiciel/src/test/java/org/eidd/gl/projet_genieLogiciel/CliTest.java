package org.eidd.gl.projet_genieLogiciel;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.eidd.gl.projet_genieLogiciel.metier.Priority;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.metier.TaskStatus;
import org.eidd.gl.projet_genieLogiciel.presentation.Cli;
import org.eidd.gl.projet_genieLogiciel.presentation.Main;
import org.eidd.gl.projet_genieLogiciel.service.ProjectService;
import org.eidd.gl.projet_genieLogiciel.service.TaskService;
import org.eidd.gl.projet_genieLogiciel.service.UserService;
import org.junit.jupiter.api.Test;

public class CliTest {

    @Test
    void shouldHideDeadlineForDoneTask() {
        File dataFile = new File("app-data.json");
        dataFile.delete();
        ByteArrayInputStream input = new ByteArrayInputStream(
                ("2\nEve\neve@test.com\neve@test.com\n1234\n" +
                "1\n1\nProjet Test\n\n" +
                "2\nTache terminee\nDescription\n3\n3\n4\n2\n\n" +
                "1\n\n" +
                "7\n" +
                "0\n").getBytes()
        );
        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setIn(input);
            System.setOut(new PrintStream(output));
            Main.main(new String[0]);
        } finally {
            dataFile.delete();
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String consoleOutput = output.toString();

        assertTrue(consoleOutput.contains("Statut : DONE"));
        assertTrue(consoleOutput.contains("Creee par : Eve\nAssignee a : Personne"));
    }

    @Test
    void shouldDisplayDeadlineOnlyForNonDoneTasks() throws Exception {
        Cli cli = new Cli();
        Task todoTask = new Task("Todo", "Desc", Priority.MEDIUM, new Date(), null);
        Task doneTask = new Task("Done", "Desc", Priority.HIGH, new Date(), null);
        doneTask.setStatus(TaskStatus.DONE);

        Method printTasks = Cli.class.getDeclaredMethod("printTasks", List.class);
        printTasks.setAccessible(true);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output));
            printTasks.invoke(cli, List.of(todoTask, doneTask));
        } finally {
            System.setOut(originalOut);
        }

        String consoleOutput = output.toString();

        assertTrue(consoleOutput.contains("Titre : Todo"));
        assertTrue(consoleOutput.contains("Titre : Done"));
        assertTrue(consoleOutput.contains("Titre : Todo\nDescription : Desc\nStatut : TODO\nPriorite : MEDIUM\nCreee par : Non renseigne\nAssignee a : Personne\nDelai : "));
        assertTrue(consoleOutput.contains("Titre : Done\nDescription : Desc\nStatut : DONE\nPriorite : HIGH\nCreee par : Non renseigne\nAssignee a : Personne\n\n"));
    }

    @Test
    void shouldLoginAfterRegistration() {
        File dataFile = new File("app-data.json");
        dataFile.delete();
        ByteArrayInputStream input = new ByteArrayInputStream(
                ("2\nEve\neve@login.com\neve@login.com\n1234\n" +
                "2\n" +
                "1\neve@login.com\n1234\n" +
                "0\n").getBytes()
        );
        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setIn(input);
            System.setOut(new PrintStream(output));
            Main.main(new String[0]);
        } finally {
            dataFile.delete();
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String consoleOutput = output.toString();

        assertTrue(consoleOutput.contains("1. Se connecter"));
        assertTrue(consoleOutput.contains("2. S'inscrire"));
        assertTrue(consoleOutput.contains("Utilisateur : Eve (eve@login.com)"));
        assertTrue(consoleOutput.contains("2. Se deconnecter"));
    }

    @Test
    void shouldAddMemberAndRejectInvalidTaskId() throws Exception {
        File dataFile = new File("app-data.json");
        dataFile.delete();

        Cli cli = new Cli();
        Field userField = Cli.class.getDeclaredField("userService");
        Field projectField = Cli.class.getDeclaredField("projectService");
        Field scannerField = Cli.class.getDeclaredField("scanner");
        userField.setAccessible(true);
        projectField.setAccessible(true);
        scannerField.setAccessible(true);

        UserService userService = (UserService) userField.get(cli);
        ProjectService projectService = (ProjectService) projectField.get(cli);
        userService.register("Eve", "eve@proj.com", "eve@proj.com", "1234");
        userService.register("Bob", "bob@proj.com", "bob@proj.com", "1234");
        UserService.AccountData eve = userService.login("eve@proj.com", "1234");
        projectService.createProject(eve, "Projet Test");
        scannerField.set(cli, new java.util.Scanner(new ByteArrayInputStream("Bob\nabc\n\n".getBytes())));

        Method addMember = Cli.class.getDeclaredMethod("addMember", UserService.AccountData.class);
        Method findTask = Cli.class.getDeclaredMethod("findTask", TaskService.class, UserService.AccountData.class);
        addMember.setAccessible(true);
        findTask.setAccessible(true);

        String message = (String) addMember.invoke(cli, eve);
        Object result = findTask.invoke(cli, projectService.currentTaskService(eve), eve);

        assertTrue("Membre ajoute.".equals(message));
        assertTrue(projectService.currentProjectMembers(eve, userService.getAccounts()).size() == 2);
        assertTrue(result == null);
        dataFile.delete();
    }
}
