# ✈️ F4U Backend - Fly For You

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Azure](https://img.shields.io/badge/Azure-SQL_Database-0078D4?style=for-the-badge&logo=microsoft-azure&logoColor=white)
![License](https://img.shields.io/badge/License-ISC-green?style=for-the-badge)

**API RESTful para Sistema de Reservas de Vuelos con Autenticación Azure AD**

[Características](#-características-principales) • [Instalación](#-instalación) • [Configuración](#️-configuración) • [API](#-documentación-api) • [Arquitectura](#️-arquitectura)

</div>

---

## 📋 Descripción

**F4U Backend** es una API REST robusta y escalable construida con **Spring Boot 3.5** que gestiona todo el sistema de reservas de vuelos. Implementa autenticación segura mediante **Microsoft Azure AD (Entra ID)**, comunicación en tiempo real con **WebSockets**, y conexión a **Azure SQL Database**.

### 🎯 Propósito del Proyecto

El backend proporciona:
- API RESTful completa para gestión de vuelos y reservas
- Autenticación y autorización mediante Azure AD OAuth2/JWT
- Sistema de bloqueo de asientos en tiempo real con WebSockets
- Integración con base de datos SQL Server en Azure
- Servicios de correo electrónico para confirmaciones
- Chatbot inteligente integrado
- Health checks y monitoreo de aplicación

---

## ✨ Características Principales

### 🔐 Seguridad y Autenticación
- **Azure AD (Entra ID)** como proveedor OAuth2
- Tokens **JWT** para autenticación stateless
- Validación automática de tokens con Spring Security
- Control de acceso basado en roles (RBAC)
- Configuración CORS segura para frontend

### 🚀 API RESTful Completa
- **Gestión de vuelos** (búsqueda, filtrado, disponibilidad)
- **Sistema de reservas** con transacciones
- **Administración de asientos** con estados
- **Gestión de ciudades** y rutas
- **Servicios adicionales** (extras)
- **Perfiles de usuario** y autenticación

### 🔄 Comunicación en Tiempo Real
- **WebSocket con STOMP** para actualizaciones instantáneas
- **Bloqueo de asientos** distribuido en tiempo real
- Notificaciones push a clientes conectados
- Sincronización de estado entre usuarios

### 💾 Persistencia de Datos
- **Spring Data JPA** con Hibernate
- **Azure SQL Database** como base de datos
- Connection pooling optimizado con HikariCP
- Migraciones controladas con Hibernate DDL

### 📧 Servicios Adicionales
- **Envío de emails** para confirmaciones
- **Chatbot** con respuestas automáticas
- **Actuator** para monitoreo y health checks
- Logging detallado con niveles configurables

---

## 🚀 Instalación

### Requisitos Previos

- **Java JDK** 17 o superior
- **Maven** 3.8+ (incluido wrapper en el proyecto)
- **Azure SQL Database** (o SQL Server local)
- **Azure Active Directory** configurado
- **Git**

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/F4U-Company/F4U-Backend.git
cd F4U-Backend
```

2. **Configurar variables de entorno** (ver [Configuración](#️-configuración))
```bash
cp .env.example .env
# Editar .env con tus credenciales
```

3. **Compilar el proyecto**
```bash
# Windows
mvnw.cmd clean install

# Linux/Mac
./mvnw clean install
```

4. **Ejecutar la aplicación**
```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

5. **Verificar que está corriendo**
```bash
curl http://localhost:8080/actuator/health
```

---

## ⚙️ Configuración

### Variables de Entorno

El proyecto utiliza **dotenv-java** para cargar variables desde el archivo `.env`.

#### Archivo `.env`

```env
# ============================================
# DATABASE CONFIGURATION
# ============================================
DB_URL=jdbc:sqlserver://tu-servidor.database.windows.net:1433;database=f4u_db;encrypt=true;trustServerCertificate=false;loginTimeout=30;
DB_USERNAME=tu-usuario-admin
DB_PASSWORD=tu-password-segura

# Database Pool (opcional)
DB_POOL_SIZE=10
DB_MIN_IDLE=2
DB_CONNECTION_TIMEOUT=20000

# ============================================
# JPA CONFIGURATION
# ============================================
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false

# ============================================
# SERVER CONFIGURATION
# ============================================
SERVER_PORT=8080

# ============================================
# AZURE AD / MICROSOFT ENTRA ID
# ============================================
AZURE_TENANT_ID=tu-tenant-id-aqui
AZURE_CLIENT_ID=tu-client-id-aqui
AZURE_CLIENT_SECRET=tu-client-secret-aqui

# Azure Issuer URI
AZURE_ISSUER_URI=https://login.microsoftonline.com/{TENANT_ID}/v2.0

# Azure Audience
AZURE_AUDIENCE=api://{CLIENT_ID}

# ============================================
# CORS CONFIGURATION
# ============================================
CORS_ALLOWED_ORIGINS=http://localhost:5173,https://tu-app.azurestaticapps.net
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS
CORS_ALLOWED_HEADERS=*

# ============================================
# EMAIL CONFIGURATION
# ============================================
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-correo@gmail.com
MAIL_PASSWORD=tu-contraseña-de-aplicacion
```

### Configuración de Azure SQL Database

1. **Crear Azure SQL Database**
   - Portal Azure → SQL databases → Create

2. **Configurar Firewall**
   - Añadir tu IP pública
   - Permitir servicios de Azure

3. **Obtener cadena de conexión**
   - SQL Database → Connection strings → JDBC

4. **Copiar valores al archivo `.env`**

### Configuración de Azure AD

1. **Crear App Registration**
   - Azure Portal → Azure Active Directory → App registrations

2. **Configurar API Permissions**
   - User.Read
   - openid, profile, email

3. **Crear Client Secret**
   - Certificates & secrets → New client secret

4. **Configurar Expose an API**
   - Application ID URI: `api://{CLIENT_ID}`
   - Añadir scope: `access_as_user`

5. **Copiar credenciales al `.env`**

---

## 📡 Documentación API

### Base URL

```
Desarrollo: http://localhost:8080
Producción: https://tu-backend.azurewebsites.net
```

### Autenticación

Todas las rutas (excepto `/actuator/health` y `/api/debug/*`) requieren token JWT en el header:

```
Authorization: Bearer {token}
```

### Endpoints Principales

#### 🔐 Autenticación

```http
# Login con Azure AD (obtener token)
POST /api/auth/login
Content-Type: application/json

{
  "azureToken": "token_de_azure_ad"
}

Response: 200 OK
{
  "token": "jwt_token_backend",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe"
  }
}
```

#### 🌍 Ciudades

```http
# Obtener todas las ciudades
GET /api/cities

Response: 200 OK
[
  {
    "id": 1,
    "name": "Bogotá",
    "country": "Colombia",
    "iataCode": "BOG",
    "latitude": 4.7110,
    "longitude": -74.0721
  },
  ...
]
```

#### ✈️ Vuelos

```http
# Buscar vuelos
GET /api/flights/search?origin=BOG&destination=MDE&date=2025-12-15

Response: 200 OK
[
  {
    "id": 1,
    "flightNumber": "F4U-100",
    "origin": { "id": 1, "name": "Bogotá", "iataCode": "BOG" },
    "destination": { "id": 2, "name": "Medellín", "iataCode": "MDE" },
    "departureTime": "2025-12-15T08:00:00",
    "arrivalTime": "2025-12-15T09:15:00",
    "price": 250000.00,
    "availableSeats": 120,
    "aircraftType": "Boeing 737"
  },
  ...
]

# Obtener detalles de un vuelo
GET /api/flights/{id}

# Obtener vuelos por fecha
GET /api/flights/date?date=2025-12-15
```

#### 💺 Asientos

```http
# Obtener asientos de un vuelo
GET /api/seats/flight/{flightId}

Response: 200 OK
[
  {
    "id": 1,
    "seatNumber": "1A",
    "seatClass": "FIRST_CLASS",
    "status": "AVAILABLE",
    "price": 500000.00,
    "isLocked": false,
    "lockedBy": null
  },
  {
    "id": 2,
    "seatNumber": "1B",
    "seatClass": "FIRST_CLASS",
    "status": "LOCKED",
    "price": 500000.00,
    "isLocked": true,
    "lockedBy": "user@example.com",
    "lockExpiry": "2025-12-04T14:45:00"
  },
  ...
]

# Bloquear un asiento (WebSocket)
POST /api/seats/lock
Content-Type: application/json

{
  "seatId": 1,
  "userId": "user@example.com",
  "flightId": 1
}

Response: 200 OK
{
  "success": true,
  "message": "Seat locked successfully",
  "lockExpiry": "2025-12-04T14:45:00"
}

# Desbloquear un asiento
POST /api/seats/unlock
Content-Type: application/json

{
  "seatId": 1,
  "userId": "user@example.com"
}
```

#### 🎫 Reservas

```http
# Crear reserva
POST /api/reservations
Content-Type: application/json
Authorization: Bearer {token}

{
  "flightId": 1,
  "seatId": 1,
  "userId": 1,
  "extras": [
    {
      "id": 1,
      "name": "Equipaje adicional 23kg",
      "price": 50000.00
    }
  ],
  "totalPrice": 300000.00,
  "paymentMethod": "CREDIT_CARD",
  "cardDetails": {
    "number": "4111111111111111",
    "holderName": "John Doe",
    "expiryDate": "12/26",
    "cvv": "123"
  }
}

Response: 201 Created
{
  "id": 100,
  "reservationCode": "F4U-ABC123",
  "flight": { ... },
  "seat": { ... },
  "user": { ... },
  "extras": [ ... ],
  "totalPrice": 300000.00,
  "status": "CONFIRMED",
  "createdAt": "2025-12-04T14:30:00"
}

# Obtener reservas del usuario
GET /api/reservations/user/{userId}

# Obtener reserva por código
GET /api/reservations/{reservationCode}

# Cancelar reserva
DELETE /api/reservations/{id}
```

#### 🤖 Chatbot

```http
# Enviar mensaje al chatbot
POST /api/chatbot/message
Content-Type: application/json

{
  "message": "¿Cuáles son los vuelos disponibles a Medellín?",
  "userId": "user@example.com"
}

Response: 200 OK
{
  "response": "Tenemos 3 vuelos disponibles a Medellín hoy...",
  "suggestions": [
    "Ver vuelos disponibles",
    "Consultar precios",
    "Estado de mi reserva"
  ]
}
```

#### 🏥 Health & Debug

```http
# Health check (no requiere autenticación)
GET /actuator/health

Response: 200 OK
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "Microsoft SQL Server",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}

# Debug info (solo desarrollo)
GET /api/debug/env
GET /api/debug/datasource
GET /api/debug/test-db
```

### WebSocket (STOMP)

#### Conexión

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
  console.log('Connected: ' + frame);
  
  // Suscribirse a actualizaciones de asientos de un vuelo
  stompClient.subscribe('/topic/seats/1', function(message) {
    const seatUpdate = JSON.parse(message.body);
    console.log('Seat update:', seatUpdate);
  });
});
```

#### Topics Disponibles

```
/topic/seats/{flightId}       - Actualizaciones de asientos por vuelo
/topic/reservations/{userId}  - Notificaciones de reservas del usuario
/topic/flights                - Actualizaciones globales de vuelos
```

---

## 🏗️ Arquitectura

### Estructura del Proyecto

```
F4U-Backend/
├── .github/
│   └── workflows/
│       └── main_backend-f4u.yml    # CI/CD para Azure
├── .mvn/
│   └── wrapper/                     # Maven wrapper
├── src/
│   ├── main/
│   │   ├── java/com/fly/company/f4u_backend/
│   │   │   ├── F4uBackendApplication.java    # Clase principal
│   │   │   ├── config/                       # Configuraciones
│   │   │   │   ├── CustomJwtAuthenticationConverter.java
│   │   │   │   ├── DBStartupHealthCheck.java
│   │   │   │   ├── DotenvConfig.java
│   │   │   │   ├── JwtDecoderConfig.java
│   │   │   │   ├── SecurityConfig.java       # Spring Security
│   │   │   │   ├── StartupLogger.java
│   │   │   │   ├── WebConfig.java            # CORS
│   │   │   │   └── WebSocketConfig.java      # WebSocket/STOMP
│   │   │   ├── controller/                   # Controladores REST
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ChatbotController.java
│   │   │   │   ├── CityController.java
│   │   │   │   ├── DebugController.java
│   │   │   │   ├── FlightController.java
│   │   │   │   ├── HealthController.java
│   │   │   │   ├── ReservationController.java
│   │   │   │   ├── SeatController.java
│   │   │   │   └── SeatLockController.java
│   │   │   ├── model/                        # Entidades JPA
│   │   │   │   ├── City.java
│   │   │   │   ├── Extra.java
│   │   │   │   ├── Flight.java
│   │   │   │   ├── Reservation.java
│   │   │   │   ├── ReservationRequest.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Seat.java
│   │   │   │   ├── SeatWithLockInfo.java
│   │   │   │   ├── User.java
│   │   │   │   ├── UserRole.java
│   │   │   │   └── UserSession.java
│   │   │   ├── repository/                   # Repositorios JPA
│   │   │   │   ├── CityRepository.java
│   │   │   │   ├── FlightRepository.java
│   │   │   │   ├── ReservationRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   ├── SeatRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── UserRoleRepository.java
│   │   │   └── service/                      # Lógica de negocio
│   │   │       ├── ChatbotService.java
│   │   │       ├── EmailService.java
│   │   │       ├── EmailServiceImpl.java
│   │   │       ├── ReservationService.java
│   │   │       ├── ReservationServiceImpl.java
│   │   │       ├── SeatLockService.java
│   │   │       ├── UserService.java
│   │   │       └── UserServiceImpl.java
│   │   └── resources/
│   │       ├── application.properties        # Configuración principal
│   │       └── META-INF/
│   │           └── spring.factories
│   └── test/
│       ├── java/com/fly/company/f4u_backend/
│       │   └── DataSourceConnectionTest.java
│       └── resources/
│           └── application-test.properties
├── target/                                   # Archivos compilados
├── .env                                      # Variables de entorno (no en git)
├── .env.example                              # Ejemplo de variables
├── .gitattributes
├── .gitignore
├── mvnw                                      # Maven wrapper (Linux/Mac)
├── mvnw.cmd                                  # Maven wrapper (Windows)
├── pom.xml                                   # Configuración Maven
├── Procfile                                  # Para despliegue (Heroku/Azure)
└── README.md                                 # Este archivo
```

### Arquitectura en Capas

```
┌─────────────────────────────────────────┐
│          Frontend (React)               │
│   http://localhost:5173                 │
└────────────────┬────────────────────────┘
                 │ HTTP/REST + WebSocket
                 │
┌────────────────▼────────────────────────┐
│          Controllers                    │
│  (AuthController, FlightController...)  │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│          Services                       │
│  (ReservationService, EmailService...)  │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│          Repositories                   │
│  (Spring Data JPA)                      │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│       Azure SQL Database                │
│   (Microsoft SQL Server)                │
└─────────────────────────────────────────┘
```

### Stack Tecnológico

#### Core Framework
- **Spring Boot 3.5.6** - Framework principal
- **Java 17** - Lenguaje de programación

#### Seguridad
- **Spring Security** - Autenticación y autorización
- **Spring OAuth2 Resource Server** - Validación de tokens JWT
- **Azure AD (Entra ID)** - Proveedor de identidad

#### Persistencia
- **Spring Data JPA** - ORM y abstracción de datos
- **Hibernate** - Implementación JPA
- **HikariCP** - Connection pooling
- **Microsoft SQL Server JDBC Driver** - Conector de base de datos

#### Comunicación
- **Spring WebSocket** - Comunicación bidireccional
- **STOMP** - Protocolo de mensajería sobre WebSocket
- **Spring Web** - API REST

#### Utilidades
- **Spring Boot Actuator** - Monitoreo y health checks
- **Spring Mail** - Envío de correos electrónicos
- **dotenv-java 3.0.0** - Gestión de variables de entorno
- **Spring Validation** - Validación de datos

#### Testing
- **Spring Boot Test** - Testing framework
- **Spring Security Test** - Testing de seguridad

#### Build & Deploy
- **Maven 3.8+** - Gestión de dependencias
- **Maven Compiler Plugin 3.13.0** - Compilación
- **Maven Surefire Plugin 3.1.2** - Ejecución de tests

---

## 🔒 Seguridad

### Medidas Implementadas

- ✅ **OAuth 2.0** con Azure AD como proveedor
- ✅ **JWT stateless** para escalabilidad
- ✅ **HTTPS** obligatorio en producción
- ✅ **CORS** restrictivo y configurable
- ✅ **Validación de entrada** en todos los endpoints
- ✅ **SQL Injection** prevenida con JPA
- ✅ **Connection pooling** para prevenir agotamiento
- ✅ **Secrets** en variables de entorno (nunca en código)
- ✅ **Health checks** sin información sensible

### Configuración de Seguridad

```java
// SecurityConfig.java (simplificado)
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfig))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );
        return http.build();
    }
}
```

### Validación de Tokens JWT

El backend valida automáticamente:
- ✅ Firma del token (mediante clave pública de Azure AD)
- ✅ Emisor (issuer) del token
- ✅ Audiencia (audience) correcta
- ✅ Expiración del token
- ✅ Claims necesarios (roles, permisos)

---

## 🚢 Despliegue

### Azure App Service (Recomendado)

El proyecto incluye configuración de CI/CD con GitHub Actions.

#### Configuración Automática

1. **Crear App Service** en Azure Portal
   - Runtime: Java 17
   - Web server: Embedded (Spring Boot)

2. **Configurar variables de entorno** en Azure Portal
   - App Service → Configuration → Application settings

3. **Conectar GitHub**
   - Deployment Center → GitHub Actions

4. **Push a main** despliega automáticamente

#### Variables de Entorno en Azure

```
DB_URL=jdbc:sqlserver://tu-server.database.windows.net:1433;...
DB_USERNAME=admin
DB_PASSWORD=***
AZURE_TENANT_ID=***
AZURE_CLIENT_ID=***
AZURE_CLIENT_SECRET=***
AZURE_ISSUER_URI=https://login.microsoftonline.com/{tenant}/v2.0
AZURE_AUDIENCE=api://{client-id}
CORS_ALLOWED_ORIGINS=https://tu-frontend.azurestaticapps.net
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=***
MAIL_PASSWORD=***
```

### Build Manual

```bash
# Compilar JAR
mvnw clean package -DskipTests

