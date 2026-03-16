package org.eidd.gl.projet_genieLogiciel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.eidd.gl.projet_genieLogiciel.metier.Priority;
import org.eidd.gl.projet_genieLogiciel.metier.Project;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.persistance.ProjectRepositoryJSON;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProjectRepositoryTest {
	ProjectRepositoryJSON repoFileEnJSON;
	
	//Init des objets qu'on va avoir besoin pour tester un repo de projet
	@BeforeEach
	void setup() {
		repoFileEnJSON = new ProjectRepositoryJSON();
	}
	
	//Les test en AAA
	@Test
	void read_QuandFichierContientProjets_RetourneListeProjets() {
		// arrange: prep donnees
		Project projetCalculatrice = new Project("Calculatrice","Listes de taches a faire, objectifs et autre pour faire une calculatrice en Java");
		Project projetFermeAnimaux = new Project("Ma superbe ferme d'animaux","Listes de taches a faire, objectifs et autre pour representer ma superbe ferme d'animaux en Java");

		Task tacheFaire_Architecture = new Task("Faire Archi", Priority.HIGH);
		Task tacheFaire_CleanCode = new Task("Faire CleanCode", Priority.MEDIUM);
		
		
		projetCalculatrice.addTask(tacheFaire_CleanCode);
		projetCalculatrice.addTask(tacheFaire_Architecture);
		
		projetFermeAnimaux.addTask(tacheFaire_Architecture);
		
		repoFileEnJSON.save(projetCalculatrice);
		repoFileEnJSON.save(projetFermeAnimaux);
		 
		// act
		List<Project> projects = repoFileEnJSON.read();
		
		// Assert
		assertEquals(2, projects.size());
	    assertEquals("Calculatrice", projects.get(0).getName());
	    assertEquals("Ma superbe ferme d'animaux", projects.get(1).getName());	
	}
	
	/* TODO tests a faire:
    void write(List<Project> projects);
    void save(Project project);
    Project findById(int id);
    void deleteProjectById(int id);
    */
	
	@AfterEach
	void nettoyage() {
		repoFileEnJSON.deleteRepo();
	}
	
}
