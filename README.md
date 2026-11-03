# Dentmarc
Proyecto web de tienda online de productos para uso dental

## Avance 02 · Panel de administración

Panel web independiente (Spring Boot + Thymeleaf) en `/admin`, protegido con Spring Security. Solo entran usuarios con rol `ADMINISTRADOR`.

**Módulos:** Resumen, Productos, Inventario, Categorías, Clientes y Usuarios y roles.

**Cómo ejecutarlo**

1. Tener MySQL en `localhost:3306` (la base `dentmarcdb` se crea sola). Si tu usuario/contraseña no son `root`/`itson`, define las variables de entorno `DB_USERNAME` y `DB_PASSWORD`.
2. `./mvnw spring-boot:run`
3. Entrar a <http://localhost:8080/admin/login>

**Datos mock iniciales** (se insertan al arrancar si las tablas están vacías)

| Rol | Correo | Contraseña |
| --- | --- | --- |
| Administrador | `admin@dentmarc.mx` | `Admin123!` |
| Cliente | `mariana@clinicadental.mx` | `Cliente123!` |

**Documentación:** [`docs/modelo-de-datos.md`](docs/modelo-de-datos.md) (justificación SQL, UML y modelo entidad–relación) y [`docs/guion-video.md`](docs/guion-video.md).

> CSRF está habilitado en toda la aplicación: los formularios con método POST deben usar `th:action` para que Thymeleaf agregue el token.
