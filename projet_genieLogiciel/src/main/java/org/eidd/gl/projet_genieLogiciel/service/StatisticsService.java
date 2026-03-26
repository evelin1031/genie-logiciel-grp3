package org.eidd.gl.projet_genieLogiciel.service;

import org.eidd.gl.projet_genieLogiciel.metier.Project;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.metier.TaskStatus;

public class StatisticsService {

    public int countCompletedTasks(Project project) {
        if (project == null || project.getTasks() == null) {
            return 0;
        }

        int count = 0;
        for (Task task : project.getTasks()) {
            if (task != null && task.getStatus() == TaskStatus.DONE) {
                count++;
            }
        }
        return count;
    }

    public int countPendingTasks(Project project) {
        if (project == null || project.getTasks() == null) {
            return 0;
        }

        int count = 0;
        for (Task task : project.getTasks()) {
            if (task != null && task.getStatus() != TaskStatus.DONE) {
                count++;
            }
        }
        return count;
    }

    public double calculateCompletionRate(Project project) {
        if (project == null || project.getTasks() == null || project.getTasks().isEmpty()) {
            return 0.0;
        }

        return (double) countCompletedTasks(project) / project.getTasks().size();
    }
}