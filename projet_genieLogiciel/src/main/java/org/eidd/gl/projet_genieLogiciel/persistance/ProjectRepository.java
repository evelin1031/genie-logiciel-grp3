package org.eidd.gl.projet_genieLogiciel.persistance;

import java.util.List;
import org.eidd.gl.projet_genieLogiciel.metier.Project;

public interface ProjectRepository {

	List<Project> read();
    void write(List<Project> projects);
    void save(Project project);
    Project findById(int id);
    void deleteProjectById(int id);
    void deleteRepo();
}
