package org.eidd.gl.projet_genieLogiciel.presentation;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.persistance.InMemoryTaskRepository;
import org.eidd.gl.projet_genieLogiciel.service.TaskService;

// gere l interface console simple des taches
public class Cli {
    private final Scanner scanner = new Scanner(System.in);
    private final TaskService taskService = new TaskService(new InMemoryTaskRepository());

    public void run() {
        createSampleTasks();

        boolean running = true;
        while (running) {
            printMenu();
            switch (scanner.nextLine().trim()) {
                case "1":
                    showTasks();
                    break;
                case "2":
                    createTask();
                    break;
                case "3":
                    updateTaskStatus();
                    break;
                case "4":
                    updateTaskPriority();
                    break;
                case "5":
                    editTask();
                    break;
                case "6":
                    deleteTask();
                    break;
                case "0":
                    System.out.println();
                    System.out.println("Fermeture de l application");
                    running = false;
                    break;
                default:
                    System.out.println();
                    System.out.println("Choix invalide");
                    break;
            }
        }
    }

    // ajoute quelques taches pour la demo
    private void createSampleTasks() {
        taskService.createTask(taskService.createTask(
                "Rediger les specs",
                "Lister les fonctionnalites du projet",
                "3",
                "1",
                createDeadline(2),
                null,
                null
        ));
        taskService.createTask(taskService.createTask(
                "Coder le service",
                "Implementer la logique metier",
                "2",
                "2",
                createDeadline(4),
                null,
                null
        ));
        taskService.createTask(taskService.createTask(
                "Ecrire les tests",
                "Verifier les cas principaux",
                "3",
                "3",
                createDeadline(5),
                null,
                null
        ));
    }

    // affiche le menu principal
    private void printMenu() {
        printTitle("GESTIONNAIRE DE TACHES");
        System.out.println("1. Afficher les taches");
        System.out.println("2. Ajouter une tache");
        System.out.println("3. Changer le statut d une tache");
        System.out.println("4. Changer la priorite d une tache");
        System.out.println("5. Modifier une tache");
        System.out.println("6. Supprimer une tache");
        System.out.println("0. Quitter");
        System.out.println();
        System.out.print("Votre choix : ");
    }

    // affiche toutes les taches
    private void showTasks() {
        System.out.println();
        printTitle("LISTE DES TACHES");
        printTasks(taskService.getAllTasks());
    }

    // cree une tache simple depuis la console
    private void createTask() {
        try {
            System.out.println();
            System.out.print("Titre : ");
            String title = scanner.nextLine().trim();
            System.out.print("Description : ");
            String description = scanner.nextLine().trim();

            Task task = taskService.createTask(title, description, "2", "1", createDeadline(7), null, null);
            taskService.createTask(task);

            System.out.println("Tache ajoutee");
        } catch (IllegalArgumentException e) {
            System.out.println("Creation impossible");
        }
    }

    // met a jour le statut d une tache
    private void updateTaskStatus() {
        Optional<Task> task = readTask();
        if (task.isEmpty()) {
            return;
        }

        System.out.println("Statut : 1=TODO, 2=IN_PROGRESS, 3=DONE");
        System.out.print("Votre choix : ");
        String value = scanner.nextLine().trim();
        taskService.updateTaskStatusFromChoice(task.get().getId(), value);
        System.out.println("Statut modifie");
    }

    // met a jour la priorite d une tache
    private void updateTaskPriority() {
        Optional<Task> task = readTask();
        if (task.isEmpty()) {
            return;
        }

        System.out.println("Priorite : 1=LOW, 2=MEDIUM, 3=HIGH");
        System.out.print("Votre choix : ");
        String value = scanner.nextLine().trim();
        taskService.updateTaskPriorityFromChoice(task.get().getId(), value);
        System.out.println("Priorite modifiee");
    }

    // modifie le titre et la description d une tache
    private void editTask() {
        Optional<Task> task = readTask();
        if (task.isEmpty()) {
            return;
        }

        System.out.print("Nouveau titre : ");
        String title = scanner.nextLine().trim();
        System.out.print("Nouvelle description : ");
        String description = scanner.nextLine().trim();
        taskService.editTask(task.get().getId(), title, description);
        System.out.println("Tache modifiee");
    }

    // supprime une tache
    private void deleteTask() {
        Optional<Task> task = readTask();
        if (task.isEmpty()) {
            return;
        }
        taskService.deleteTask(task.get().getId());
        System.out.println("Tache supprimee");
    }

    // lit une tache a partir de son id
    private Optional<Task> readTask() {
        System.out.println();
        System.out.print("ID de la tache : ");
        String idValue = scanner.nextLine().trim();
        Optional<Task> task = taskService.getTaskByInput(idValue);
        if (task.isPresent()) {
            return task;
        }
        System.out.println(idValue.chars().allMatch(Character::isDigit) ? "Tache introuvable" : "ID invalide");
        return Optional.empty();
    }

    private void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Aucune tache");
            return;
        }
        for (Task task : tasks) {
            System.out.println("ID : " + task.getId());
            System.out.println("Titre : " + task.getTitle());
            System.out.println("Description : " + task.getDescription());
            System.out.println("Statut : " + task.getStatus());
            System.out.println("Priorite : " + task.getPriority());
            System.out.println("Delai : " + task.getDeadline());
            System.out.println();
        }
    }

    private static void printTitle(String title) {
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("==================================================");
        System.out.println();
    }

    private static Date createDeadline(int days) {
        return new Date(System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L);
    }
}
