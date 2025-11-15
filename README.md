# Système de Gestion Optimisée de Tournées de Livraison - V2.0

## 1. Contexte

Ce projet est la version 2.0 du **Système de Gestion de Tournées**.  
Il vise à optimiser les tournées de livraison en intégrant des fonctionnalités avancées, notamment l'utilisation de l'IA pour proposer l'ordre optimal des livraisons, tout en gardant une architecture robuste et évolutive.

**Objectifs principaux :**
- Gestion complète des clients et de leurs livraisons.
- Historisation des livraisons avec analyse des délais.
- Algorithme d’optimisation basé sur Spring AI.
- Pagination et recherche avancée pour gérer de gros volumes de données.
- Utilisation de Liquibase pour gérer les migrations de la base de données.
- Configuration multi-environnements via YAML.
- Tests unitaires et d’intégration pour garantir la qualité du code.

---

## 2. Nouvelles Entités

### Customer
- `id` : Identifiant unique
- `name` : Nom du client
- `address` : Adresse
- `latitude` / `longitude` : Coordonnées GPS
- `preferredTimeSlot` : Plage horaire préférée (ex: "09:00-11:00")
- Relations : 1 Customer → N Deliveries

### DeliveryHistory
- `id` : Identifiant unique
- `customer` : Référence vers le client
- `tour` : Référence vers le tour
- `deliveryDate` : Date de livraison
- `plannedTime` / `actualTime` : Heures planifiées et réelles
- `delay` : Durée de retard (planned vs actual)
- `dayOfWeek` : Jour de la semaine
- Créé automatiquement lors du passage d’un tour en `COMPLETED`

---

## 3. Base de Données et Liquibase

- Migration gérée avec **Liquibase**
- Changelogs :
  - `db.changelog-master.xml`
  - `db.changelog-v1.0-initial.xml` (V1)
  - `db.changelog-v2.0-new-entities.xml` (Customer & DeliveryHistory)
- Chaque changeset documente :
  - Qui a effectué la modification
  - Quelle modification
  - Date
- Exemple de rollback fonctionnel inclus

---

## 4. Configuration YAML

- Fichier principal : `application.yml`
- Environnements :
  - `application-dev.yml` : Base H2
  - `application-qa.yml` : Base relationnelle (PostgreSQL/MySQL)
- Configuration lisible et structurée
- Activation conditionnelle des optimizers via `optimizer.type`

---

## 5. AI Optimizer

- Implémente un algorithme basé sur Spring AI pour proposer l’ordre optimal des livraisons.
- Analyse l’historique des livraisons (`DeliveryHistory`) pour détecter les patterns.
- Sortie : JSON avec :
  - Liste des livraisons ordonnées
  - Recommandations pour l’optimisation
- Activation via `@ConditionalOnProperty(name="optimizer.type", havingValue="AI")`

---

## 6. Pagination et Recherche Avancée

- Pagination via Spring Data JPA (`Pageable`)
- Recherche avancée via méthodes dérivées et `@Query`
- Gestion efficace des gros volumes de données

---

## 7. Tests

- Tests unitaires pour les services et repositories
- Test d’intégration minimum pour vérifier la création et la récupération des clients

---

## 8. Technologies Utilisées

- **Java 17+**
- **Spring Boot 3**
- **Spring Data JPA**
- **Spring AI**
- **Liquibase**
- **H2 / PostgreSQL**
- **JUnit 5**
- **Maven**

---

## 9. Démarrage du Projet

### Prérequis
- Java 17+
- Maven 3+
- Git

### Commandes
```bash
# Cloner le dépôt
git clone <repo-url> livraison-v2
cd livraison-v2

# Lancer en développement (H2)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Lancer tests
mvn test

# Appliquer les migrations Liquibase
mvn liquibase:update -Dspring-boot.run.profiles=dev
