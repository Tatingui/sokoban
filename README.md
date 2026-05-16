# Sokoban - Trabajo Integrador PDS

Proyecto de desarrollo para la asignatura Proceso de Desarrollo de Software (UADE). Este sistema implementa el clásico juego Sokoban enfocando su diseño en la correcta aplicación de patrones de diseño GoF, principios SOLID y la arquitectura estructural Modelo-Vista-Controlador (MVC).

## Arquitectura y Patrones Creacionales Aplicados

El proyecto se encuentra dividido en los paquetes `modelo`, `vista`, `controlador` y `utilidades` respetando estrictamente el patrón arquitectónico MVC. Para la inicialización del juego se implementó una infraestructura creacional compuesta por:
* **Singleton:** Aplicado en la fábrica para garantizar un único punto de acceso y evitar instancias repetidas en memoria.
* **Factory Method:** Centraliza la lógica de conversión de caracteres en entidades del modelo.
* **Builder:** Separa la lectura e interpretación del archivo de texto plano de la representación final del objeto complejo Tablero.

## Guía para la Creación de Mapas Propios

El sistema es capaz de procesar cualquier mapa personalizado estructurado en un archivo de texto plano (.txt). Para que el juego interprete correctamente los elementos, se debe respetar el siguiente diccionario de caracteres:

* `P` : Pared (Bloque estático que delimita el mapa y no puede ser atravesado)
* `S` : Suelo (Espacio vacío por el cual el jugador y las cajas se mueven libremente)
* `J` : Jugador (Representa la posición inicial de Sokoban)
* `C` : Caja normal (Bloque estándar que debe ser empujado hacia los destinos)
* `D` : Casilla destino (Ubicación donde deben colocarse las cajas para ganar)
* `F` : Caja frágil (Caja con límite de desplazamientos)
* `L` : Caja llave (Caja especial utilizada para abrir casilleros cerrojo)
* `R` : Terreno resbaladizo (Superficie que desliza los objetos hasta un tope)
* `X` : Casillero cerrojo (Bloqueo que se libera al superponerle la caja llave)
* `M` : Muro cerrado / `A` : Muro abierto (Puertas temporales)

### Requisito importante para mapas personalizados:
Todos los archivos de nivel deben guardarse dentro del directorio `/niveles`. Además, para evitar errores de desbordamiento en la matriz, el mapa debe ser perfectamente rectangular (todas las líneas del archivo de texto deben poseer exactamente la misma cantidad de caracteres).

## Instrucciones de Ejecución

1. Clonar el repositorio localmente.
2. Abrir el proyecto en IntelliJ IDEA.
3. Asegurarse de tener configurado un JDK versión 17 o superior.
4. Ejecutar la clase `Main.java` ubicada en la raíz del directorio `src`.
