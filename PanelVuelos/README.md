# Panel de Vuelos ✈️

Panel de salidas de aeropuerto hecho con Java y JavaFX.

![Captura del panel](captura.png)

## Qué hace
- Muestra una tabla de salidas con vuelo, destino, hora, puerta y estado.
- Colores según el estado del vuelo
- Base de datos con vuelos, compañías y aeropuertos

## Tecnologías
- Java 23
- JavaFX 21

## Próximos pasos
- Datos reales mediante una APIS
- Versión web

## Cómo ejecutarlo
1. Descarga JavaFX 21 (gluonhq.com/products/javafx) y añade sus .jar al proyecto.
2. Añade sqlite-jdbc y slf4j-api al proyecto.
3. Ejecuta Main con estos argumentos de la VM:
   --module-path "RUTA_A_JAVAFX/lib" --add-modules javafx.controls