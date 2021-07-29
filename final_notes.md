
He implementado una arquitectura MVVM. He querido hacer una app lo más actualizada posible en cuanto a lo que llevo estudiando estos meses en tecnologías y arquitectura, para que en caso de que tomeis la decisión de incorporarme al equipo pueda ser lo más rentable posible. He tardado más de lo que me gustaría porque al ser muchas cosas nuevas para mí aún no tengo una plantilla a mi gusto que incluya todo lo que considero básico a estas alturas. Se han implementado todos los objetivos.

El proyecto consta de 4 módulos:
- app: Es el módulo principal y contiene la parte de la vista, inyección de dependencias, servicio de sincronización con la api, y viewmodel.

- data: Contiene los interactors de rxJava, el repositorio y la interfaz y factoría de datastores (que en realidad con koin se convierte en un proveedor de estos) para el manejo de los objetos de negocio, y la gestión de hilos con el executor.

- framework: En él se encuentra la gestión de los objetos de negocio. Está la base de datos, el dao, los mapper para BD y para el servicio remoto, la implementación de los datastore para cada caso, y los datos necesarios para las llamadas al servicio y su factoría.

- model: Tiene los objetos del modelo, en este caso sólo está la clase Repo.


## Tecnologías utilizadas
Room, Koin, rxJava3, Retrofit, Glide, jUnit, Truth.


## Notas
La parte de testing aún me queda mucho por aprender, pero al menos he incluído algunas pruebas en el módulo framework, que es el que considero más importante comprobar. Los tests se centran en la obtención de datos de BD, del servidor y en el funcionamiento de los mapper, y se encuentran en el fichero DatabaseAndRemoteTest.kt.


## Bugs encontrados
Al desplazar la lista hacia arriba y realizarse la carga de más datos se reinicia a su posición inicial. Por tiempo no me he querido detener en esto.
