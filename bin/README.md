# Exemple d'une application d'authentification utilisant Spring Boot, JWT, Spring Security & Spring Data JPA, Swagger

## Dependency
– Pour utiliser PostgreSQL ajouter dans le pom.xml:

<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>

 – Ou bien pour utiliser MySQL ajouter dans le pom.xml:

<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <scope>runtime</scope>
</dependency>



## Preparation base de données

Création des containers MySQL et PhpMyadmin
```
docker run --name cabd_mysql -e MYSQL_ROOT_PASSWORD=ddstssm -p 3306:3306 -d mysql:8.0.1
docker run --name cabd_phpmyadmin -d --link cabd_mysql:db -p 8081:80 phpmyadmin/phpmyadmin

```

Se connecter à phpmyadmin en mettant la valeur du user "root" et la valeur du mot de passe "ddstssm" l'url vers phpmyadmin étant http://localhost:8081

Créer la base de données testdb
```

create database testdb;

Insérer les roles
```

INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```

## Configurer Spring Datasource, JPA, App properties
Ouvrir le fichier `src/main/resources/application.properties`
- Si vous avez opté pour PostgreSQL ajouter les entrées suivantes:

spring.datasource.url= jdbc:postgresql://localhost:5432/testdb
spring.datasource.username= postgres
spring.datasource.password= 123

spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation= true
spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.PostgreSQLDialect

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto= update

# App Properties
cabd.app.jwtSecret= cabdSecretKey
cabd.app.jwtExpirationMs= 86400000
```
- Si vous avez opté pour MySQL ajouter les entrées suivantes
```
spring.datasource.url= jdbc:mysql://localhost:3306/testdb?useSSL=false
spring.datasource.username= root
spring.datasource.password= ddstssm

spring.jpa.properties.hibernate.dialect= org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto= update

# App Properties
cabd.app.jwtSecret= cabdSecretKey
cabd.app.jwtExpirationMs= 86400000
```
## Run Spring Boot application
```
mvn spring-boot:run
```

