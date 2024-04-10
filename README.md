## Requirements
In order to run the application you will need Maven project management tool available under the link: https://maven.apache.org/.
Application has been written in Java with jdk 17 in Spring boot 3.2.4. This version or never is required to start application. Application uses
in memory H2 database. 
## Setup
### Start application
You can start application by executing command from root project location:
````
mvn spring-boot:run
````
This command should run spring boot application with listening to the port 8080 and should be available 
under the link host: http://localhost:8080/. You can change if you need port to another by add property:
````
server.port=port
```` 
in `src/main/resources/application.properties`


### Build jar package
````
mvn clean install
````
executed from root of the project should create jar package in location : `target/scoreboard-0.0.1-SNAPSHOT.jar`
and run all possible tests: unit, integration, MockMvc.
### Sample data
During starting web server application creates tables in h2 in memory database based on JPA entity definitions.
It's configured by setting in `src/main/resources/application.properties` key value pair: 
```
spring.jpa.hibernate.ddl-auto=update
```
In next steps sample data are injected into database from file: `src/main/resources/data.sql`
### H2 database console
In memory h2 database console is available under the link:
````
http://localhost:8080/h2-console/login
```` 
In case JDBC URL, User Name, Password are not set after entering h2 console url, then they should have value values:
```
JDBC URL: jdbc:h2:mem:testdb
User Name: sa
Password : 
```
(Password is empty value).


### Run only tests
You can execute all tests of the application only without starting server and building package by executing command:
````
mvn clean test
````

### Assumptions
- in root page `/` or `/board` user should see the board with sample games with different statuses
- User can perform actions like: Add new game, Start already created game, finish game or filter scores by sum of home and away team scores 
- Every game is associated with one of 3 statuses: NEW, IN_PROGRESS, FINISHED defined in `src/main/java/kmichalski/scoreboard/model/GameStatus.java`
- Once user finish the game then it's removed from the board. Under the hood status is changed to FINISHED. Board shows games with NEW or IN_PROGRESS statuses
- Summary by total scores filters Games among games with IN_PROGRESS status only and is handled by passing query string `total_score`. For example: http://localhost:8080/?total_score=2 
will show all IN_PROGRESS games where total score is equal 2 
- Clear button causes deletion of total_score filter and renders board without filtering: http://localhost:8080/ - not started 
games with status NEW should be visible again
- In GameRepository @EntityGraph annotation has been applied in order to optimize sql queries, avoid N+1 problem