# JAR generado en: target/f4u-backend-0.0.1-SNAPSHOT.jar

# Ejecutar JAR
java -jar target/f4u-backend-0.0.1-SNAPSHOT.jar
```

### Docker (Opcional)

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/f4u-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
docker build -t f4u-backend .
docker run -p 8080:8080 --env-file .env f4u-backend
```

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
mvnw test

# Test específico
mvnw test -Dtest=DataSourceConnectionTest

# Con coverage
mvnw test jacoco:report
```

### Tests Incluidos

- `DataSourceConnectionTest.java` - Verificación de conexión a BD

---

## 📦 Dependencias Principales

### Spring Boot Starters

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>      <!-- REST API -->
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId> <!-- WebSocket -->
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>  <!-- JPA/Hibernate -->
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>  <!-- Security -->
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId> <!-- Validation -->
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>  <!-- Monitoring -->
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>      <!-- Email -->
</dependency>
```

### Seguridad OAuth2/JWT

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

### Base de Datos

```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
</dependency>
```

### Utilidades

```xml
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>3.0.0</version>
</dependency>
```

---

## 🤝 Contribución

### Workflow de Contribución

1. **Fork** el proyecto
2. Crea una **rama** para tu feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** tus cambios (`git commit -m 'Add: Amazing Feature'`)
4. **Push** a la rama (`git push origin feature/AmazingFeature`)
5. Abre un **Pull Request**

