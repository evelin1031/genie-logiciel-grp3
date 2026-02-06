package org.eidd.gl.projet_genieLogiciel.persistance;

import java.util.List;
import java.util.Optional;
import org.eidd.gl.projet_genieLogiciel.metier.Project;

public interface ProjectRepository {

    void save(Project project);
    List<Project> findAll();
    Optional<Project> findById(int id);
    void delete(int id);
}
