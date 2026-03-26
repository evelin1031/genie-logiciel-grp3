![CI](https://github.com/dohabentaoussy/genie-logiciel-grp3/actions/workflows/ci.yml/badge.svg)

# genie-logiciel-grp3
Group Project

## Team

- Eve Lin
- Meyssem Soussou
- Doha Bentaoussy
- Hugo Lege

## Sujet 1 : Gestionnaire de tâches

### Contexte

Une équipe a besoin d'un outil pour organiser son travail. L’outil permet de 
créer des tâches, de suivre leur avancement et de les organiser (ex : Jira | Trello).

### Fonctionnalités obligatoires
- Créer, modifier et supprimer des tâches (titre, description, priorité…)
- Gérer les statuts (To do, in progress, done)
- Lister les tâches avec filtre par statut

### Fonctionnalités bonus
- Assigner une tâche à un utilisateur
- Filtres supplémentaires (par priorité, par deadline)
- Deadline avec alerte

### Répartition

#### Meyssem :

- Task.java
- TaskRepository.java
- TaskService.java
- TaskTest.java
- TaskRepositoryTest.java
- TaskServiceTest.java


#### Doha : 

- User.java
- UserRepository.java
- UserService.java
- UserServiceTest.java
- InMemoryUserRepository.java


#### Hugo : 

- Project.java
- ProjectRepository.java
- ProjectRepositoryTest.java
- ProjectRepositoryJSON.java
- ProjectService.java


#### Eve : 

- Priority.java
- TaskStatus.java
- Main.java
- StatisticsService.java
- StatisticsServiceTest.java
