package org.eidd.gl.projet_genieLogiciel.service;

import java.util.List;
import java.util.Optional;

import org.eidd.gl.projet_genieLogiciel.metier.User;
import org.eidd.gl.projet_genieLogiciel.persistance.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
    	if (userRepository == null) {
            throw new IllegalArgumentException("Le repository ne peut pas être null");
        }
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
    	if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }
    public void deleteUser(int id) {
        userRepository.delete(id);
    }
  
}

