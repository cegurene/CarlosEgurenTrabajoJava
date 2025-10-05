# Simulación de una colonia de hormigas

Este repositorio contiene una práctica Java que simula el comportamiento de una colonia de hormigas con hilos, sincronización y una interfaz gráfica. Además incluye una parte distribuida (`RMI`) para exponer métricas en una segunda interfaz cliente.

A continuación hay una descripción detallada de la estructura del proyecto y el contenido de cada archivo.

## Resumen rápido

- Paquetes principales: `entidades`, `interfaz`, `ParteDistribuida`.
- Ejecutables principales (clases con `main`): `interfaz.Interfaz` (GUI principal que inicia la colonia y el RMI registry) y `interfaz.InterfazParte2` (cliente RMI para mostrar métricas).
- Logging: en `historial/evolucionColonia.txt` mediante `FileManager`.

---

## Estructura del proyecto (resumida)

- `pom.xml` — fichero Maven (presente en la raíz).
- `src/entidades/`
  - `Colonia.java`
  - `FileManager.java`
  - `HormigaCria.java`
  - `HormigaObrera.java`
  - `HormigaSoldado.java`
  - `ListaHormigas.java`
  - `Paso.java`
- `src/interfaz/`
  - `Interfaz.java` (GUI principal)
  - `InterfazParte2.java` (cliente RMI GUI)
- `src/ParteDistribuida/`
  - `EnvioValores.java` (objeto remoto que expone métricas)
  - `InterfaceObtenerValores.java` (interfaz RMI)
- `historial/` (salida del programa): `evolucionColonia.txt`

---

## Descripción de cada archivo

### src/entidades/Colonia.java
Propósito:
- Clase núcleo que modela la colonia y centraliza la lógica de sincronización entre hormigas (threads).

Responsabilidad principal:
- Mantener listas de hormigas en diferentes zonas (almacén, instrucción, descanso, refugio, zona de comer, insecto, buscando).
- Gestionar contadores de comida (`numeroComidaAlmacen`, `numeroComidaZonaComer`) y número de hormigas en el almacén.
- Coordinar operaciones concurrentes usando `Semaphore`, `Lock` (`ReentrantLock`), `Condition` y `CyclicBarrier`.
- Interactuar con `EnvioValores` para actualizar el estado que se expone por RMI.
- Guardar eventos en el historial mediante `FileManager`.

Métodos y comportamiento clave:
- `entrar(String idStr)` / `salir(String idStr)`: gestión de entradas/salidas con semáforos y registro en fichero.
- `almacen(...)`, `zonaComer(...)`, `instruccion(...)`, `descanso(...)`, `refugio(...)`, `lucharAmenaza(...)`: operaciones que simulan cada actividad de las hormigas (incluyen sleeps aleatorios para modelar tiempo de trabajo).
- `actualizarHormigas*` y `actualizarComida*`: actualizan las listas y los campos de texto asociados en la GUI.
- `comprobarTipoHormmiga(String id)`: determina si una hormiga es cria/soldado/obrera por convención en `id`.
- `barreraAmenaza` (CyclicBarrier) coordina a los soldados en caso de invasión.

---

### src/entidades/FileManager.java
Propósito:
- Registrar el historial de la simulación en `historial/evolucionColonia.txt`.

Detalles:
- Usa `FileWriter` y `PrintWriter` para añadir líneas al fichero.
- Protege el acceso con un `ReentrantLock` (`cerrojo`) para evitar escritura concurrente.
- `formatearFechaHora(Date fecha)`: formatea la fecha (ATENCIÓN: el patrón usado es `"dd-mm-yyyy hh.mm.ss: "` — esto es un error/bug: en `SimpleDateFormat` `mm` son minutos y `MM` son meses. Por tanto la fecha no se formatea como se espera).
- `tipoHormiga(String idStr)`: devuelve la etiqueta de tipo de hormiga según el id.

Resultado:
- Archivo generado: `historial/evolucionColonia.txt` en el directorio de trabajo (`System.getProperty("user.dir") + "\\historial\\"`).

---

### src/entidades/HormigaCria.java
Propósito:
- Implementa una hormiga de tipo "cría" como `Thread` con su comportamiento específico.

Comportamiento:
- Construye `idStr` con el prefijo `HC...` según el número.
- En `run()`: entra en la colonia y alterna ciclos de `zonaComer(3,5)` y `descanso(4)`.
- Si se produce una excepción (por ejemplo interrupción por amenaza), invoca `colonia.refugio(idStr)`.

Interacciones:
- Llama a métodos de `Colonia` para sincronizar.
- Actualiza `EnvioValores` indirectamente a través de `Colonia`.

---

### src/entidades/HormigaObrera.java
Propósito:
- Hormiga obrera implementada como `Thread`. Su comportamiento varía según si su id es par o impar.

Comportamiento:
- Construye `idStr` con prefijo `HO...`.
- Si su id es par: realiza una rutina de sacar comida del almacén y llevarla a la zona de comer.
- Si es impar: sale al exterior, busca comida (simulación con sleep), vuelve, entra al almacén a dejar comida.
- Cada 10 iteraciones realiza una breve comida y descanso.

Interacciones:
- Usa `EnvioValores` para actualizar contadores remotos (obreras interior/exterior).

---

