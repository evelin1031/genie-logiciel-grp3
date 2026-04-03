package org.eidd.gl.projet_genieLogiciel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eidd.gl.projet_genieLogiciel.metier.Priority;
import org.junit.jupiter.api.Test;

public class PriorityTest {

    @Test
    void shouldContainExpectedPriorityValues() {
        assertEquals(3, Priority.values().length);
        assertEquals(Priority.LOW, Priority.valueOf("LOW"));
        assertEquals(Priority.MEDIUM, Priority.valueOf("MEDIUM"));
        assertEquals(Priority.HIGH, Priority.valueOf("HIGH"));
    }
}