### Convenciones de Código

- **Nombres de clases**: PascalCase (`FlightController`)
- **Nombres de métodos**: camelCase (`findFlightById`)
- **Constantes**: UPPER_SNAKE_CASE (`MAX_SEAT_LOCK_TIME`)
- **Paquetes**: lowercase (`com.fly.company.f4u_backend`)
- **DTOs**: sufijo `Request`, `Response`, `DTO`
- **Servicios**: sufijo `Service`, `ServiceImpl`

### Estructura de Commits

```
Add: Nueva funcionalidad
Fix: Corrección de bug
Update: Actualización de código existente
Refactor: Reestructuración sin cambio de funcionalidad
Security: Mejoras de seguridad
Docs: Documentación
Test: Pruebas
Perf: Mejoras de rendimiento
```

---

## 🐛 Troubleshooting

### Error: "Cannot connect to database"

**Causa**: Credenciales incorrectas o firewall de Azure SQL.

**Solución**:
1. Verifica variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` en `.env`
2. Azure Portal → SQL Database → Firewalls and virtual networks
3. Añade tu IP pública o habilita "Allow Azure services"

### Error: "Invalid JWT token"

**Causa**: Token expirado o mal configurado.

**Solución**:
1. Verifica `AZURE_ISSUER_URI` y `AZURE_AUDIENCE` en `.env`
2. Asegúrate de usar el formato correcto:
   - `AZURE_ISSUER_URI=https://login.microsoftonline.com/{TENANT_ID}/v2.0`
   - `AZURE_AUDIENCE=api://{CLIENT_ID}`
