# Dentmarc
Proyecto web de tienda online de productos para uso dental

## Avance 02 · Panel de administración

Panel web independiente (Spring Boot + Thymeleaf) en `/admin`, protegido con Spring Security. Solo entran usuarios con rol `ADMINISTRADOR`.

**Módulos:** Resumen, Productos, Inventario, Categorías, Clientes y Usuarios y roles.

**Cómo ejecutarlo**

1. Tener MySQL en `localhost:3306` (la base `dentmarcdb` se crea sola). Si tu usuario/contraseña no son `root`/`itson`, define las variables de entorno `DB_USERNAME` y `DB_PASSWORD`.
2. Desde la carpeta `Dentmarc`, ejecutar `.\mvnw.cmd spring-boot:run` (Linux/macOS: `./mvnw spring-boot:run`).
3. Entrar a <http://localhost:8080/admin/login>

**Datos mock iniciales** (se insertan al arrancar si las tablas están vacías)

| Rol | Correo | Contraseña |
| --- | --- | --- |
| Administrador | `admin@dentmarc.mx` | `Admin123!` |
| Cliente | `mariana@clinicadental.mx` | `Cliente123!` |

**Documentación:** [`Dentmarc/docs/modelo-de-datos.md`](Dentmarc/docs/modelo-de-datos.md) contiene la justificación de SQL, el diagrama UML y el modelo entidad–relación.

> CSRF permanece habilitado para los formularios web; la API `/api/**` es stateless y se protege con JWT.

**Tecnologías:** Java 21, Spring Boot, Spring MVC, Thymeleaf, Spring Security,
Spring Data JPA/Hibernate, MySQL, H2 para pruebas, Bean Validation y JJWT.

## Avance 4 · API REST del storefront

La API para el storefront está disponible bajo `/api/v1`. Los catálogos y la
autenticación son públicos; cuenta y carrito requieren
`Authorization: Bearer <JWT>`. Los controladores REST usan DTOs, Bean
Validation y errores JSON estructurados. Las pantallas Thymeleaf de catálogo, autenticación,
cuenta y carrito consumen la API; el flujo del panel de administración se conserva.

| Pantalla/acción | Endpoint |
| --- | --- |
| Registro de cliente | `POST /api/v1/auth/register` |
| Inicio de sesión | `POST /api/v1/auth/login` |
| Navegación por categorías | `GET /api/v1/categories` |
| Catálogo y búsqueda | `GET /api/v1/products?search=&categoryId=` |
| Detalle de producto | `GET /api/v1/products/{id}` |
| Mi cuenta | `GET /api/v1/account` |
| Ver carrito | `GET /api/v1/cart` |
| Agregar producto al carrito | `POST /api/v1/cart/items` |
| Actualizar o quitar producto | `PUT/DELETE /api/v1/cart/items/{productId}` |

No se exponen pedidos, pagos, direcciones ni descuentos: el storefront actual
no ofrece esas operaciones respaldadas por datos persistentes.

**Postman:** importa [`Dentmarc-Storefront.postman_collection.json`](Dentmarc/postman/Dentmarc-Storefront.postman_collection.json)
y el entorno [`Dentmarc-Local.postman_environment.json`](Dentmarc/postman/Dentmarc-Local.postman_environment.json).
La colección incluye la cuenta demo para iniciar sesión, guarda el JWT en la
variable `jwt` y lo envía en las peticiones protegidas. El producto/categoría
de prueba se pueden cambiar en el entorno.

**JWT:** se configura con `JWT_SECRET` (mínimo 32 bytes) y
`JWT_EXPIRATION_MS` (por defecto, 3 600 000 ms). El valor predeterminado es
solo para desarrollo local; reemplázalo antes de cualquier despliegue.

Ejemplo de autenticación:

```http
POST /api/v1/auth/login
Content-Type: application/json

{"email":"mariana@clinicadental.mx","contrasenia":"Cliente123!"}
```

La respuesta incluye `token`; envíalo como `Authorization: Bearer <token>` al
consultar `/api/v1/account` o `/api/v1/cart`.

**Pruebas:** desde `Dentmarc`, ejecutar `.\mvnw.cmd test`.
