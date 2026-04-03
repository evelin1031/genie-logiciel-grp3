package org.eidd.gl.projet_genieLogiciel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eidd.gl.projet_genieLogiciel.metier.TaskStatus;
import org.junit.jupiter.api.Test;

public class TaskStatusTest {

    @Test
    void shouldContainExpectedStatusValues() {
        assertEquals(3, TaskStatus.values().length);
        assertEquals(TaskStatus.TODO, TaskStatus.valueOf("TODO"));
        assertEquals(TaskStatus.IN_PROGRESS, TaskStatus.valueOf("IN_PROGRESS"));
        assertEquals(TaskStatus.DONE, TaskStatus.valueOf("DONE"));
    }
}
