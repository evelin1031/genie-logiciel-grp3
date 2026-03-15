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
	
	
	@Override
	public void save(Project project) {
		List<Project> projects = findAll();
		projects.add(project);
		writeProjects(projects);
	}
	
	// ouvre un writer, ecrit dans file le contenu de projects grace a gson, gere les erreurs
	private void writeProjects(List<Project> projects) {
		try(FileWriter writer = new FileWriter(file)){
			 gson.toJson(projects,writer);
		} catch (IOException e) {
			throw new RuntimeException("Erreur ecriture fichier");
		}
	}
	
	
	// Lis un fichier json, utilise listType pour récupérer le type des éléments, et mets tout ca dans projects
	@Override
    public List<Project> findAll(){
		
		try(FileReader reader = new FileReader(file)){
			Type listType = new TypeToken<List<Project>>() {}.getType();
			List<Project> projects = gson.fromJson(reader, listType);
			return projects != null ? projects : new ArrayList<>();
		} catch (IOException e) {
			throw new RuntimeException("Erreur lecture fichioer json");
		}
	}
	
	
	//renvoie le projet par id
	@Override
	public Project findById(int id) {
	    for (Project project : findAll()) {
	        if (project.getId() == id) {
	            return project;
	        }
	    }
	    return null; 
	}
  
	//Remoove si condition de l'id est validée
	@Override
    public void delete(int id) {
        List<Project> projects = findAll();
        projects.removeIf(project -> project.getId() == id);
        writeProjects(projects);
    }
    
}
