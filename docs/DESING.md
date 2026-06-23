# Arquitectura y Diseño del Sistema de Monitoreo de Flotas IoT

Este documento detalla las elecciones de diseño, la estructura de la arquitectura y los compromisos técnicos (*trade-offs*) asumidos durante el desarrollo para lograr una plataforma robusta, segura y con una excelente experiencia móvil.

---

## 🚀 El Stack Tecnológico

La aplicación utiliza un enfoque **Full-Stack desacoplado**, separando limpiamente la lógica del servidor de la interfaz de usuario:

1. **Backend:** Java 17 + Spring Boot 3
   - **Spring Security + JWT:** Autenticación robusta y control de acceso basado en roles (`ADMIN` y `USER`).
   - **Spring Data JPA + PostgreSQL:** Motor relacional eficiente para almacenar vehículos, usuarios y lecturas de sensores.
   - **Springdoc OpenAPI (Swagger):** Documentación automática e interactiva de endpoints.
2. **Frontend:** React (Vite)
   - **Chart.js (`react-chartjs-2`):** Visualización analítica de telemetría en tiempo real (Velocidad y Combustible).
   - **React Leaflet (OpenStreetMap):** Mapeo geográfico de vehículos en vivo.

---

## 🏗️ Elecciones de Arquitectura y Soluciones Clave

### 1. Doble Capa de Seguridad: Spring Security + JwtInterceptor
Para blindar la aplicación sin penalizar el rendimiento ni la experiencia de usuario:
- **Filtro de Seguridad Principal (Spring Security):** Configura las de políticas de CORS, deshabilita CSRF para mantener un estado *Stateless* y permite el acceso libre y público a las rutas de autenticación (`/api/auth/**`) y de documentación (`/swagger-ui/**`, `/v3/api-docs/**`).
- **JwtInterceptor (Manejador MVC):** Actúa de forma quirúrgica sobre los controladores de Spring. Implementa un *pase libre controlado* para peticiones `GET` hacia `/api/fleet/vehicles/*/history`, permitiendo que operadores comunes auditen rutas sin levantar falsos errores 403, mientras restringe de raíz las alertas y transmisiones críticas exclusivamente para el rol `ADMIN`.

## 🚀 2. Optimización en la Capa de Datos (PostgreSQL)

Para evitar la saturación de memoria ocasionada por ráfagas constantes de datos provenientes de los sensores IoT, se reemplazó el mapeo masivo de entidades por una **consulta SQL nativa optimizada e indexada** en `SensorRepository`.

### Consulta utilizada

```sql
SELECT *
FROM sensor
WHERE vehicle_id = :vehicleId
ORDER BY timestamp DESC
LIMIT 20;
```

### Beneficios obtenidos

✅ Menor consumo de memoria

✅ Reducción del tiempo de respuesta

✅ Consulta optimizada para telemetría en tiempo real

✅ Escalabilidad ante grandes volúmenes de datos

---

# 🗄️ Datos Iniciales para Pruebas

## 🔐 3. Inserción de Usuarios

Las contraseñas se encuentran encriptadas utilizando **BCrypt**.

| Usuario | Contraseña |
|----------|------------|
| admin@monitoreo.com | admin123 |
| user1@simon.com | user123 |

INSERT INTO app_user (email, password, role)
VALUES
(
    'admin@monitoreo.com',
    '$2a$10$X5p8ABybSWSX1I/kGvPKtO1D8sX15Ld8Bv6GkHnSg8hD2H9Q2M0G6',
    'ADMIN'
),
(
    'user1@simon.com',
    '$2a$10$vE2j8x1gC8rM8f7dB3v5eO7eK3Gf9d8s7g6h5j4k3m2n1b0v9c8x7',
    'USER'
);
```

---

## 🚚 4. Inserción de Vehículos

```sql
INSERT INTO vehicles (plate, model, device_id) VALUES
('ABC-123', 'Chevrolet N300', 'DEV-1234-XC54'),
('XYZ-789', 'Kenworth T800', 'DEV-5678-LK99'),
('JKL-456', 'Foton Gratour', 'DEV-9012-MN11')
```

### Vehículos creados

| Placa | Modelo 
|--------|---------
| ABC-123 | Chevrolet N300 
| XYZ-789 | Kenworth T800 
| JKL-456 | Foton Gratour 

---

## 📡 5. Historial de Telemetría

Datos históricos utilizados para alimentar las gráficas en tiempo real del vehículo **ABC-123**.

```sql
INSERT INTO sensores (vehicle_id, current_consumption, fuel_level, latitude, longitude, speed, temperature, timestamp) VALUES 
(1, 24.5, 15.0,  6.120, -75.420, 75.0, 31.0, NOW() - INTERVAL '20 minutes'),
(1, 28.2, 12.1,  6.150, -75.450, 82.3, 32.5, NOW() - INTERVAL '10 minutes'),
(1, 30.0, 8.5,   6.185, -75.490, 85.0, 34.0, NOW());
```

### 📈 Variables monitoreadas

- Velocidad (`speed`)
- Nivel de combustible (`fuel_level`)
- Consumo actual (`current_consumption`)
- Ubicación GPS (`latitude`, `longitude`)
- Temperatura del motor (`temperature`)
- Fecha y hora del evento (`timestamp`)

---

## 🚛 Telemetría Base - Vehículo XYZ-789

```sql
INSERT INTO sensor (
    vehicle_id,
    speed,
    fuel_level,
    current_consumption,
    latitude,
    longitude,
    temperature,
    timestamp
)
VALUES
(
    2,
    40.0,
    14.5,
    18.5,
    4.6420,
    -74.1120,
    96.0,
    NOW() - INTERVAL '2 minutes'
);
```

### Resultado esperado

Al iniciar la aplicación:

- ✅ Usuarios disponibles para autenticación JWT.
- ✅ Vehículos precargados.
- ✅ Historial de sensores para visualización inmediata.
- ✅ Gráficas alimentadas con datos reales de prueba.
- ✅ Dashboard funcional sin necesidad de insertar registros manualmente.