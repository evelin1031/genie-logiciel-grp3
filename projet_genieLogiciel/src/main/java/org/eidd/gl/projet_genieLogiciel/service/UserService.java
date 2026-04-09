package org.eidd.gl.projet_genieLogiciel.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eidd.gl.projet_genieLogiciel.metier.User;
import org.eidd.gl.projet_genieLogiciel.persistance.AppDataStore;
import org.eidd.gl.projet_genieLogiciel.persistance.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserRepository userRepository;
    private final AppDataStore appDataStore = new AppDataStore();
    private final Map<String, AccountData> accounts = new LinkedHashMap<>();

    public UserService(UserRepository userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("Le repository ne peut pas être null");
        }
        this.userRepository = userRepository;
        loadAccounts();
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

    public List<AccountData> getAccounts() {
        return new ArrayList<>(accounts.values());
    }

    // verifie les identifiants d un compte existant
    public AccountData login(String email, String password) {
        AccountData account = accounts.get(email);
        return account != null && matchesPassword(account, password) ? account : null;
    }

    // enregistre un nouveau compte si les donnees sont valides
    public String register(String pseudo, String email, String confirmEmail, String password) {
        if (pseudo == null || email == null || confirmEmail == null || password == null) {
            return "Inscription impossible.";
        }
        if (!email.equals(confirmEmail) || pseudo.isBlank() || email.isBlank() || password.isBlank()) {
            return "Inscription impossible.";
        }
        if (!isPasswordStrong(password)) {
            return "Le mot de passe doit contenir au moins 8 caracteres, une majuscule, une minuscule, un chiffre et un caractere special.";
        }
        if (findAccountByPseudo(pseudo) != null) {
            return "Pseudo deja utilise.";
        }
        if (accounts.containsKey(email)) {
            return "Inscription impossible.";
        }

        accounts.put(email, new AccountData(pseudo, email, hashPassword(password), null));
        saveAccounts();
        return null;
    }

    public AccountData findAccountByEmail(String email) {
        return accounts.get(email);
    }

    public AccountData findAccountByPseudo(String pseudo) {
        for (AccountData account : accounts.values()) {
            if (account.pseudo.equals(pseudo)) {
                return account;
            }
        }
        return null;
    }

    public void saveAccounts() {
       migrateLegacyPasswords();
       appDataStore.writeAccounts(new ArrayList<>(accounts.values()));
    }

    // recharge les comptes sauvegardes au demarage
    private void loadAccounts() {
       for (AccountData account : appDataStore.readAccounts()) {
           if (account.passwordHash == null && account.password != null) {
               account.passwordHash = hashPassword(account.password);
               account.password = null;
           }
           accounts.put(account.email, account);
        }
   }

    private void migrateLegacyPasswords() {
        for (AccountData account : accounts.values()) {
            if (account.passwordHash == null && account.password != null) {
                account.passwordHash = hashPassword(account.password);
                account.password = null;
            }
        }
    }

    private boolean matchesPassword(AccountData account, String password) {
        if (password == null || password.isBlank()) {
            return false;
        }
        if (account.passwordHash != null) {
            return BCrypt.checkpw(password, account.passwordHash);
        }
        return account.password != null && account.password.equals(password);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[^A-Za-z0-9].*");
    }

    public static class AccountData {
        public String pseudo;
        public String email;
        public String passwordHash;
        public String password;
        public String activeProject;

        public AccountData(String pseudo, String email, String passwordHash, String activeProject) {
            this.pseudo = pseudo;
            this.email = email;
            this.passwordHash = passwordHash;
            this.activeProject = activeProject;
        }
    }
}
