# TALLER #3 MICROFRAMEWORK WEB

Se implementara un microframework web  similar a spark usando el lab anterior sobre la api de peliculas

## 1
Clonar el repositorio y abrir el proyecto en su IDE de preferencia.

### DESCRIPCION DEL PROYECTO

El proyecto trata de usar una API externa sobre peliculas y de como podemos leer diferentes archivos desde el disco.

Se usa java, html, css y js

una vez se corra el proyecto, escribimos en el brawser localhost:35000 

![image](https://github.com/Sebasian-Cepeda/taller2AREP/assets/89321404/2f57a261-0ef8-48c6-8f72-0afa9aaba73e)

y obtenemos la pagina inicial la cual permitira realizar una petición a la API externa dandole como parametro el nombre de una pelicula.
le damos click al botón de getMovie 

![image](https://github.com/Sebasian-Cepeda/taller2AREP/assets/89321404/8dbbfdff-d9d9-46f6-9f8c-1536be890708)

y obtenemos como resultado la pelicula que buscamos.

ahora bien, si ponemos http://localhost:35000/spark/hello obtenemos otra pestaña con esta petición GET
![image](https://github.com/Sebasian-Cepeda/taller3Arep/assets/89321404/7696f0e4-d9d6-48b7-9182-724d9a1093a5)

y si la url es incorrecta obtenemos un mensaje de error.

![image](https://github.com/Sebasian-Cepeda/taller3Arep/assets/89321404/d5d3d50e-750e-496a-a4cc-b616663d7167)

y si usamos ? en la url podemos ver como la pagina interactua con esta información de la query

![image](https://github.com/Sebasian-Cepeda/taller3Arep/assets/89321404/a70b1997-38fa-4e3d-935f-7df6265de340)


## DESARROLLADO CON

* [Java version 21](https://www.oracle.com/co/java/technologies/downloads/) - Lenguaje de programación usado.
* [Maven](https://maven.apache.org/download.cgi) - Gestor de dependencias del proyecto
* [Git](https://git-scm.com/downloads) - Gestion de versiones del proyecto
* [omdbapi](https://www.omdbapi.com) - API externa para realizar consultas


## Autor

* **Juan Sebastian Cepeda Saray
