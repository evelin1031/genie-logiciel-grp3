package org.eidd.gl.projet_genieLogiciel.persistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.eidd.gl.projet_genieLogiciel.metier.Task;

public class InMemoryTaskRepository implements TaskRepository {

    private final List<Task> tasks = new ArrayList<>();

    @Override
    public void save(Task task) {
        tasks.add(task);
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    @Override
    public Optional<Task> findById(int id) {
        for (Task task : tasks) {
            if (task.getId() == id) {
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }

    @Override
    public void delete(int id) {
        tasks.removeIf(task -> task.getId() == id);
    }
}
