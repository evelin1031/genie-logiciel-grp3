package org.eidd.gl.projet_genieLogiciel.service;
import org.eidd.gl.projet_genieLogiciel.metier.Project;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.persistance.ProjectRepository;

public class ProjectService {
    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public void updateProjectDescription(int projectId, String newDescription) {
        Project project = repository.findById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projet introuvable");
        }
        project.setDescription(newDescription);
        repository.save(project);
    }

    public void addTaskToProject(int projectId, Task task) {
        Project project = repository.findById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Projet introuvable");
        }
        project.addTask(task);
        repository.save(project);
    }
}