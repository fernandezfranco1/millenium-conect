# Proyecto Millenium Conect - Sistema de Gestión de Escuela de Taekwondo

## Descripción del Proyecto
Sistema full-stack para gestión de escuela de taekwondo con backend en Java Spring Boot y frontend en Vue.js 3.

## Estado del Proyecto
- [x] Crear archivo copilot-instructions.md
- [ ] Configurar estructura backend Java
- [ ] Configurar estructura frontend Vue.js
- [ ] Crear documentación README

## Arquitectura
- **Backend**: Java 17 + Spring Boot 3.x + MySQL
- **Frontend**: Vue.js 3 + Composition API + Pinia + Tailwind CSS
- **Autenticación**: JWT

## Entidades del Sistema
1. Alumno (id, nombre, apellido, dni, teléfono, peso, categoría, género, fecha_nacimiento, edad)
2. Asistencia (id, id_alumno, estado, fecha_asistencia)
3. Cuota (id, id_alumno, monto, fecha_pago, forma_pago, estado)
4. Producto (id, nombre, marca, color, stock, precio_venta)
5. Venta (id, id_producto, fecha_venta, total)
6. Evento (id, nombre, fecha, tipo)
7. Usuario (id, username, password, rol)
