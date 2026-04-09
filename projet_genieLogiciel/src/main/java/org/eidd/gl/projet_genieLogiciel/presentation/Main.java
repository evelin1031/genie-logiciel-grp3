package org.eidd.gl.projet_genieLogiciel.presentation;

import org.eidd.gl.projet_genieLogiciel.persistance.InMemoryUserRepository;
import org.eidd.gl.projet_genieLogiciel.persistance.ProjectRepositoryJSON;
import org.eidd.gl.projet_genieLogiciel.service.ProjectService;
import org.eidd.gl.projet_genieLogiciel.service.UserService;

public class Main {
    // lance juste l application console
    public static void main(String[] args) {
        UserService userService = new UserService(new InMemoryUserRepository());
        ProjectService projectService = new ProjectService(new ProjectRepositoryJSON());
        new Cli(userService, projectService).run();
    }
}
