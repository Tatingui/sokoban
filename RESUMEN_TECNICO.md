# Sokoban — Resumen técnico (patrones, decisiones y mecánicas)

Documento para el equipo: qué patrones se aplicaron, **dónde y cómo**, y —tan
importante como eso— **dónde decidimos NO usar un patrón** (o usar otra cosa) y
por qué. Sirve de base para actualizar la documentación oficial y para revisar
que los patrones estén bien aplicados.

> Regla guía de todo el proyecto: aplicar patrones donde aportan, y **no forzarlos**
> (eso es el code smell que la consigna pide evitar). Varias decisiones de abajo
> son justamente "acá un patrón sería sobre-ingeniería".

---

## 1. Arquitectura general (MVC)

Paquetes:
- **`modelo`** — dominio del juego (entidades, reglas, gestores, memento, estrategias).
- **`vista`** — Swing (`VentanaPrincipal`, `PanelTablero`, `PanelHUD`). Solo dibuja y capta input.
- **`controlador`** — `ControladorJuego` (input → comandos), `GestorDeNiveles` + `SecuenciaDeNiveles` (flujo de niveles), `EstadisticasNivel`.
- **`utilidades`** — carga y construcción de niveles (Builder + Factory).
- **`sonido`** — `GestorDeSonido` (singleton de audio).

Única librería de UI: **Java Swing**. El resto es todo JDK estándar (incluido `javax.sound.sampled` para el audio; **no** hay librerías externas).

---

## 2. Patrones GoF aplicados

| Patrón | Dónde | Clases / roles |
|---|---|---|
| **MVC** (arquitectónico) | Todo el proyecto | paquetes `modelo` / `vista` / `controlador` |
| **Memento** | Deshacer movimientos | `Tablero` (Originator), `TableroMemento` (Memento, inmutable), `ControladorJuego` (Caretaker) |
| **Observer** | Cerrojos → puertas | `Canal` (Subject), `ObservadorCanal`, `Muro` (Observer concreto), coordinado por `GestorDeCanales` |
| **Observer** | Condición de victoria | `Destino` (Subject), `ObservadorDestino`, `GestorDeVictoria` (Observer concreto) |
| **Strategy** | Comportamiento del suelo | `EstrategiaDeSuelo` (interfaz), `SueloFijo` (normal), `Deslizamiento` (resbaladizo), `ArrastreCinta` (cinta) |
| **Builder** | Construcción del tablero | `ConstructorDeTablero` (Builder), `ConstructorTablero` (ConcreteBuilder), `DirectorDeTablero` (Director); `LectorDeNivel` lee el .txt |
| **Simple Factory** + **Singleton** | Creación de entidades | `FabricaDeEntidades` (registro token→`Supplier`, instancia única) + `ParserDeToken` para tokens con parámetros |
| **Singleton** | Audio | `GestorDeSonido` |

### Detalle de cada uno

**Memento.** `Tablero.guardarEstado()` produce un `TableroMemento` inmutable (copia
defensiva de las colecciones). El `ControladorJuego` (Caretaker) guarda un snapshot
**antes** de cada movimiento y lo descarta si el movimiento no fue válido (chocar
una pared no genera snapshot). El undo (tecla **Z**) retrocede **5 pasos de una**;
con tope de **15** snapshots, eso da **máximo 3** undos seguidos. Las restauraciones
usan "rutas silenciosas" (`...Silencioso`) para no re-disparar los Observers.

**Observer (cerrojos).** Cada `Canal` (ej. AZUL) agrupa sus cerrojos y muros. Cuando
un `CasilleroCerrojo` se activa/desactiva, avisa al `Canal`; si **todos** sus cerrojos
están activos, el canal notifica a sus `Muro` (Observers) para abrirse. `Muro` es
**una sola clase con estado** (abierto/cerrado), no dos clases.

**Observer (victoria).** Cada `Destino` es Subject: cuando una caja **válida** queda
encima, notifica a `GestorDeVictoria`, que mantiene el conteo en **O(1)** y decide la
victoria. **No hay polling** recorriendo la grilla cada tecla.

**Strategy (suelos).** `EstrategiaDeSuelo` tiene dos "momentos" (ambos con default
"sin efecto", para que cada estrategia implemente solo lo suyo):
- `alLlegar(...)` — al entrar a la celda → lo usa `Deslizamiento` (resbaladizo).
- `alResolverTurno(...)` — al cerrarse el turno → lo usa `ArrastreCinta` (cinta).
Cada `Entidad` devuelve su estrategia (`getEstrategiaDeSuelo()`), `SueloFijo` por defecto.

