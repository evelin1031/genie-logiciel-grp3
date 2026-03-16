package org.eidd.gl.projet_genieLogiciel.persistance;

import java.util.ArrayList;
import java.util.List;

import org.eidd.gl.projet_genieLogiciel.metier.Project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class ProjectRepositoryJSON implements ProjectRepository{
	
    private final File file = new File("projects.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
    //Test si fichier existe, créé un reader, converti les tokens dans le le bon type et met dans projects
    @Override
    public List<Project> read() {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Project>>() {}.getType();
            List<Project> projects = gson.fromJson(reader, listType);
            return projects != null ? projects : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture fichier JSON", e);
        }
    }
    
    
	@Override
	public void save(Project project) {
		List<Project> projects = read();
		projects.add(project);
		write(projects);
	}
	
	// ouvre un writer, ecrit dans file le contenu de projects grace a gson, gere les erreurs
	@Override
    public void write(List<Project> projects) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(projects, writer);
        } catch (IOException e) {
            throw new RuntimeException("Erreur écriture fichier JSON", e);
        }
    }
	
	//Fonction pour trouver un projet par son id dans le repoJSON
	@Override
	public Project findById(int id) {
		List<Project> projects = read();
		for(Project project: projects) {
			if(project.getId() == id) {
				return project;
			}
		}
		//Si pas trouver renvoie null
		return null;
	}
	
	@Override
	public void deleteProjectById(int id) {
		List<Project> projects = read();
		projects.removeIf(project -> project.getId() == id);
		write(projects);
	}
	@Override
	public void deleteRepo() {
		file.delete();
	}
	//Todo: findByTask
	
}
