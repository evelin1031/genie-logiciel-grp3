package org.eidd.gl.projet_genieLogiciel.persistance;

import java.util.List;
import java.util.Optional;
import org.eidd.gl.projet_genieLogiciel.metier.Task;

public interface TaskRepository {

    void save(Task task);
    List<Task> findAll();
    Optional<Task> findById(int id);
    void delete(int id);
}