**Builder.** `DirectorDeTablero` conoce la secuencia (`reiniciar → definirTamaño →
procesarFila×N → obtenerTablero`) y delega en el `ConstructorTablero`. `LectorDeNivel`
solo parsea el .txt a filas de tokens.

---

## 3. Decisiones de diseño: dónde NO usamos un patrón (o usamos otra cosa)

Esta sección es clave para defender el proyecto: muestra criterio, no "patrón porque sí".

- **Mirada de Sokoban → enum `Direccion`, NO State.**
  Sokoban mira en 4 direcciones (dispara el portal hacia donde mira; girar y avanzar
  son dos pulsaciones). Las 4 direcciones son **simétricas** (mismo comportamiento,
  solo cambia el vector), así que State sería una explosión de clases sin beneficio.
  Se resolvió con un **campo enum** en `Sokoban` y la regla "girar vs. avanzar" en
  `MotorMovimiento`. State se justifica cuando el comportamiento difiere por estado;
  no es el caso.

- **Gestores por composición, NO Singleton** (`GestorDeCanales`, `GestorDeVictoria`,
  `GestorDeCintas`, `GestorDePortales`). Tienen **estado del nivel**, así que hay
  **una instancia por `Tablero`** (compuesta, no global). Hacerlos Singleton rompería
  el reinicio de nivel, el paso al siguiente y los tests. **Singleton solo donde es
  stateless/servicio global:** `FabricaDeEntidades` (registro de constructores) y
  `GestorDeSonido` (servicio de audio).

- **Factory Method GoF → Simple Factory (documentado honesto).**
  `FabricaDeEntidades` es un **registro `token → Supplier`** con un único método de
  creación: elimina los if-else hardcodeados y centraliza la creación (SRP). Un
  Factory Method "de libro" daría ~10 clases creadoras y **aún así** necesitaría un
  mapa token→creador (que es una Simple Factory). Los method-references
  (`CajaNormal::new`) son los "creadores concretos" expresados como lambdas. **En la
  doc va nombrado como Simple Factory, no como Factory Method.**

- **Prototype descartado.** Las entidades (Destino, etc.) no tienen construcción
  costosa ni configuración por instancia; `new` vía la fábrica alcanza. Clonar no
  aportaba nada → habría sido patrón por el patrón.

- **Cerrojos exclusivos eliminados.** La consigna pedía 2 mecánicas extra; ya están
  **portal + cinta**, así que sacamos toda la lógica de "cerrojo exclusivo / supresión
  de canales" (más el `llaveActiva` que solo existía para sostenerla). El gameplay de
  "una sola puerta abierta a la vez" **no se pierde**: con la **llave comodín** (que es
  física y entra en un solo cerrojo a la vez) se logra el mismo toggle, más simple.

- **Llave comodín (wildcard) en vez de combinaciones multicanal.** El sprite de la
  multicanal tiene los 3 colores, y hacer sprites por cada combinación posible no
  escalaba. Entonces `CajaLlave` es **de un canal** (`L-AZUL`) **o comodín** (`L-MULTI`,
  abre cualquier cerrojo). Sin combinaciones arbitrarias.

- **Casillero intermedio = `EntidadEstaticaConOcupante`.** Los suelos especiales
  (Destino/Cerrojo/Resbaladizo/Cinta) **y el muro abierto** comparten la capacidad de
  tener un objeto encima sin perder su identidad (modelo `objetoEncima` de la consigna).
  Esto arregló el bug de "la puerta desaparecía al cruzarla".

- **`MotorMovimiento` sin `instanceof`.** El motor delega en métodos polimórficos
  (`getOcupante()`, `esEmpujable()`, `alSerEmpujada()`, `estaRota()`, `getEstrategiaDeSuelo()`)
  y en métodos de grilla de `Tablero` (`colocarDinamica` / `quitarDinamica` /
  `moverDinamica`). El único `instanceof` estructural quedó encapsulado en `Tablero`
  (dueño de la grilla).

- **Memento: qué guarda y qué no.** Guarda posición y **mirada** del jugador, cajas/
  resistencia, estado de cerrojos, canales y muros. **No** guarda los **portales** (su
  posición persiste al deshacer, como un estado de "apuntado"). Los contadores de
  **empujes** y **usos de deshacer** son **monótonos** (no se revierten con el undo,
  para que penalicen el puntaje); los **movimientos** sí se restauran.

