package org.eidd.gl.projet_genieLogiciel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eidd.gl.projet_genieLogiciel.metier.Priority;
import org.eidd.gl.projet_genieLogiciel.metier.Project;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.metier.TaskStatus;
import org.eidd.gl.projet_genieLogiciel.metier.User;
import org.eidd.gl.projet_genieLogiciel.persistance.AppDataStore;
import org.eidd.gl.projet_genieLogiciel.persistance.InMemoryTaskRepository;
import org.eidd.gl.projet_genieLogiciel.persistance.ProjectRepository;

public class ProjectService {
    private final AppDataStore appDataStore = new AppDataStore();
    private final ProjectRepository repository;
    private final Map<String, ProjectSpace> projects = new LinkedHashMap<>();

    //On branche le repo souhaité
    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
        loadProjects();
    }

 //Trouve le projet choisi et change sa description
    public void updateProjectDescription(int projectId, String newDescription) {
        Project project = repository.findById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projet introuvable");
        }
        project.setDescription(newDescription);
        repository.update(project);
    }

    //Trouve un projet dans notre repo et ajoute une task
    public void addTaskToProject(int projectId, Task task) {
        Project project = repository.findById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projet introuvable");
        }
        project.addTask(task);
        repository.update(project);
    }

    public String createProject(UserService.AccountData account, String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        ProjectSpace project = projects.computeIfAbsent(name, ProjectSpace::new);
        project.members.add(account.email);
        account.activeProject = name;
        saveProjects();
        return "Projet actif : " + name;
    }

    // verifie l acces puis active un projet existant
    public String joinProject(UserService.AccountData account, String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        ProjectSpace project = projects.get(name);
        if (project == null || !project.members.contains(account.email)) {
            return "Projet introuvable ou acces refuse.";
        }
        account.activeProject = name;
        saveProjects();
        return "Projet actif : " + name;
    }

    // ajoute un membre au projet courant a partir de son pseudo
    public String addMember(UserService.AccountData account, String pseudo, List<UserService.AccountData> accounts) {
        ProjectSpace project = currentProject(account);
        if (project == null) {
            return "Aucun projet actif.";
        }
        for (UserService.AccountData user : accounts) {
            if (user.pseudo.equals(pseudo)) {
                project.members.add(user.email);
                saveProjects();
                return "Membre ajoute.";
            }
        }
        return "Utilisateur introuvable.";
    }

    public TaskService currentTaskService(UserService.AccountData account) {
        ProjectSpace project = currentProject(account);
        return project == null ? null : project.service;
    }

    // retourne juste les comptes membres du projet courant
    public List<UserService.AccountData> currentProjectMembers(
            UserService.AccountData account,
            List<UserService.AccountData> accounts
    ) {
        ProjectSpace project = currentProject(account);
        return project == null ? new ArrayList<>() : accounts.stream()
                .filter(user -> project.members.contains(user.email))
                .toList();
    }

    // prepare la liste affichee dans le menu des projets
    public List<String> projectLabels(UserService.AccountData account) {
        return projects.values().stream()
                .map(project -> project.name
                        + (project.members.contains(account.email)
                        ? " (membre)"
                        : " (non membre)"))
                .toList();
    }

    public boolean hasActiveProject(UserService.AccountData account) {
        return currentProject(account) != null;
    }

    public String activeProjectName(UserService.AccountData account) {
        return account.activeProject;
    }

    public void leaveProject(UserService.AccountData account) {
        account.activeProject = null;
    }

    // sauvegarde les projets et leurs taches dans app data
    public void saveProjects() {
        List<AppDataStore.ProjectData> savedProjects = new ArrayList<>();
        for (ProjectSpace project : projects.values()) {
            AppDataStore.ProjectData record = new AppDataStore.ProjectData();
            record.name = project.name;
            record.members = new ArrayList<>(project.members);
            record.tasks = new ArrayList<>();
            for (Task task : project.service.getAllTasks()) {
                AppDataStore.TaskData taskRecord = new AppDataStore.TaskData();
                taskRecord.title = task.getTitle();
                taskRecord.description = task.getDescription();
                taskRecord.status = task.getStatus().name();
                taskRecord.priority = task.getPriority().name();
                taskRecord.assignedEmail = task.getAssignedUser() == null ? null : task.getAssignedUser().getEmail();
                taskRecord.assignedPseudo = task.getAssignedUser() == null ? null : task.getAssignedUser().getName();
                taskRecord.creatorEmail = task.getCreatedBy() == null ? null : task.getCreatedBy().getEmail();
                taskRecord.creatorPseudo = task.getCreatedBy() == null ? null : task.getCreatedBy().getName();
                taskRecord.deadline = task.getDeadline().getTime();
                record.tasks.add(taskRecord);
            }
            savedProjects.add(record);
        }
        appDataStore.writeProjects(savedProjects);
    }

    private ProjectSpace currentProject(UserService.AccountData account) {
        if (account.activeProject == null) {
            return null;
        }
        return projects.get(account.activeProject);
    }

    // recharge les projets sauvegardes au demarage
    private void loadProjects() {
        for (AppDataStore.ProjectData record : appDataStore.readProjects()) {
            ProjectSpace project = new ProjectSpace(record.name);
            if (record.members != null) {
                project.members.addAll(record.members);
            }
            if (record.tasks != null) {
                for (AppDataStore.TaskData taskRecord : record.tasks) {
                    User user = taskRecord.creatorEmail == null ? null : new User(
                            taskRecord.creatorPseudo == null ? taskRecord.creatorEmail : taskRecord.creatorPseudo,
                            taskRecord.creatorEmail
                    );
                    Task task = new Task(
                            taskRecord.title,
                            taskRecord.description,
                            Priority.valueOf(taskRecord.priority),
                            new Date(taskRecord.deadline),
                            user
                    );
                    task.setStatus(TaskStatus.valueOf(taskRecord.status));
                    if (taskRecord.assignedEmail != null) {
                        task.setAssignedUser(
                                new User(
                                        taskRecord.assignedPseudo == null
                                                ? taskRecord.assignedEmail
                                                : taskRecord.assignedPseudo,
                                        taskRecord.assignedEmail
                                )
                        );
                    }
                    task.setCreatedBy(user);
                    project.service.createTask(task);
                }
            }
            projects.put(project.name, project);
        }
    }

    private static class ProjectSpace {
        private final String name;
        private final Set<String> members = new LinkedHashSet<>();
        private final TaskService service = new TaskService(new InMemoryTaskRepository());

        private ProjectSpace(String name) {
            this.name = name;
        }
    }
}
