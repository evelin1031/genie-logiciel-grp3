package org.eidd.gl.projet_genieLogiciel.presentation;

import java.io.Console;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.metier.User;
import org.eidd.gl.projet_genieLogiciel.persistance.InMemoryUserRepository;
import org.eidd.gl.projet_genieLogiciel.persistance.ProjectRepositoryJSON;
import org.eidd.gl.projet_genieLogiciel.service.ProjectService;
import org.eidd.gl.projet_genieLogiciel.service.TaskService;
import org.eidd.gl.projet_genieLogiciel.service.UserService;

// gere l interface console avec les menus et les saisies
public class Cli {
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final ProjectService projectService;
    private boolean running = true;

    public Cli() {
        this(
                new UserService(new InMemoryUserRepository()),
                new ProjectService(new ProjectRepositoryJSON())
        );
    }

    public Cli(UserService userService, ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
    }

    public void run() {
        UserService.AccountData account = null;
        while (running) {
            if (account == null) {
                account = authenticate();
                if (account == null) {
                    System.out.println("Fermeture de l'application.");
                    return;
                }
                continue;
            }
            account = handleMainMenu(account);
            running = account != null || shouldKeepRunning();
        }
    }

    private UserService.AccountData handleMainMenu(UserService.AccountData account) {
        clearScreen();
        printMenu(account);
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                return handleFirstChoice(account);
            case "2":
                return handleSecondChoice(account);
            case "3":
                return handleProjectChoice(account, () -> showMembers(account));
            case "4":
                return handleProjectChoice(account, () -> pause(addMember(account)));
            case "5":
                return handleProjectChoice(account, () -> manageTask(account));
            case "6":
                return handleChangeProject(account);
            case "7":
                return handleLogout(account);
            case "0":
                closeApplication();
                return null;
            default:
                pause("Choix invalide.");
                return account;
        }
    }

    private UserService.AccountData handleFirstChoice(UserService.AccountData account) {
        if (projectService.hasActiveProject(account)) {
            showTasks(account);
        } else {
            pause(chooseProject(account));
            userService.saveAccounts();
        }
        return account;
    }

    private UserService.AccountData handleSecondChoice(UserService.AccountData account) {
        if (projectService.hasActiveProject(account)) {
            createTask(account);
            return account;
        }
        userService.saveAccounts();
        projectService.saveProjects();
        return null;
    }

    private UserService.AccountData handleProjectChoice(
            UserService.AccountData account,
            Runnable action
    ) {
        if (projectService.hasActiveProject(account)) {
            action.run();
        } else {
            pause("Choix invalide.");
        }
        return account;
    }

    private UserService.AccountData handleChangeProject(UserService.AccountData account) {
        if (projectService.hasActiveProject(account)) {
            projectService.leaveProject(account);
            userService.saveAccounts();
            pause(chooseProject(account));
            userService.saveAccounts();
        } else {
            pause("Choix invalide.");
        }
        return account;
    }

    private UserService.AccountData handleLogout(UserService.AccountData account) {
        if (projectService.hasActiveProject(account)) {
            userService.saveAccounts();
            projectService.saveProjects();
            return null;
        }
        pause("Choix invalide.");
        return account;
    }

    private boolean shouldKeepRunning() {
        return running;
    }

    private void closeApplication() {
        userService.saveAccounts();
        projectService.saveProjects();
        System.out.println("Fermeture de l'application.");
        running = false;
    }

    // affiche le menu selon le projet courant
    private void printMenu(UserService.AccountData account) {
        printTitle("MENU");
        System.out.println("Utilisateur : " + account.pseudo + " (" + account.email + ")");
        System.out.println("Projet actif : " + valueOrDefault(projectService.activeProjectName(account), "Aucun"));
        System.out.println();
        if (projectService.hasActiveProject(account)) {
            System.out.println("1. Afficher les taches");
            System.out.println("2. Ajouter une tache");
            System.out.println("3. Afficher les membres du projet");
            System.out.println("4. Ajouter un membre au projet");
            System.out.println("5. Choisir une tache a modifier");
            System.out.println("6. Changer de projet");
            System.out.println("7. Se deconnecter");
        } else {
            System.out.println("1. Creer ou rejoindre un projet");
            System.out.println("2. Se deconnecter");
        }
        System.out.println("0. Quitter");
        System.out.println();
        System.out.print("Votre choix : ");
    }

    // gere la connexion et l inscription
    private UserService.AccountData authenticate() {
        int failedAttempts = 0;
        while (true) {
            clearScreen();
            printTitle("CONNEXION");
            System.out.println("1. Se connecter");
            System.out.println("2. S'inscrire");
            System.out.println("0. Quitter");
            System.out.println();
            System.out.print("Votre choix : ");
            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                String email = ask("Email : ");
                String password = readPassword("Mot de passe : ");
                if (email == null || password == null) {
                    continue;
                }
                UserService.AccountData account = userService.login(email, password);
                if (account != null) {
                    return account;
                }
                failedAttempts++;
                if (failedAttempts >= 3) {
                    pause("Vous avez utilise le nombre d'essais de cette session. Veuillez reessayer ulterieurement.");
                    return null;
                }
                pause("Connexion impossible.");
            } else if ("2".equals(choice)) {
                String pseudo = ask("Pseudo : ");
                String email = ask("Email : ");
                String confirmEmail = ask("Confirmation email : ");
                String password = readPassword("Mot de passe : ");
                if (pseudo == null || email == null || confirmEmail == null || password == null) {
                    continue;
                }
                String error = userService.register(pseudo, email, confirmEmail, password);
                if (error == null) {
                    projectService.saveProjects();
                    return userService.login(email, password);
                }
                pause(error);
            } else if ("0".equals(choice)) {
                return null;
            } else {
                pause("Choix invalide.");
            }
        }
    }

    // gere le choix entre creation et connexion a un projet
    private String chooseProject(UserService.AccountData account) {
        clearScreen();
        printTitle("PROJETS");
        System.out.println("Vos projets :");
        for (String project : projectService.projectLabels(account)) {
            System.out.println("- " + project);
        }
        if (projectService.projectLabels(account).isEmpty()) {
            System.out.println("Aucun projet pour le moment.");
        }
        System.out.println();
        System.out.println("1. Creer un projet");
        System.out.println("2. Rejoindre un projet existant");
        System.out.println("R. Retour");
        System.out.println();
        System.out.print("Votre choix : ");
        String choice = scanner.nextLine().trim();

        if ("R".equalsIgnoreCase(choice)) {
            return null;
        }
        if ("1".equals(choice)) {
            return projectService.createProject(account, ask("Nom du projet : "));
        }
        if ("2".equals(choice)) {
            return projectService.joinProject(account, ask("Nom du projet : "));
        }
        return "Choix invalide.";
    }

    // demande le pseudo d un membre a ajouter
    private String addMember(UserService.AccountData account) {
        clearScreen();
        printTitle("AJOUTER UN MEMBRE");
        System.out.println("Utilisateurs disponibles :");
        for (UserService.AccountData user : userService.getAccounts()) {
            System.out.println("- " + user.pseudo + " (" + user.email + ")");
        }
        System.out.println();
        return projectService.addMember(account, ask("Pseudo du membre : "), userService.getAccounts());
    }

    private void showTasks(UserService.AccountData account) {
        TaskService taskService = taskService(account);
        if (taskService == null) {
            pause("Aucun projet actif. Creez ou rejoignez un projet.");
            return;
        }
        clearScreen();
        printTitle("LISTE DES TACHES");
        printTasks(taskService.getAllTasks());
        waitEnter();
    }

    private void createTask(UserService.AccountData account) {
        TaskService taskService = taskService(account);
        if (taskService == null) {
            pause("Aucun projet actif. Creez ou rejoignez un projet.");
            return;
        }
        Task task = buildTask(account);
        if (task == null) {
            return;
        }
        taskService.createTask(task);
        projectService.saveProjects();
        pause("Tache ajoutee.");
    }

    // recupere les infos utiles pour creer une tache
    private Task buildTask(UserService.AccountData account) {
        String title = ask("Titre : ");
        String description = ask("Description : ");
        String priorityValue = ask("Priorite : 1=LOW, 2=MEDIUM, 3=HIGH : ");
        String statusValue = ask("Statut : 1=TODO, 2=IN_PROGRESS, 3=DONE : ");
        String deadlineValue = ask("Delai en jours : ");
        String assignValue = ask("Assigner a quelqu'un ? 1=Oui, 2=Non : ");
        if (title == null
                || description == null
                || priorityValue == null
                || statusValue == null
                || deadlineValue == null
                || assignValue == null) {
            return null;
        }

        User assignedUser = "1".equals(assignValue) ? chooseMember(account) : null;
        if ("1".equals(assignValue) && assignedUser == null) {
            return null;
        }
        try {
            return taskService(account).createTask(
                    title,
                    description,
                    priorityValue,
                    statusValue,
                    createDeadline(deadlineValue),
                    new User(account.pseudo, account.email),
                    assignedUser
            );
        } catch (IllegalArgumentException e) {
            pause("Creation impossible.");
            return null;
        }
    }

    // regroupe les actions de modif sur une tache
    private void manageTask(UserService.AccountData account) {
        TaskService taskService = taskService(account);
        clearScreen();
        printTitle("CHOISIR UNE TACHE A MODIFIER");
        printTasks(taskService.getAllTasks());
        Task task = findTask(taskService, account);
        if (task == null) {
            return;
        }
        boolean editing = true;
        while (editing) {
            clearScreen();
            printTitle("GERER UNE TACHE");
            printTasks(List.of(task));
            System.out.println("1. Modifier le statut");
            System.out.println("2. Modifier la priorite");
            System.out.println("3. Modifier le titre");
            System.out.println("4. Modifier la description");
            System.out.println("5. Modifier le delai");
            System.out.println("6. Assigner la tache");
            System.out.println("7. Supprimer la tache");
            System.out.println("R. Retour");
            System.out.println();
            System.out.print("Votre choix : ");
            String choice = scanner.nextLine().trim();
            editing = handleTaskAction(choice, account, taskService, task);
        }
    }

    private boolean handleTaskAction(
            String choice,
            UserService.AccountData account,
            TaskService taskService,
            Task task
    ) {
        if ("R".equalsIgnoreCase(choice)) {
            return false;
        }
        if ("1".equals(choice)) {
            updateStatus(taskService, task);
            return true;
        }
        if ("2".equals(choice)) {
            updatePriority(taskService, task);
            return true;
        }
        if ("3".equals(choice)) {
            updateTitle(taskService, task);
            return true;
        }
        if ("4".equals(choice)) {
            updateDescription(taskService, task);
            return true;
        }
        if ("5".equals(choice)) {
            updateDeadline(taskService, task);
            return true;
        }
        if ("6".equals(choice)) {
            updateAssignee(account, taskService, task);
            return true;
        }
        if ("7".equals(choice)) {
            taskService.deleteTask(task.getId());
            projectService.saveProjects();
            pause("Tache supprimee.");
            return false;
        }
        pause("Choix invalide.");
        return true;
    }

    // recherche une tache a partir de son id
    private Task findTask(TaskService taskService, UserService.AccountData account) {
        if (taskService == null || !projectService.hasActiveProject(account)) {
            pause("Aucun projet actif. Creez ou rejoignez un projet.");
            return null;
        }
        String idValue = ask("ID de la tache : ");
        if (idValue == null) {
            return null;
        }
        Optional<Task> task = taskService.getTaskByInput(idValue);
        if (task.isPresent()) {
            return task.get();
        }
        pause(idValue.chars().allMatch(Character::isDigit) ? "Tache introuvable." : "ID invalide.");
        return null;
    }

    // choisit un membre pour l assignation
    private User chooseMember(UserService.AccountData account) {
        List<UserService.AccountData> members =
                projectService.currentProjectMembers(account, userService.getAccounts());
        if (members.isEmpty()) {
            pause("Aucun membre disponible.");
            return null;
        }
        clearScreen();
        printTitle("MEMBRES DU PROJET");
        for (UserService.AccountData member : members) {
            System.out.println("- " + member.pseudo + " (" + member.email + ")");
        }
        System.out.println();
        String pseudo = ask("Pseudo a assigner : ");
        if (pseudo == null) {
            return null;
        }
        for (UserService.AccountData member : members) {
            if (member.pseudo.equals(pseudo)) {
                return new User(member.pseudo, member.email);
            }
        }
        pause("Utilisateur introuvable.");
        return null;
    }

    private void showMembers(UserService.AccountData account) {
        clearScreen();
        printTitle("MEMBRES DU PROJET");
        List<UserService.AccountData> members =
                projectService.currentProjectMembers(account, userService.getAccounts());
        if (members.isEmpty()) {
            System.out.println("Aucun membre.");
        } else {
            for (UserService.AccountData member : members) {
                System.out.println("- " + member.pseudo + " (" + member.email + ")");
            }
        }
        waitEnter();
    }

    private TaskService taskService(UserService.AccountData account) {
        return projectService.currentTaskService(account);
    }

    // lit une saisie simple avec retour possible
    private String ask(String prompt) {
        clearScreen();
        System.out.println("Tapez R pour revenir.");
        System.out.println();
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        return "R".equalsIgnoreCase(value) ? null : value;
    }

    // lit un mot de passe avec masquage si possible
    private String readPassword(String prompt) {
        clearScreen();
        System.out.println("Tapez R pour revenir.");
        System.out.println();
        Console console = System.console();
        if (console != null) {
            char[] chars = console.readPassword(prompt);
            if (chars == null) {
                return null;
            }
            String value = new String(chars).trim();
            return "R".equalsIgnoreCase(value) ? null : value;
        }
        System.out.print(prompt);
        String value = scanner.nextLine().trim();
        return "R".equalsIgnoreCase(value) ? null : value;
    }

    // affiche un message puis attend la validtaion
    private void pause(String message) {
        if (message == null) {
            return;
        }
        clearScreen();
        System.out.println(message);
        waitEnter();
    }

    private static void clearScreen() {
        System.out.println();
        System.out.println();
    }

    private static void printTitle(String title) {
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("==================================================");
        System.out.println();
    }

    private void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Aucune tache.");
            return;
        }
        for (Task task : tasks) {
            System.out.println("ID : " + task.getId());
            System.out.println("Titre : " + task.getTitle());
            System.out.println("Description : " + task.getDescription());
            System.out.println("Statut : " + task.getStatus());
            System.out.println("Priorite : " + task.getPriority());
            System.out.println(
                    "Creee par : " + (task.getCreatedBy() == null ? "Non renseigne" : task.getCreatedBy().getName())
            );
            System.out.println(
                    "Assignee a : " + (task.getAssignedUser() == null ? "Personne" : task.getAssignedUser().getName())
            );
            if (task.getStatus() != org.eidd.gl.projet_genieLogiciel.metier.TaskStatus.DONE) {
                System.out.println("Delai : " + task.getDeadline());
            }
            System.out.println();
        }
    }

    private Date createDeadline(String deadlineValue) {
        return new Date(
                System.currentTimeMillis() + Long.parseLong(deadlineValue) * 24L * 60L * 60L * 1000L
        );
    }

    private void updateStatus(TaskService taskService, Task task) {
        String value = ask("Statut : 1=TODO, 2=IN_PROGRESS, 3=DONE : ");
        if (value != null) {
            taskService.updateTaskStatusFromChoice(task.getId(), value);
            projectService.saveProjects();
        }
    }

    private void updatePriority(TaskService taskService, Task task) {
        String value = ask("Priorite : 1=LOW, 2=MEDIUM, 3=HIGH : ");
        if (value != null) {
            taskService.updateTaskPriorityFromChoice(task.getId(), value);
            projectService.saveProjects();
        }
    }

    private void updateTitle(TaskService taskService, Task task) {
        String title = ask("Nouveau titre : ");
        if (title != null) {
            taskService.editTask(task.getId(), title, task.getDescription());
            projectService.saveProjects();
        }
    }

    private void updateDescription(TaskService taskService, Task task) {
        String description = ask("Nouvelle description : ");
        if (description != null) {
            taskService.editTask(task.getId(), task.getTitle(), description);
            projectService.saveProjects();
        }
    }

    private void updateDeadline(TaskService taskService, Task task) {
        String deadlineValue = ask("Nouveau delai en jours : ");
        if (deadlineValue != null) {
            taskService.updateTaskDeadline(task.getId(), createDeadline(deadlineValue));
            projectService.saveProjects();
        }
    }

    private void updateAssignee(UserService.AccountData account, TaskService taskService, Task task) {
        User assignedUser = chooseMember(account);
        if (assignedUser != null) {
            taskService.updateTaskAssignee(task.getId(), assignedUser);
            projectService.saveProjects();
        }
    }

    private void waitEnter() {
        System.out.println();
        System.out.println("Appuyez sur Entree pour continuer...");
        scanner.nextLine();
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
