# Sistema de Facturación - Guía de ejecución para desarrolladores

Este documento explica cómo preparar el entorno, clonar el repositorio, configurar la conexión a PostgreSQL y ejecutar la aplicación localmente.

## 1. Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado lo siguiente:

- **JDK 21 o superior**
  - El proyecto está configurado para usar **Java 21** en `build.gradle`.
- **Git**
  - Necesario para clonar el repositorio.
- **Gradle** *(opcional, recomendado solo si no deseas usar el wrapper incluido)*
  - El repositorio ya incluye `gradlew` y `gradlew.bat`, por lo que **no es obligatorio** instalar Gradle globalmente.
- **PostgreSQL**
  - Debes tener un servidor PostgreSQL disponible y una base de datos creada para la aplicación.
- **Navegador web**
  - Para abrir la aplicación o probar endpoints locales cuando el servidor esté levantado.
- **IDE recomendado** *(opcional)*
  - IntelliJ IDEA, VS Code o Spring Tools.

## 2. Clonar el repositorio

Abre una terminal y ejecuta:

```powershell
git clone https://github.com/RicharDev01/tienda_El_Milagro.git
cd tienda_El_Milagro
```

Si el nombre de la carpeta local cambia o descargas el proyecto como archivo `.zip`, solo asegúrate de abrir la carpeta raíz donde se encuentra `build.gradle`.

## 3. Configurar la conexión a la base de datos

La configuración actual está en el archivo:

`src/main/resources/application.properties`

Actualmente el repositorio contiene una configuración local de ejemplo para PostgreSQL:

```properties
spring.application.name=SistemaFacturacion
server.port=2100

spring.datasource.url=jdbc:postgresql://localhost:5432/facturacion
spring.datasource.username=adminUES
spring.datasource.password=adminDB

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Qué debes cambiar

Cada desarrollador debe reemplazar estos valores por los de su entorno local:

- **Host** de PostgreSQL
- **Puerto** de PostgreSQL
- **Nombre de la base de datos**
- **Usuario**
- **Contraseña**

Ejemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mi_basedatos
spring.datasource.username=mi_usuario
spring.datasource.password=mi_password
```

### Recomendaciones

- Verifica que la base de datos exista antes de iniciar la aplicación.
- Si usarás otro puerto para la aplicación, modifica:

```properties
server.port=2100
```

- La propiedad:

```properties
spring.jpa.hibernate.ddl-auto=update
```

permite que Hibernate actualice el esquema automáticamente. Úsala con cuidado según el entorno.

## 4. Ejecutar la aplicación

### Opción recomendada: usar el Gradle Wrapper

Desde la raíz del proyecto, ejecuta:

```powershell
.\gradlew.bat bootRun
```

### Opción alternativa: si tienes Gradle instalado globalmente

```powershell
gradle bootRun
```

Si todo está correcto, la aplicación debería iniciar en:

```text
http://localhost:2100
```

> [!CAUTION]
> Si cambiaste `server.port`, usa el puerto que hayas configurado.

## 5. Verificar que el proyecto compila correctamente

Antes o después de levantar la aplicación, puedes ejecutar las pruebas con:

```powershell
.\gradlew.bat test
```

## 6. Resumen rápido

1. Instalar **JDK 21+**, **Git** y **PostgreSQL**.
2. Clonar el repositorio.
3. Editar `src/main/resources/application.properties` con tus credenciales de PostgreSQL.
4. Ejecutar `.\gradlew.bat bootRun`.
5. Abrir la aplicación en `http://localhost:2100`.

## 7. Nota para el equipo

> [!NOTE]
> El repositorio actualmente incluye credenciales locales de ejemplo en `application.properties`. Cada desarrollador debe cambiarlas por las de su entorno antes de ejecutar el sistema.

## 8. Buenas prácticas y estándares de desarrollo

Para mantener una base de código clara, consistente y fácil de mantener, el equipo debe seguir estas prácticas al momento de nombrar clases, métodos, variables y paquetes.

### 8.1 Usar español de forma consistente

Como este proyecto está orientado a trabajar el dominio en español, los nombres del código deben escribirse en **español de forma consistente**.

- No mezclar español con inglés en nombres de clases, métodos, variables o servicios.
- Evitar el **spanglish** en cualquier elemento del código.
- Si una clase comienza en español, todo su nombre debe permanecer en español.

Ejemplos:

- ❌ `FacturaController`
- ✅ `FacturaControlador`

- ❌ `FacturaService`
- ✅ `FacturaServicio`

- ❌ `clienteName`
- ✅ `nombreCliente`

- ❌ `saleTotal`
- ✅ `totalVenta`

### 8.2 Respetar los estándares de nomenclatura de Java

Además de escribir el código en español, se deben respetar las convenciones estándar de Java.

#### Clases e interfaces: `PascalCase`

Los nombres de clases e interfaces deben iniciar con mayúscula y cada palabra adicional también debe iniciar con mayúscula.

Ejemplos:

- ✅ `FacturaControlador`
- ✅ `FacturaServicio`
- ✅ `DetalleFactura`
- ❌ `facturaControlador`
- ❌ `factura_controlador`

#### Métodos y variables: `camelCase`

Los métodos y variables deben iniciar en minúscula, y cada palabra siguiente debe iniciar con mayúscula.

Ejemplos:

- ✅ `calcularTotal`
- ✅ `obtenerClientePorId`
- ✅ `fechaEmision`
- ✅ `montoDescuento`
- ❌ `CalcularTotal`
- ❌ `calcular_total`
- ❌ `customerName`

#### Constantes: `UPPER_SNAKE_CASE`

Las constantes deben escribirse en mayúsculas y separadas por guiones bajos.

Ejemplos:

- ✅ `TIEMPO_MAXIMO_ESPERA`
- ✅ `PORCENTAJE_IVA`
- ❌ `TiempoMaximoEspera`
- ❌ `porcentajeIva`

#### Paquetes: minúsculas y sin mezclar idiomas

Los paquetes deben mantenerse en minúsculas y con nombres consistentes.

Ejemplos:

- ✅ `tienda.milagro.sistemafacturacion.controlador`
- ✅ `tienda.milagro.sistemafacturacion.servicio`
- ❌ `tienda.milagro.sistemaFacturacion.Controller`
- ❌ `tienda.milagro.facturacion.service`

### 8.3 Reglas prácticas para el equipo

- Todo el código nuevo debe escribirse siguiendo la misma convención de idioma.
- Si el dominio del proyecto está en español, usar español también en:
  - clases
  - interfaces
  - métodos
  - variables
  - DTOs
  - servicios
  - controladores
  - repositorios
- Evitar abreviaturas ambiguas o nombres poco descriptivos.
- Preferir nombres claros y legibles, por ejemplo:
  - ✅ `buscarFacturaPorNumero`
  - ✅ `listaProductos`
  - ✅ `fechaRegistro`
  - ❌ `bfxn`
  - ❌ `lstProd`

### 8.4 Excepción para nombres técnicos externos

Se permite conservar nombres en inglés cuando provengan directamente de tecnologías, librerías, anotaciones o conceptos propios del framework, por ejemplo:

- `SpringBootApplication`
- `ResponseEntity`
- `PostgreSQL`
- `Gradle`

Sin embargo, el código propio del proyecto debe mantenerse en español y con las convenciones estándar de Java.

