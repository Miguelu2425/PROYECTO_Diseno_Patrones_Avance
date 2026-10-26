# Plataforma Integral de Atencion Ciudadana Municipal

**Proyecto 10 - Diseno de Patrones | Universidad Tecnologica del Peru (UTP)**
**Semana 11**

---

## Integrantes del grupo

| Nombre | Rol |
|---|---|
| Gonzalo Solar Silva | Integrante |
| Miguel Aldair Lopez Litano | Integrante |
| Eddie Santiago Carrion Prado | Integrante |
| Alejandro Arturo Fernandez Chavez | Integrante |

---

## Arquitectura

El proyecto sigue el patron **MVC (Modelo - Vista - Controlador)**:

| Capa | Paquete | Responsabilidad |
|---|---|---|
| **Modelo** | src/modelos/ | Entidades del dominio (Ciudadano, etc.) |
| **Vista** | src/vistas/ | Interfaz grafica Swing (Main.java) |
| **Controlador** | src/controladores/ | Logica de coordinacion (AtencionController.java) |
| **Patrones** | src/patrones/ | Implementacion de los 11 patrones de diseno |

### Principios SOLID aplicados

- **S - Single Responsibility**: cada clase tiene una unica razon de cambio (ej. ConexionSingleton solo gestiona la conexion).
- **O - Open/Closed**: las fabricas y decoradores son extensibles sin modificar codigo existente.
- **L - Liskov Substitution**: Reclamo y Solicitud son intercambiables como Tramite.
- **I - Interface Segregation**: CanalNotificacion y Tramite son interfaces pequenas y cohesivas.
- **D - Dependency Inversion**: AtencionController depende de la abstraccion FabricaAtencion, no de implementaciones concretas.

---

## Patrones de Diseno Implementados (11 + Singleton de configuracion)

| # | Patron | Categoria | Ubicacion en src/ |
|---|---|---|---|
| 1 | **Singleton** (conexion BD) | Creacional | patrones/creacionales/singleton/ConexionSingleton.java |
| 2 | **Singleton** (configuracion) | Creacional | patrones/creacionales/singleton/ConfiguracionPlataforma.java |
| 3 | **Factory Method** | Creacional | patrones/creacionales/factory/TramiteFactory.java |
| 4 | **Abstract Factory** | Creacional | patrones/creacionales/abstractfactory/FabricaAtencion.java |
| 5 | **Builder** | Creacional | patrones/creacionales/builder/Expediente.java |
| 6 | **Prototype** | Creacional | patrones/creacionales/prototype/EncuestaSatisfaccion.java |
| 7 | **Facade** | Estructural | patrones/estructurales/facade/MesaPartesFacade.java |
| 8 | **Adapter** | Estructural | patrones/estructurales/adapter/AdaptadorSistemaAntiguo.java |
| 9 | **Composite** | Estructural | patrones/estructurales/composite/GrupoTramites.java |
| 10 | **Decorator** | Estructural | patrones/estructurales/decorator/NotificadorSMS.java |
| 11 | **Proxy** | Estructural | patrones/estructurales/proxy/TramiteProxy.java |
| 12 | **Bridge** | Estructural | patrones/estructurales/bridge/AlertaCiudadano.java |

---

## Configuracion y Ejecucion

### Requisitos

- Java 11 o superior
- SQL Server (local o remoto) con la base de datos Municipalidad
- NetBeans IDE o cualquier IDE compatible con proyectos Ant
- Driver JDBC para SQL Server (incluido en lib/)

### 1. Crear la base de datos

Abrir SQL Server Management Studio (SSMS) y ejecutar:

    bd/script_municipalidad.sql

Esto crea la base de datos Municipalidad con las tablas TipoTramite, Ciudadano
y Expediente, inserta los tipos base y define el procedimiento almacenado
sp_RegistrarTramite con manejo de transacciones (BEGIN TRAN / COMMIT / ROLLBACK).

### 2. Configurar credenciales

    copy config.properties.example config.properties

Editar config.properties con los datos de conexion:

    DB_SERVER=localhost
    DB_PORT=1433
    DB_NAME=Municipalidad
    DB_USER=tu_usuario
    DB_PASSWORD=tu_contrasena

ADVERTENCIA: config.properties esta en .gitignore y NO debe subirse al repositorio.

### 3. Ejecutar el proyecto

Abrir en NetBeans y ejecutar vistas/Main.java, o desde la terminal:

    ant run

---

## Estructura del Repositorio

    PROYECTO_Diseno_Patrones_Avance/
    |-- bd/
    |   -- script_municipalidad.sql       <- Script T-SQL para SQL Server
    |-- lib/                               <- Driver JDBC de SQL Server
    |-- PROYECTO_Diseno_Patrones_Avance/
    |   -- Plataforma_Integral_de_Atencion_Ciudadana_Municipal/
    |       -- src/
    |           |-- controladores/         <- AtencionController.java
    |           |-- modelos/               <- Ciudadano.java
    |           |-- patrones/
    |           |   |-- creacionales/      <- Singleton, Factory, Abstract Factory, Builder, Prototype
    |           |   -- estructurales/     <- Facade, Adapter, Composite, Decorator, Proxy, Bridge
    |           -- vistas/               <- Main.java (Swing GUI + punto de entrada)
    |-- config.properties.example          <- Plantilla de credenciales (segura para commit)
    |-- .gitignore
    -- README.md