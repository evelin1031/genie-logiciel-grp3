package org.eidd.gl.projet_genieLogiciel.persistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.eidd.gl.projet_genieLogiciel.metier.User;

public class InMemoryUserRepository implements UserRepository {

    private final List<User> users = new ArrayList<>();

    @Override
    public void save(User user) {
    	if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }
        // Remplace l'utilisateur s'il existe déjà 
        delete(user.getId());
        users.add(user);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public Optional<User> findById(int id) {
    	return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    @Override
    public void delete(int id) {
        users.removeIf(user -> user.getId() == id);
    }
}
