package org.eidd.gl.projet_genieLogiciel;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.eidd.gl.projet_genieLogiciel.presentation.Main;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    void shouldHandleInvalidChoiceInMain() {
        ByteArrayInputStream input = new ByteArrayInputStream(
                ("8\n0\n").getBytes()
        );
        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setIn(input);
            System.setOut(new PrintStream(output));
            Main.main(new String[0]);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String consoleOutput = output.toString();

        assertTrue(consoleOutput.contains("Choix invalide"));
        assertTrue(consoleOutput.contains("Fermeture de l application"));
    }
}
