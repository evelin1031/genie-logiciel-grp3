package org.eidd.gl.projet_genieLogiciel.presentation;

import org.eidd.gl.projet_genieLogiciel.metier.User;
import org.eidd.gl.projet_genieLogiciel.metier.UserService;
import org.eidd.gl.projet_genieLogiciel.persistance.InMemoryUserRepository;
import org.eidd.gl.projet_genieLogiciel.persistance.UserRepository;

public class Main {

    public static void main(String[] args) {

        UserRepository repository = new InMemoryUserRepository();
        UserService service = new UserService(repository);

        User user = new User("Eve", "eve@mail.com");
        service.createUser(user);
        System.out.println("Création OK");
        System.out.println("Nombre d'utilisateurs : " + service.getAllUsers().size());

        try {
            new User("", "");
        } catch (IllegalArgumentException e) {
        	System.out.println("Données invalides correctement rejetées");
        }
    }
}
