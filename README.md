<p align="center">
  <a href="" rel="noopener">
 <img width=550px height=200px src="docs-resources/raising_schrift.PNG" alt="Project logo"></a>
</p>

<h3 align="center">RAI$ING - Backend</h3>

---

<p align="center"> This is the backend part of our app Rai$ing. You can find our project deliverables <a href="https://github.com/olistaehli/raising-deliverables">here!</a>
    <br> 
</p>

## 📝 Table of Contents
1. [Installation](#install)
2. [Live Server](#live)
3. [Local Version](#local)
4. [Endpoints](#endpoints)
5. [Database Schema](#schema)
6. [Folder Structure](#folderstructure)
7. [Authors](#authors)

# Installation <a name="install">
1. Install both [OpenJDK Version 11](https://www.java.com/de/download/) and [Maven](http://maven.apache.org/download.cgi) in order to build/run the backend server. <br />
1. Clone this repository to your local machine
1. If you use [VS Code](https://code.visualstudio.com/) as your code editor, make sure to install the [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack), [Maven for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven), [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack), [Spring Boot Tools](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-spring-boot) and [Language Support for Java(TM) by Red Hat](https://marketplace.visualstudio.com/items?itemName=redhat.java) in order to comfortably work with the project

# Live server <a name="live">
A running live backend server can be found at https://33383.hostserv.eu:8080<br />
Use [Postman](https://www.postman.com/downloads/) to perform HTTP Requests against the backend server.

# Run local version of the backend <a name="local">
1. Run `mvn clean install` to install the neccessary dependencies.<br />
1. Use `mvn spring-boot:run` to start the backend server. 

# Endpoints <a name="endpoints">
An overview about the currently implemented endpoints can be found at: http://raising.herokuapp.com/docs/

# Databse Schema <a name="schema">
This schmema represents the current version of the database server located at http://33384.hostserv.eu. This address is only reachable for the webserver.
![schema.png](/docs-resources/schema.png)
  
# UML Class Diagram <a name="uml">
The following picture is the UML-Class diagram of the webserver. The original picture can be found [here](/docs-resources/raising-backend-class-diagramm.svg).
  ![raising-backend-class-diagramm.svg](/docs-resources/raising-backend-class-diagramm.svg)
  
## 📁 Folder structure <a name = "folderstructure"></a>

| Link | Content |
|---|---|
**<a href="/src/main/java/ch/raising">raising</a>** | **The source code written by us***
<a href="/src/test/java/ch/raising/test">raising-tests</a> | Unit as well as integration tests
<a href="/src/main/resources">*.properties</a> | The configuration files for Java Spring
<a href="/src/main/resources/db/migration">flyway</a> | Sql files for database-migration
<a href="/docs-resources">docs-resources</a> | The images used for this README

*This folder contains another README with additional, more precise information about the folder.

## Authors <a name="authors"/>

[Manuel Schüpbach](https://github.com/maschuep) (main developer) <br /> [Noah Schmid](https://github.com/noahschmid) (matching, assistance)
