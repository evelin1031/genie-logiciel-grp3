package org.eidd.gl.projet_genieLogiciel.persistance;

import java.util.List;
import java.util.Optional;
import org.eidd.gl.projet_genieLogiciel.metier.User;

public interface UserRepository {

    void save(User user);
    List<User> findAll();
    Optional<User> findById(int id);
    void delete(int id);
}
