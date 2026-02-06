package org.eidd.gl.projet_genieLogiciel.metier;

import java.util.List;
import java.util.Optional;
import org.eidd.gl.projet_genieLogiciel.persistance.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }
}
