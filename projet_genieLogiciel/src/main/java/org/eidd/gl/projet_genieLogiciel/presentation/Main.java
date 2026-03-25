package org.eidd.gl.projet_genieLogiciel.presentation;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.eidd.gl.projet_genieLogiciel.metier.Priority;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.metier.TaskStatus;
import org.eidd.gl.projet_genieLogiciel.metier.User;
import org.eidd.gl.projet_genieLogiciel.persistance.InMemoryTaskRepository;
import org.eidd.gl.projet_genieLogiciel.persistance.TaskRepository;
import org.eidd.gl.projet_genieLogiciel.service.TaskService;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);

        printTitle("GESTIONNAIRE DE TACHES");
        User currentUser = createUser(scanner);
        createSampleTasks(service, currentUser);

        boolean running = true;
        while (running) {
            printMenu(currentUser);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    printTitle("LISTE DES TACHES");
                    printTasks(service.getAllTasks());
                    break;
                case "2":
                    createTask(scanner, service, currentUser);
                    break;
                case "3":
                    updateTaskStatus(scanner, service);
                    break;
                case "4":
                    updateTaskPriority(scanner, service);
                    break;
                case "5":
                    editTask(scanner, service);
                    break;
                case "6":
                    deleteTask(scanner, service);
                    break;
                case "0":
                    running = false;
                    System.out.println();
                    System.out.println("Fermeture de l'application.");
                    break;
                default:
                    System.out.println();
                    System.out.println("Choix invalide.");
                    break;
            }
        }
    }

    private static User createUser(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Votre nom : ");
                String name = scanner.nextLine().trim();
                System.out.print("Votre email : ");
                String email = scanner.nextLine().trim();
                return new User(name, email);
            } catch (IllegalArgumentException e) {
                System.out.println("Informations invalides, veuillez recommencer.");
            }
        }
    }

    private static void createSampleTasks(TaskService service, User user) {
        Task task1 = new Task(
                "Rediger les specifications",
                "Lister les fonctionnalites obligatoires du projet",
                Priority.HIGH,
                createDeadline(2),
                user
        );
        Task task2 = new Task(
                "Coder les services",
                "Implementer la logique principale",
                Priority.MEDIUM,
                createDeadline(4),
                user
        );
        Task task3 = new Task(
                "Ecrire les tests",
                "Verifier les comportements principaux",
                Priority.HIGH,
                createDeadline(5),
                user
        );

        task2.setStatus(TaskStatus.IN_PROGRESS);
        task3.setStatus(TaskStatus.DONE);

        service.createTask(task1);
        service.createTask(task2);
        service.createTask(task3);
    }

    private static void printMenu(User user) {
        printTitle("MENU");
        System.out.println("Utilisateur : " + user.getName());
        System.out.println();
        System.out.println("1. Afficher les taches");
        System.out.println("2. Ajouter une tache");
        System.out.println("3. Changer le statut d'une tache");
        System.out.println("4. Changer la priorite d'une tache");
        System.out.println("5. Modifier une tache");
        System.out.println("6. Supprimer une tache");
        System.out.println("0. Quitter");
        System.out.println();
        System.out.print("Votre choix : ");
    }

    private static void createTask(Scanner scanner, TaskService service, User user) {
        try {
            System.out.println();
            System.out.print("Titre : ");
            String title = scanner.nextLine().trim();
            System.out.print("Description : ");
            String description = scanner.nextLine().trim();

            Task task = new Task(title, description, Priority.MEDIUM, createDeadline(7), user);
            service.createTask(task);

            System.out.println();
            System.out.println("Tache ajoutee.");
        } catch (IllegalArgumentException e) {
            System.out.println();
            System.out.println("Creation impossible.");
        }
    }

    private static void updateTaskStatus(Scanner scanner, TaskService service) {
        System.out.println();
        System.out.print("ID de la tache : ");
        String idValue = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(idValue);
            Optional<Task> taskOptional = service.getTaskById(id);

            if (taskOptional.isEmpty()) {
                System.out.println("Tache introuvable.");
                return;
            }

            System.out.println("Statut : 1=TODO, 2=IN_PROGRESS, 3=DONE");
            System.out.print("Votre choix : ");
            String statusValue = scanner.nextLine().trim();

            if ("1".equals(statusValue)) {
                service.updateTaskStatus(id, TaskStatus.TODO);
            } else if ("2".equals(statusValue)) {
                service.updateTaskStatus(id, TaskStatus.IN_PROGRESS);
            } else if ("3".equals(statusValue)) {
                service.updateTaskStatus(id, TaskStatus.DONE);
            } else {
                System.out.println("Statut invalide.");
                return;
            }

            System.out.println("Statut modifie.");
        } catch (NumberFormatException e) {
            System.out.println("ID invalide.");
        }
    }

    private static void updateTaskPriority(Scanner scanner, TaskService service) {
        System.out.println();
        System.out.print("ID de la tache : ");
        String idValue = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(idValue);
            Optional<Task> taskOptional = service.getTaskById(id);

            if (taskOptional.isEmpty()) {
                System.out.println("Tache introuvable.");
                return;
            }

            System.out.println("Priorite : 1=LOW, 2=MEDIUM, 3=HIGH");
            System.out.print("Votre choix : ");
            String priorityValue = scanner.nextLine().trim();

            Task task = taskOptional.get();
            if ("1".equals(priorityValue)) {
                updateTaskPriority(task, Priority.LOW);
            } else if ("2".equals(priorityValue)) {
                updateTaskPriority(task, Priority.MEDIUM);
            } else if ("3".equals(priorityValue)) {
                updateTaskPriority(task, Priority.HIGH);
            } else {
                System.out.println("Priorite invalide.");
                return;
            }

            System.out.println("Priorite modifiee.");
        } catch (NumberFormatException e) {
            System.out.println("ID invalide.");
        }
    }

    private static void editTask(Scanner scanner, TaskService service) {
        System.out.println();
        System.out.print("ID de la tache : ");
        String idValue = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(idValue);
            Optional<Task> taskOptional = service.getTaskById(id);

            if (taskOptional.isEmpty()) {
                System.out.println("Tache introuvable.");
                return;
            }

            Task task = taskOptional.get();
            System.out.print("Nouveau titre : ");
            String title = scanner.nextLine().trim();
            System.out.print("Nouvelle description : ");
            String description = scanner.nextLine().trim();

            task.updateTitle(title);
            task.updateDescription(description);
            System.out.println("Tache modifiee.");
        } catch (NumberFormatException e) {
            System.out.println("ID invalide.");
        } catch (IllegalArgumentException e) {
            System.out.println("Modification impossible.");
        }
    }

    private static void deleteTask(Scanner scanner, TaskService service) {
        System.out.println();
        System.out.print("ID de la tache : ");
        String idValue = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(idValue);
            Optional<Task> taskOptional = service.getTaskById(id);

            if (taskOptional.isEmpty()) {
                System.out.println("Tache introuvable.");
                return;
            }

            service.deleteTask(id);
            System.out.println("Tache supprimee.");
        } catch (NumberFormatException e) {
            System.out.println("ID invalide.");
        }
    }

    private static Date createDeadline(int daysFromNow) {
        return new Date(System.currentTimeMillis() + daysFromNow * 24L * 60L * 60L * 1000L);
    }

    private static void printTitle(String title) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("==================================================");
        System.out.println();
    }

    private static void printTasks(List<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(formatTask(task));
            System.out.println();
        }
    }

    private static String formatTask(Task task) {
        String creator = task.getAssignedUser() == null ? "Non renseigne" : task.getAssignedUser().getName();
        return String.format(
                "ID : %d%nTitre : %s%nDescription : %s%nStatut : %s%nPriorite : %s%nCreee par : %s",
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                creator
        );
    }

    private static void updateTaskPriority(Task task, Priority priority) {
        Task updatedTask = new Task(
                task.getTitle(),
                task.getDescription(),
                priority,
                task.getDeadline(),
                task.getAssignedUser()
        );
        updatedTask.setStatus(task.getStatus());
        task.updateTitle(updatedTask.getTitle());
        task.updateDescription(updatedTask.getDescription());
        setTaskPriority(task, priority);
    }

    private static void setTaskPriority(Task task, Priority priority) {
        try {
            java.lang.reflect.Field priorityField = Task.class.getDeclaredField("priority");
            priorityField.setAccessible(true);
            priorityField.set(task, priority);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Impossible de modifier la priorite", e);
        }
    }
}
