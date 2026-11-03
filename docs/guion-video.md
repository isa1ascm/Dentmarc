# Guion sugerido para el video (5 a 10 minutos)

1. **Introducción (30 s):** qué es Dentmarc y qué cubre este avance (panel administrativo en la rama `feat-administracion`).
2. **Modelo de datos (1 min):** mostrar `docs/modelo-de-datos.md`: por qué SQL, diagrama de clases UML y diagrama entidad–relación.
3. **Arquitectura (1 min):** recorrer las carpetas `controllers`, `service`, `repository`, `models`, `config` y la plantilla `layout/admin.html` (cabecera y menú lateral reutilizables). Explicar que los controladores no tienen reglas de negocio.
4. **Seguridad (1.5 min):**
   - Abrir `/admin/resumen` sin sesión → redirige a `/admin/login`.
   - Iniciar sesión con credenciales incorrectas → mensaje de error.
   - Iniciar sesión con `admin@dentmarc.mx` / `Admin123!`.
   - Mostrar `SeguridadConfig` (`SecurityFilterChain`, `/admin/login` público, CSRF habilitado) y el hash BCrypt en la tabla `usuarios`.
5. **Módulos (4 min):** Resumen, Productos (crear, editar, buscar, filtrar, eliminar), Inventario (registrar entrada, salida y ajuste; ver cómo cambia el estado), Categorías (crear, ocultar, intentar borrar una con productos), Clientes y Usuarios y roles (crear un administrador, intentar eliminar la propia cuenta).
6. **Cierre de sesión (30 s):** botón de salir → vuelve al login; usar "atrás" del navegador y comprobar que el panel sigue bloqueado.
7. **Conclusión (30 s):** lo que sigue (pedidos, carrito y compra en la tienda).
