<p align="center">
  <a href="" rel="noopener">
 <img width=550px height=200px src="../../../../../docs-resources/raising_schrift.PNG" alt="Project logo"></a>
</p>

<h3 align="center">RAI$ING - Backend (Source Code)</h3>

---

<p align="center"> In this folder you will find the code written by the Rai$ing development team.
    <br> 
</p>

## 📝 Table of Contents
- [📝 Table of Contents](#-table-of-contents)
- [🏁 Code structure <a name = "code_structure"></a>](#-code-structure)

## 🏁 Code structure <a name = "code_structure"></a>
| Path | Content |
|---|---|
config | This folder contains all global configuration for Spring
controllers | This folder contains the classes that controll all the backend endpoints
data | The data folder contains the repositories which are used to interact with the database
interfaces | The interfaces folder contains interfaces for models and repositories
models | The models folder contains the data models used for communication with the frontend
raisingbackend | This folder contains the main function of the backend application
services | This folder contains services which implement the functionality used by the controllers. They heavily depend on the repositories.
utils | The util folder contains useful classes that are not services (like filters, exceptions, static methods)