---

## 4. Mecánicas implementadas (cómo funcionan)

- **Movimiento + mirada.** `MotorMovimiento.intentarMover` devuelve un
  `ResultadoMovimiento` (`GIRO` / `CAMINATA` / `EMPUJE` / `SIN_CAMBIO`). Primer toque a
  una dirección nueva = solo **gira** (no cuenta ni genera snapshot); el segundo avanza.

- **Caja frágil.** Pierde 1 de resistencia por empuje; el **sprite** refleja la vida
  (sana → dañada → muy dañada) y al llegar a 0 desaparece.

- **Resbaladizo (RF8).** "Solo sobre el hielo": desliza mientras pisa hielo y frena al
  **salir del hielo** (primer suelo normal) o al **chocar** algo sólido. Implementado por
  **delegación**: cada estrategia, tras avanzar una celda, delega en la del suelo
  siguiente (si es hielo, sigue; si no, `SueloFijo` lo deja ahí).

- **Cinta transportadora.** En cada movimiento válido, el `GestorDeCintas` arrastra **un
  paso** a cada ocupante de cinta en su dirección (con "foto" previa para que nadie se
  mueva dos veces por turno). Token `T-ARRIBA/ABAJO/IZQUIERDA/DERECHA`.

- **Portales.** Click **izquierdo** = portal azul, **derecho** = naranja, en la dirección
  que mira Sokoban. El disparo es un ray-cast que **atraviesa cajas y puertas** y frena
  **solo contra paredes**. El portal vive en la **arista** de la celda (no en la grilla:
  lo maneja `GestorDePortales`). Teletransporta a Sokoban y cajas; podés **seguir a la
  caja** empujándola al emerger; **no** se pueden poner dos portales en la misma arista.

- **Victoria / niveles / puntaje.** `Main` corre 3 niveles encadenados; al ganar, pantalla
  con movimientos, **empujes**, **usos de deshacer** y **puntaje**. El puntaje
  (`CalculadoraDePuntaje`) baja con más tiempo, movimientos y deshacer. El HUD muestra
  tiempo, movimientos, empujes, cajas, retrocesos, puntaje y nivel.

- **Sonido (`GestorDeSonido`, Singleton).** Música de fondo en bucle; efectos al caminar,
  mover caja/llave, colocar caja válida en destino o llave en cerrojo (no suena con
  colocaciones inválidas), disparar portal y ganar. La detección de "colocación correcta"
  se hace por **conteo antes/después** en el controlador, **sin** acoplar sonido al modelo.

---

## 5. Tokens del archivo de nivel (.txt)

`P` pared · `S` suelo · `J` jugador · `C` caja · `F` caja frágil · `D` destino ·
`R` resbaladizo · `L-CANAL` llave de canal · `L-MULTI` llave comodín ·
`X-CANAL` cerrojo · `M-CANAL` muro cerrado · `A-CANAL` muro abierto ·
`T-DIR` cinta (DIR = ARRIBA/ABAJO/IZQUIERDA/DERECHA). Canales con sprite: AZUL, NARANJA, VERDE.

Los portales **no** van en el .txt: se disparan en runtime con el mouse.

---

## 6. Tests (manuales, sin framework, en `test/pruebas`)

`PruebaMemento`, `PruebaMovimiento`, `PruebaSueloResbaladizo`, `PruebaCintaTransportadora`,
`PruebaPortales`, `PruebaObserverCerrojos`, `PruebaObserverVictoria`, `PruebaPuertaPersistente`,
`PruebaConstruccionNivel`. Cubren cada patrón/mecánica y su interacción con el Memento.

---

## 7. Pendientes / a revisar

- **Documentación oficial** (README + PDF): sacar menciones a cerrojos exclusivos,
  sumar comodín, cinta, portal, puntaje, mirada-enum, etc. (este resumen es la base).
- **Música OST**: debe estar en `.wav` (Java estándar no reproduce MP3). Ya convertida.
- **Cintas en esquina** (sprites de doblez): hoy son cintas **rectas** direccionales;
  las que doblan quedarían como mejora.
- Opcionales: animación de caminata de Sokoban, 9-slice de paredes/hielo.

---

## 8. Cómo correrlo

- Juego (3 niveles): clase `Main`.
- Sandbox (todos los elementos en un nivel): clase `MainSandbox`.
- Test de audio aislado: clase `MainTestSonido`.
- Requiere JDK 17+. Directorio de trabajo = carpeta del proyecto (`sokoban/`).
