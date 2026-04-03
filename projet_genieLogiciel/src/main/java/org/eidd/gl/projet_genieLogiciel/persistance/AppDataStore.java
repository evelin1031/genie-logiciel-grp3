package org.eidd.gl.projet_genieLogiciel.persistance;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.eidd.gl.projet_genieLogiciel.service.UserService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class AppDataStore {
    // gere la lecture et l ecriture de app data json
    private static final File FILE = new File("app-data.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public List<UserService.AccountData> readAccounts() {
        AppData data = read();
        return data.accounts == null ? new ArrayList<>() : data.accounts;
    }

    // remplace la partie comptes du fichier partage
    public void writeAccounts(List<UserService.AccountData> accounts) {
        AppData data = read();
        data.accounts = accounts;
        write(data);
    }

    public List<ProjectData> readProjects() {
        AppData data = read();
        return data.projects == null ? new ArrayList<>() : data.projects;
    }

    // remplace la partie projets du fichier partage
    public void writeProjects(List<ProjectData> projects) {
        AppData data = read();
        data.projects = projects;
        write(data);
    }

    // lit tout le contenu de app data json
    private AppData read() {
        if (!FILE.exists() || FILE.length() == 0) {
            return new AppData();
        }
        try (FileReader reader = new FileReader(FILE)) {
            Type type = new TypeToken<AppData>() {}.getType();
            AppData data = GSON.fromJson(reader, type);
            return data == null ? new AppData() : data;
        } catch (IOException e) {
            return new AppData();
        }
    }

    // reecrit tout le contenu de app data json
    private void write(AppData data) {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.out.println("Sauvegarde impossible.");
        }
    }

    private static class AppData {
        private List<UserService.AccountData> accounts;
        private List<ProjectData> projects;
    }

    public static class ProjectData {
        public String name;
        public List<String> members;
        public List<TaskData> tasks;
    }

    public static class TaskData {
        public String title;
        public String description;
        public String status;
        public String priority;
        public String creatorEmail;
        public String creatorPseudo;
        public String assignedEmail;
        public String assignedPseudo;
        public long deadline;
    }
}
