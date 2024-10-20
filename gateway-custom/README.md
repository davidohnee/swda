# Modul SWDA - Gateway Microservice Sample
Beispiel (Gateway-)Microservice für SWDA, basierend auf [micronaut.io](https://micronaut.io/) für g99.
* Der Service nutzt [Logback](http://logback.qos.ch/) über die [SLF4J](http://www.slf4j.org/)-
  Schnittstelle für Logging.
* Im Package `ch.hslu.swda.entities` befindet sich das (sehr kleine) Domain Model.
* Im Package `ch.hslu.swda.business` befindet sich eine klassische class-based API
  mit Interface und einer einfachen (Fake-)Implementation (in Memory Store).
* Im Package `ch.hslu.swda.micro` befindet sich der REST-Controller und der 
  Applikationskontext für das [Micronaut](https://micronaut.io/) -Framework.

### Build
* `mvn package` erstellt ein Single-JAR (enthält alle Dependencies) mit dem Namen
`service.jar` (im `./target-Verzeichnis`). 

### Start lokal (ohne Docker)
* Das Single-JAR kann direkt gestartet werden mit: `java -jar service.jar`
* Nach dem Start steht die API unter http://localhost:8090/api/v1/students/ 
  zur Verfügung.

### Docker Build (nativ)
* Erstellen des Docker-Images: `docker build . -t "swda/gateway-sample"`
* Starten des Containers: `docker run -d --name swda-gateway -p 8090:8090 swda/gateway-sample`
* Logs verfolgen (tail): `docker logs swda-container`
* Stoppen des Containers: `docker stop swda-container`
Hinweis: Passen Sie das Portmapping wenn nötig an.

### Docker über Maven-Plugin (alternativ)
* Erstellen der Docker-Images: `mvn docker:build`
* Starten des Containers: `mvn docker:start`
* Logs anzeigen: `mvn docker:logs`
* Stoppen des Containers: `mvn docker:stop`
Hinweis: Maven entfernt beim Stoppen auch gleich den Container.
