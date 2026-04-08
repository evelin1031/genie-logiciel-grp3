package org.eidd.gl.projet_genieLogiciel;

import org.eidd.gl.projet_genieLogiciel.metier.Priority;
import org.eidd.gl.projet_genieLogiciel.metier.Task;
import org.eidd.gl.projet_genieLogiciel.metier.User;
import org.eidd.gl.projet_genieLogiciel.persistance.InMemoryTaskRepository;
import org.eidd.gl.projet_genieLogiciel.persistance.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TaskRepositoryTest {

	private TaskRepository taskRepository;
	private User testUser;

	@BeforeEach
	void setup() {
		taskRepository = new InMemoryTaskRepository();
		testUser = new User("test@example.com", "Test User");
	}

	@Test
	void save_QuandAjoutUneTache_LaTachEsteAjouteeAuRepo() {
		// Arrange
		Task task = new Task("Task 1", "Description 1", Priority.HIGH, new Date(), testUser);
		
		// Act
		taskRepository.save(task);
		List<Task> tasks = taskRepository.findAll();
		
		// Assert
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		assertEquals("Task 1", tasks.get(0).getTitle());
	}

	@Test
	void findAll_QuandAucuneTache_RetourneListeVide() {
		// Act
		List<Task> tasks = taskRepository.findAll();
		
		// Assert
		assertNotNull(tasks);
		assertEquals(0, tasks.size());
	}

	@Test
	void findAll_QuandPlusieursTaches_RetourneToutesLesTaches() {
		// Arrange
		Task task1 = new Task("Task 1", "Description 1", Priority.HIGH, new Date(), testUser);
		Task task2 = new Task("Task 2", "Description 2", Priority.MEDIUM, new Date(), testUser);
		Task task3 = new Task("Task 3", "Description 3", Priority.LOW, new Date(), testUser);
		
		taskRepository.save(task1);
		taskRepository.save(task2);
		taskRepository.save(task3);
		
		// Act
		List<Task> tasks = taskRepository.findAll();
		
		// Assert
		assertNotNull(tasks);
		assertEquals(3, tasks.size());
		assertEquals("Task 1", tasks.get(0).getTitle());
		assertEquals("Task 2", tasks.get(1).getTitle());
		assertEquals("Task 3", tasks.get(2).getTitle());
	}

	@Test
	void findById_QuandTacheExiste_RetourneLaTache() {
		// Arrange
		Task task = new Task("Task 1", "Description 1", Priority.HIGH, new Date(), testUser);
		taskRepository.save(task);
		int taskId = task.getId();
		
		// Act
		Optional<Task> foundTask = taskRepository.findById(taskId);
		
		// Assert
		assertTrue(foundTask.isPresent());
		assertEquals("Task 1", foundTask.get().getTitle());
		assertEquals(taskId, foundTask.get().getId());
	}

	@Test
	void findById_QuandTacheNexistePas_RetourneOptionalVide() {
		// Act
		Optional<Task> foundTask = taskRepository.findById(999);
		
		// Assert
		assertFalse(foundTask.isPresent());
	}

	@Test
	void delete_QuandSupprimUneTache_LaTacheEstSupprimeeAuRepo() {
		// Arrange
		Task task1 = new Task("Task 1", "Description 1", Priority.HIGH, new Date(), testUser);
		Task task2 = new Task("Task 2", "Description 2", Priority.MEDIUM, new Date(), testUser);
		
		taskRepository.save(task1);
		taskRepository.save(task2);
		
		int task1Id = task1.getId();
		
		// Act
		taskRepository.delete(task1Id);
		List<Task> tasks = taskRepository.findAll();
		
		// Assert
		assertEquals(1, tasks.size());
		assertEquals("Task 2", tasks.get(0).getTitle());
		assertFalse(taskRepository.findById(task1Id).isPresent());
	}

	@Test
	void delete_QuandSupprimUneTacheQuiNexistePas_NeFaitRien() {
		// Arrange
		Task task = new Task("Task 1", "Description 1", Priority.HIGH, new Date(), testUser);
		taskRepository.save(task);
		
		// Act
		taskRepository.delete(999);
		List<Task> tasks = taskRepository.findAll();
		
		// Assert
		assertEquals(1, tasks.size());
	}
}