3. Regenera token en el frontend

### Error: "CORS policy blocked"

**Causa**: Frontend no está en la lista de orígenes permitidos.

**Solución**:
1. Añade la URL del frontend a `CORS_ALLOWED_ORIGINS` en `.env`
2. Múltiples orígenes separados por coma:
   ```
   CORS_ALLOWED_ORIGINS=http://localhost:5173,https://tu-app.azurestaticapps.net
   ```

### Error: "Port 8080 already in use"

**Solución**:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

O cambia el puerto en `.env`:
```
SERVER_PORT=8081
```

---

## 📄 Licencia

Este proyecto está bajo la licencia **ISC**.

---

## 👥 Equipo

**F4U Company** - Fly For You

- 🌐 [GitHub Organization](https://github.com/F4U-Company)
- 📧 Contacto: [contacto@f4u.com](mailto:contacto@f4u.com)

---

## 📞 Soporte

### Reportar Issues

Si encuentras un bug o tienes una sugerencia:
1. Revisa los [issues existentes](https://github.com/F4U-Company/F4U-Backend/issues)
2. Crea un [nuevo issue](https://github.com/F4U-Company/F4U-Backend/issues/new) con:
   - Descripción detallada
   - Logs de error
   - Pasos para reproducir
   - Versión de Java y Spring Boot

### FAQs

**Q: ¿Puedo usar otra base de datos en lugar de SQL Server?**
A: Sí, pero necesitas cambiar el driver JDBC en `pom.xml` y ajustar `spring.jpa.properties.hibernate.dialect`.

**Q: ¿Cómo añado nuevos endpoints?**
A: Crea un controlador en `controller/`, anótalo con `@RestController` y `@RequestMapping`.

**Q: ¿Cómo desactivo la autenticación para testing?**
A: En `application-test.properties` añade:
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

**Q: ¿El proyecto soporta múltiples tenants?**
A: Actualmente no, pero puede extenderse con Spring Data JPA multitenancy.

---

## 🗺️ Roadmap

### Versión Actual (0.0.1-SNAPSHOT)
- ✅ API REST completa
- ✅ Autenticación Azure AD
- ✅ WebSocket para tiempo real
- ✅ Sistema de reservas y asientos
- ✅ Integración con Azure SQL

### Próximas Versiones

#### v0.1.0
- [ ] Tests unitarios completos
- [ ] Tests de integración
- [ ] API documentation con Swagger/OpenAPI
- [ ] Rate limiting

#### v0.2.0
- [ ] Cache con Redis
- [ ] Logging mejorado con ELK Stack
- [ ] Métricas con Prometheus
- [ ] Circuit breaker con Resilience4j

#### v1.0.0
- [ ] Multi-tenancy
- [ ] Internacionalización (i18n)
- [ ] Backup automático de BD
- [ ] API Gateway integration
- [ ] Kubernetes deployment configs

---

## 🙏 Agradecimientos

- **Spring Team** por el increíble framework
- **Microsoft** por Azure y herramientas cloud
- **Comunidad Open Source** por las librerías utilizadas

---

<div align="center">

**[⬆ Volver arriba](#️-f4u-backend---fly-for-you)**

Hecho con ❤️ por **F4U Company**

</div>
