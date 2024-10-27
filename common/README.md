# Modul SWDA - Common Library
Enthält über Microservices geteilte Datenmodelle
## Package Registry
Die Dependency wird in folgendem Package Registry gehosted:
```xml
<repositories>
  <repository>
    <id>gitlab-maven</id>
    <url>https://gitlab.switch.ch/api/v4/projects/8189/packages/maven</url>
  </repository>
</repositories>

<distributionManagement>
  <repository>
    <id>gitlab-maven</id>
    <url>https://gitlab.switch.ch/api/v4/projects/8189/packages/maven</url>
  </repository>

  <snapshotRepository>
    <id>gitlab-maven</id>
    <url>https://gitlab.switch.ch/api/v4/projects/8189/packages/maven</url>
  </snapshotRepository>
</distributionManagement>
```
## Koordinaten
Die Dependency kann über folgende Maven Koordinaten angezogen werden:
```xml
<dependency>
<groupId>ch.hslu.swda24hs.g09</groupId>
<artifactId>g09-common</artifactId>
<version>1.0.0-SNAPSHOT</version>
</dependency>
```