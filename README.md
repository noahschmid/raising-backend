# Installation
- Install both [OpenJDK Version 8](https://www.java.com/de/download/) and [Maven](http://maven.apache.org/download.cgi) in order to build/run the backend server. <br />
- Clone this repository to your local machine
- If you use [VS Code](https://code.visualstudio.com/) as your code editor, make sure to install the [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack), [Maven for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven), [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack), [Spring Boot Tools](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-spring-boot) and [Language Support for Java(TM) by Red Hat](https://marketplace.visualstudio.com/items?itemName=redhat.java) in order to comfortably work with the project

# Run local version of the backend
Run `mvn clean install` to install the neccessary dependencies.<br />
Use `mvn spring-boot:run` to start the backend server. 

# Live server
A running live backend server can be found at https://33383.hostserv.eu:8080<br />
Use [Postman](https://www.postman.com/downloads/) to perform HTTP Requests against the backend server.

# Endpoints
An overview about the currently implemented endpoints can be found at: http://raising.herokuapp.com/docs/
