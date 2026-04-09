package org.eidd.gl.projet_genieLogiciel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.eidd.gl.projet_genieLogiciel.metier.User;
import org.eidd.gl.projet_genieLogiciel.persistance.InMemoryUserRepository;
import org.eidd.gl.projet_genieLogiciel.persistance.UserRepository;
import org.eidd.gl.projet_genieLogiciel.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        UserRepository repo = new InMemoryUserRepository();
        userService = new UserService(repo);
    }

    @Test
    void creationUtilisateur() {
        User user = new User("Doha", "doha@mail.com");

        userService.createUser(user);

        Optional<User> result = userService.getUserById(user.getId());
        assertTrue(result.isPresent());
    }

    @Test
    void recuperationListe() {
        userService.createUser(new User("Doha", "doha@mail.com"));
        userService.createUser(new User("Hugo", "hugo@mail.com"));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void rechercheParId() {
        User user = new User("Eve", "Eve@mail.com");
        userService.createUser(user);

        Optional<User> result = userService.getUserById(user.getId());

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void suppressionUtilisateur() {
        User user = new User("Meyssem", "meyssem@mail.com");
        userService.createUser(user);

        userService.deleteUser(user.getId());

        Optional<User> result = userService.getUserById(user.getId());
        assertFalse(result.isPresent());
    }

    @Test
    void inscriptionEtConnexionAvecMotDePasseHash() {
        String error = userService.register("Eve", "eve@mail.com", "eve@mail.com", "Secret123!");

        assertNull(error);
        assertTrue(userService.login("eve@mail.com", "Secret123!") != null);
        assertTrue(userService.findAccountByEmail("eve@mail.com").passwordHash != null);
    }

    @Test
    void refuseMotDePasseTropFaible() {
        String error = userService.register("Eve", "eve@mail.com", "eve@mail.com", "1234");

        assertEquals(
                "Le mot de passe doit contenir au moins 8 caracteres, une majuscule, une minuscule, un chiffre et un caractere special.",
                error
        );
        assertTrue(userService.login("eve@mail.com", "1234") == null);
    }
}
