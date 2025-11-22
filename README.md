# Millenium Conect - Sistema de Gestión de Escuela de Taekwondo

Sistema full-stack para la gestión integral de una escuela de taekwondo, desarrollado con Java Spring Boot en el backend y Vue.js 3 en el frontend.

## 🥋 Características Principales

- **Gestión de Alumnos**: Registro, modificación, eliminación y búsqueda de alumnos con información detallada
- **Control de Asistencias**: Registro de asistencias a clases con estados (Presente, Ausente, Tardanza)
- **Administración de Cuotas**: Gestión de pagos mensuales con diferentes formas de pago y estados
- **Inventario de Productos**: Control de stock de productos (uniformes, protecciones, etc.)
- **Registro de Ventas**: Gestión de ventas de productos
- **Gestión de Eventos**: Organización de torneos, exámenes y seminarios
- **Autenticación JWT**: Sistema de login seguro para administradores
- **Reportes**: Listados y filtros personalizados para análisis de datos

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
  - Spring Data JPA
  - Spring Security
  - Spring Web
- **MySQL 8.0**
- **JWT** para autenticación
- **Maven** para gestión de dependencias
- **Lombok** para reducción de código boilerplate

### Frontend
- **Vue.js 3** (Composition API)
- **Vue Router** para navegación
- **Pinia** para gestión de estado
- **Axios** para peticiones HTTP
- **Tailwind CSS** para estilos
- **Vite** como build tool

## 📋 Requisitos Previos

### Backend
- JDK 17 o superior
- MySQL 8.0 o superior
- Maven 3.6+

### Frontend
- Node.js 16+ y npm

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
cd millenium-conect
```

### 2. Configurar la Base de Datos

Crear una base de datos MySQL:

```sql
CREATE DATABASE millenium_conect;
```

Editar el archivo `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/millenium_conect
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### 3. Configurar el Backend

```bash
cd backend
mvn clean install
```

### 4. Crear Usuario Administrador

Ejecutar este script SQL para crear el usuario inicial (la contraseña será encriptada por Spring Security):

```sql
INSERT INTO usuarios (username, password, rol) 
VALUES ('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQNkhf0jXkKOPqXLGVp4G', 'ADMIN');
-- Usuario: admin
-- Contraseña: admin123
```

O usa BCryptPasswordEncoder para generar tu propia contraseña encriptada.

### 5. Iniciar el Backend

```bash
cd backend
mvn spring-boot:run
```

El backend estará disponible en: `http://localhost:8080`

### 6. Configurar e Iniciar el Frontend

```bash
cd frontend
npm install
npm run dev
```

El frontend estará disponible en: `http://localhost:5173`

## 📁 Estructura del Proyecto

```
millenium-conect/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/millenium/
│   │   │   │   ├── config/          # Configuraciones (Security, CORS)
│   │   │   │   ├── controller/      # Controladores REST
│   │   │   │   ├── model/           # Entidades JPA
│   │   │   │   ├── repository/      # Repositorios Spring Data
│   │   │   │   ├── security/        # JWT y autenticación
│   │   │   │   └── service/         # Lógica de negocio
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
│
└── frontend/
    ├── src/
    │   ├── assets/          # Estilos globales
    │   ├── components/      # Componentes Vue reutilizables
    │   ├── router/          # Configuración de rutas
    │   ├── services/        # Servicios API
    │   ├── stores/          # Stores de Pinia
    │   ├── views/           # Vistas principales
    │   ├── App.vue
    │   └── main.js
    ├── index.html
    ├── package.json
    ├── tailwind.config.js
    └── vite.config.js
```

## 🔐 Credenciales por Defecto

**Usuario:** admin  
**Contraseña:** admin123

⚠️ **IMPORTANTE**: Cambia estas credenciales en producción.

## 📡 API Endpoints

### Autenticación
- `POST /api/auth/login` - Iniciar sesión

### Alumnos
- `GET /api/alumnos` - Listar todos los alumnos
- `GET /api/alumnos/{id}` - Obtener alumno por ID
- `POST /api/alumnos` - Crear alumno
- `PUT /api/alumnos/{id}` - Actualizar alumno
- `DELETE /api/alumnos/{id}` - Eliminar alumno
- `GET /api/alumnos/buscar?termino={termino}` - Buscar alumnos
- `GET /api/alumnos/categoria/{categoria}` - Filtrar por categoría

### Asistencias
- `GET /api/asistencias` - Listar todas las asistencias
- `POST /api/asistencias` - Registrar asistencia
- `GET /api/asistencias/fecha?fecha={fecha}` - Filtrar por fecha
- `GET /api/asistencias/rango?inicio={inicio}&fin={fin}` - Filtrar por rango

### Cuotas
- `GET /api/cuotas` - Listar todas las cuotas
- `POST /api/cuotas` - Registrar cuota
- `GET /api/cuotas/estado/{estado}` - Filtrar por estado
- `GET /api/cuotas/rango?inicio={inicio}&fin={fin}` - Filtrar por rango

### Productos
- `GET /api/productos` - Listar todos los productos
- `POST /api/productos` - Crear producto
- `GET /api/productos/buscar?nombre={nombre}` - Buscar productos

### Ventas
- `GET /api/ventas` - Listar todas las ventas
- `POST /api/ventas` - Registrar venta

### Eventos
- `GET /api/eventos` - Listar todos los eventos
- `POST /api/eventos` - Crear evento
- `GET /api/eventos/tipo/{tipo}` - Filtrar por tipo

## 🔧 Configuración Adicional

### CORS
El backend está configurado para aceptar peticiones desde:
- `http://localhost:5173` (Vite dev server)
- `http://localhost:3000`

Para modificar esto, edita `backend/src/main/java/com/millenium/config/SecurityConfig.java`

### JWT
El token JWT expira en 24 horas por defecto. Para modificarlo:
```properties
jwt.expiration=86400000  # en milisegundos
```

## 🎯 Próximas Funcionalidades

- [ ] Implementar completamente los formularios de Asistencias, Cuotas, Productos, Ventas y Eventos
- [ ] Sistema de reportes con gráficos
- [ ] Exportación a PDF/Excel
- [ ] Notificaciones de cuotas vencidas
- [ ] Dashboard con estadísticas
- [ ] Sistema de roles más complejo
- [ ] Acceso para alumnos (consultar cuotas, asistencias, etc.)
- [ ] Sistema de mensajería interna

## 🐛 Solución de Problemas

### El backend no inicia
- Verifica que MySQL esté corriendo
- Comprueba las credenciales en `application.properties`
- Asegúrate de tener Java 17+

### Error de CORS en el frontend
- Verifica que el backend esté corriendo en el puerto 8080
- Comprueba la configuración de CORS en `SecurityConfig.java`

### No puedo hacer login
- Verifica que el usuario admin esté creado en la base de datos
- Comprueba que la contraseña esté encriptada correctamente

## 👥 Contribuir

Este proyecto fue creado para la gestión de una escuela de taekwondo. Para contribuir:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

## 📞 Contacto

Para preguntas o soporte, por favor abre un issue en el repositorio.

---

**Desarrollado con ❤️ para la comunidad de Taekwondo**
