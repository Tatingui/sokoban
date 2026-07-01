# Sokoban - Trabajo Integrador PDS

Proyecto de desarrollo para la asignatura Proceso de Desarrollo de Software (UADE). Este sistema implementa el clásico juego Sokoban enfocando su diseño en la correcta aplicación de patrones de diseño GoF, principios SOLID y la arquitectura estructural Modelo-Vista-Controlador (MVC).

ACLARACION PARA PROFE PEPE
Nos motivo mucho el desarrollo de este proyecto, y lo quisimos seguir por fuera de la materia. Para no romper lo que hoy funciona, creamos la rama llamada "Avance en paralelo a PDS", en la que estamos agregandole algunas implementaciones quiza poco cristianas ante los fundamentos de la materia de Proceso Desarrollo de Software.
La rama "main" tiene la version que usted tiene que importar y probar, ya que esa misma es la que lleva la ultima version de lo que fue acomplejado para la entrega de nuestro TPI. 

## Arquitectura y Patrones Creacionales Aplicados

El proyecto se encuentra dividido en los paquetes `modelo`, `vista`, `controlador` y `utilidades` respetando estrictamente el patrón arquitectónico MVC. Para la inicialización del juego se implementó una infraestructura creacional compuesta por:
* **Singleton:** Aplicado en la fábrica para garantizar un único punto de acceso y evitar instancias repetidas en memoria.
* **Factory Method:** Centraliza la lógica de conversión de caracteres en entidades del modelo.
* **Builder:** Separa la lectura e interpretación del archivo de texto plano de la representación final del objeto complejo Tablero.

## Patrón de Comportamiento Aplicado

Para el sistema de llaves, cerrojos y muros se aplicó el patrón **Observer**, desacoplando los muros de la lógica que decide cuándo deben abrirse:

* **`Canal` (Subject):** Agrupa los cerrojos y muros de un mismo canal identificado (ej: `AZUL`). Reevalúa su estado ante cada cambio y notifica únicamente cuando hay una transición real (abrir/cerrar), evitando notificaciones redundantes.
* **`ObservadorCanal` (Observer):** Interfaz con `alAbrirCanal()` / `alCerrarCanal()`.
* **`Muro` (Concrete Observer):** Muta su transitabilidad al ser notificado. Una única clase con estado reemplaza a las antiguas `MuroCerrado` y `MuroAbierto`, eliminando la duplicación.
* **`CasilleroCerrojo`:** Reporta su activación al canal cuando se le coloca encima una `CajaLlave` del mismo canal. Soporta modo exclusivo.
* **`GestorDeCanales`:** Centraliza los canales del tablero (`cerrojosPorCanal` / `murosPorCanal`) y coordina la supresión de otros canales en los cerrojos exclusivos.

Regla de apertura: un canal abre sus muros sólo cuando **todos** sus cerrojos están activados; si un cerrojo se desactiva, los muros vuelven a cerrarse.

## Guía para la Creación de Mapas Propios

El sistema es capaz de procesar cualquier mapa personalizado estructurado en un archivo de texto plano (.txt). Para que el juego interprete correctamente los elementos, se debe respetar el siguiente diccionario de caracteres:

Cada celda se escribe como un token separado por espacios. Los tokens simples son una sola letra; el sistema de llaves usa tokens compuestos con el canal (ej: `AZUL`, `ROJA`, `VERDE`).

**Tokens simples:**

* `P` : Pared (Bloque estático que delimita el mapa y no puede ser atravesado)
* `S` : Suelo (Espacio vacío por el cual el jugador y las cajas se mueven libremente)
* `J` : Jugador (Representa la posición inicial de Sokoban)
* `C` : Caja normal (Bloque estándar que debe ser empujado hacia los destinos)
* `D` : Casilla destino (Ubicación donde deben colocarse las cajas para ganar)
* `F` : Caja frágil (Caja con límite de 3 empujes, el tercero la rompe y desaparece)
* `R` : Terreno resbaladizo (Superficie de hielo que desliza los objetos hasta un tope)
* `T-[DIRECCION]` : Cinta transportadora. Arrastra una celda en la dirección indicada tras cada turno. Direcciones válidas: `ARRIBA`, `ABAJO`, `IZQUIERDA`, `DERECHA` (ej: `T-ABAJO`).

**Tokens por canal (sistema llave-cerrojo-muro):**

* `L-[CANAL]` : Caja llave de un canal (ej: `L-AZUL`). Para crear una llave comodín universal se utiliza `L-MULTI`.
* `X-[CANAL]` : Casillero cerrojo (ej: `X-AZUL`).
* `M-[CANAL]` : Muro cerrado (ej: `M-AZUL`). También se acepta el formato legado `MC-[CANAL]`.
* `A-[CANAL]` : Muro abierto (ej: `A-AZUL`).

> Nota: `Muro` es una única entidad con estado. El mismo objeto pasa de cerrado a abierto cuando su canal se activa (patrón Observer); no son dos clases distintas.

### Requisito importante para mapas personalizados:
Todos los archivos de nivel deben guardarse dentro del directorio `/niveles`. Además, para evitar errores de desbordamiento en la matriz, el mapa debe ser **perfectamente rectangular** (todas las filas deben poseer exactamente la misma cantidad de tokens, separados por un solo espacio). 

Para que el juego cargue tu propio mapa, podés simplemente reemplazar el contenido de alguno de los archivos existentes (como `niveles/nivel3.txt`), o bien crear un archivo nuevo y agregarlo a la lista `List.of(...)` en el archivo `src/Main.java`.

## Instrucciones de Ejecución

Requisito común: tener instalado un **JDK 17 o superior** (el proyecto se valida con JDK 21).

> Importante: `Main.java` resuelve las rutas de niveles e imágenes como `user.dir + "/sokoban/..."`, por lo que el **directorio de trabajo del proceso debe ser la carpeta que contiene al repositorio** (la carpeta padre de `sokoban`).

### IntelliJ IDEA
1. Clonar el repositorio y abrirlo en IntelliJ IDEA.
2. Configurar el SDK del proyecto en JDK 17+.
3. Ejecutar la clase `Main.java` ubicada en `src/`.

### Visual Studio Code
1. Instalar el **Extension Pack for Java** (Microsoft).
2. Abrir la carpeta del repositorio.
3. Abrir `src/Main.java` y usar **Run** (▶) sobre `main`, o presionar `F5`.

### Por línea de comandos
Compilar dentro del repo y ejecutar (asegurándose que el directorio de trabajo base sea la raíz del proyecto para que lea la carpeta public):
```bash
# parado en la carpeta del repo (sokoban/)
javac -d bin $(find src -name "*.java")
# ejecutar utilizando bin como classpath
java -cp bin Main
```
