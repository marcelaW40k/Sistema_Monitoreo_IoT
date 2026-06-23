# Guía de Despliegue Local (SETUP)

Sigue estos pasos detallados para compilar, probar y ejecutar de manera local el Sistema de Monitoreo de Flotas IoT.

---

## 📋 Prerrequisitos

Asegúrate de contar con las siguientes herramientas en tu máquina de desarrollo:
- **Java JDK 17** o superior.
- **Node.js** (Versión 18 o superior) y gestor de paquetes `npm`.
- **PostgreSQL** activo y configurado.

---

## 💾 1. Configuración del Servidor (Backend)

*  **Crear la Base de Datos:**
   Ingresa a tu consola de PostgreSQL o gestor gráfico (pgAdmin / DBeaver) y ejecuta:
   ```sql
   CREATE DATABASE sistema_monitoreo_db;
---

* ### Ajustar el Perfil de Configuración

* Dirígete al archivo `src/main/resources/application.properties` en tu backend y reemplaza tus credenciales locales:

   ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/sistema_monitoreo
      spring.datasource.username=tu_usuario_postgres
      spring.datasource.password=tu_contrasena_postgres

      spring.jpa.hibernate.ddl-auto=update
      spring.jpa.show-sql=true
      
## 💻 2. Configuración de la Interfaz (Frontend)

### 📂 Navegar e Instalar Dependencias
---
* Ingresa a la carpeta del proyecto frontend desde tu terminal e instala los módulos de Node:

   ```bash
   cd frontend
   npm install
---
* Inicia Vite para levantar el servidor local del frontend:

   ```bash
   npm run dev
---

 ### 🛠️  3. Interfaz Interactiva de Swagger (OpenAPI 3)

* Gracias a la configuración de `springdoc-openapi` integrada en nuestro `pom.xml`, puedes auditar, probar y consumir de manera visual todos los endpoints HTTP del sistema de flotas sin necesidad de clientes externos. Una vez que tu servidor backend esté corriendo, accede desde tu navegador preferido a:
   ```
   Link: http://localhost:8080/swagger-ui/index.html

### 🔑 4. Perfiles de Prueba por Defecto
---
* Puedes iniciar sesión en la pantalla de bienvenida utilizando cualquiera de los siguientes accesos simulados. Esto te permitirá validar la visualización responsiva de los componentes, el flujo dinámico de las gráficas y la segregación de paneles según los privilegios del usuario:

* | Rol de Usuario | Correo Electrónico | Contraseña |

* | **Administrador (ADMIN)** | `admin@monitoreo.com` | `admin123` |
| **Operador Común (USER)** | `user1@simon.com` | `user123` |