### src/entidades/HormigaSoldado.java
Propósito:
- Hormiga soldado como `Thread`. Alterna entre instrucción y descanso; participa en la defensa ante una invasión.

Comportamiento:
- Construye `idStr` con prefijo `HS...`.
- En su bucle, cada 6 iteraciones come, en las demás realiza instrucción (`instruccion(...)`) y descanso.
- Si detecta interrupción, invoca `colonia.lucharAmenaza(idStr)` para coordinar defensa.

Interacciones:
- Participa en la `CyclicBarrier` usada en `Colonia` para sincronizar soldados durante la invasión.

---

### src/entidades/ListaHormigas.java
Propósito:
- Estructura auxiliar para mantener listas de identificadores (`ArrayList<String> lista`) y (en otra variante) lista de objetos `Thread`.
- Actualiza un `JTextField` con el contenido de la lista cuando cambia (método `imprimir()`).

Métodos importantes:
- `meter(String id)` y `sacar(String id)` (síncronos) para añadir/quitar IDs.
- `meterLista(Thread th)` y `sacarLista(Thread th)` para gestionar referencias a los `Thread` creados.

---

### src/entidades/Paso.java
Propósito:
- Mecanismo de pausa/reanudación global para la simulación.

Funcionamiento:
- Mantiene un `boolean cerrado` y un `Condition` para bloquear hilos en `mirar()` cuando `cerrado==true`.
- `cerrar()` pone `cerrado=true`; `abrir()` pone `cerrado=false` y hace `signalAll()`.

Uso:
- Muchas clases llaman a `paso.mirar()` antes de pasos críticos para respetar la pausa desde la GUI.

---
---

### src/interfaz/Interfaz.java (GUI principal)
Propósito:
- Interfaz gráfica principal que crea la colonia, listas visibles en JTextFields y genera los hilos de hormiga.
- Inicia un RMI registry local en el puerto 1099 y publica una instancia de `EnvioValores` como `//localhost/VistaNumeroHormigas`.

Elementos clave:
- Campos `JTextField` para mostrar listas y contadores (almacén, instrucción, descanso, zona comer, refugio, hormigas exterior/interior, etc.).
- Botones: `Pausar`, `Reanudar`, `Generar Insecto Invasor`.
- Método `crearHormigas()`: crea `HormigaObrera` continuamente (hasta 6000) y cada 3 obreras crea 1 soldado y 1 cria.
- `crearAmenaza()`: marca amenaza, interrumpe a crías y soldados para forzar que actúen y espera hasta que `Colonia` libere la amenaza.

Notas:
- Lanza `LocateRegistry.createRegistry(1099)` y `Naming.rebind(...)` para RMI.
- Los hilos se gestionan directamente usando `Thread.start()`.

---

### src/interfaz/InterfazParte2.java (GUI cliente RMI)
Propósito:
- Cliente que usa RMI para consultar el objeto `EnvioValores` expuesto por la `Interfaz` y actualizar una vista con métricas en tiempo real.

Elementos clave:
- Localiza el objeto remoto con `Naming.lookup("//127.0.0.1/VistaNumeroHormigas")`.
- En un hilo continuo actualiza campos `JTextField` con números: obreras exterior/interior, soldados en instrucción/invasión, crías en zona de comer/refugio.
- Dispone de un botón para enviar una amenaza al servidor remoto (invoca `enviarAmenaza()` vía RMI).

---

### src/ParteDistribuida/EnvioValores.java
Propósito:
- Objeto remoto (`UnicastRemoteObject`) que implementa `InterfaceObtenerValores` y permite consultar y modificar contadores compartidos desde la GUI principal.

Detalles:
- Mantiene contadores: `hormigasObrerasExterior`, `hormigasObrerasInterior`, `hormigasSoldadoInstruccion`, `hormigasSoldadoInvasion`, `hormigasCriaZonaComer`, `hormigasCriaRefugio`.
- Métodos `actualizarHormigas*` para incrementar/decrementar contadores, protegidos con `ReentrantLock` por contador.
- Métodos `get...()` con `throws RemoteException` para exponer los valores al cliente RMI.
- `enviarAmenaza()` invoca `Interfaz.pulsarBotonAmenaza()` para simular que el cliente remoto obliga a generar la amenaza en la GUI local.

Observación:
- El método `enviarAmenaza()` depende de la referencia estática `Interfaz.pulsarBotonAmenaza()` (acción global en la GUI principal).

---

### src/ParteDistribuida/InterfaceObtenerValores.java
Propósito:
- Interfaz RMI que declara los métodos remotos accesibles por clientes: getters de métricas y `enviarAmenaza()`.

---

## Archivos generados por la ejecución

- `historial/evolucionColonia.txt` — contiene las entradas del historial generadas por `FileManager.guardarDatos(...)`.

Ruta base usada: `System.getProperty("user.dir") + "\\historial\\"`.

---

## Resumen final

- El proyecto implementa una simulación concurrente de una colonia de hormigas con GUI y métricas expuestas vía RMI.
- Las clases en `entidades` contienen la lógica concurrente y los `Thread` que simulan comportamientos distintos.
- `Interfaz` arranca la simulación y publica un objeto RMI (`EnvioValores`) que el cliente `InterfazParte2` consulta.
- He documentado los archivos, sus interacciones y he señalado errores y mejoras prácticas (por ejemplo formato de fecha y actualizaciones de Swing).
