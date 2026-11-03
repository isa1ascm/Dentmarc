# Modelo de datos y definición de dominio

## Elección de base de datos

Dentmarc utiliza una base de datos **SQL relacional**, específicamente MySQL en
el entorno de ejecución. La información del sistema tiene una estructura
definida y varias relaciones que deben conservar integridad:

- cada producto pertenece a una categoría;
- cada movimiento de inventario corresponde a un producto;
- un usuario puede tener varios productos en su carrito y un producto puede
  estar en los carritos de varios usuarios;
- el correo del usuario y el SKU del producto son valores únicos.

El modelo relacional permite expresar estas relaciones mediante claves
primarias, claves foráneas, restricciones `NOT NULL` y restricciones de
unicidad. Además, JPA/Hibernate permite consultar y administrar estas
entidades mediante repositorios Spring Data JPA. NoSQL no es necesario en este
caso porque los datos no son principalmente documentos variables ni requieren
desnormalización para su operación principal.

## Diagrama UML de clases

El siguiente diagrama representa las entidades implementadas en
`src/main/java/com/example/Dentmarc/models` y sus asociaciones principales.

```mermaid
classDiagram
    class Usuario {
        Long id
        String email
        String contrasenia
        String nombre
        String apellidos
        String telefono
        TipoPractica tipoPractica
        Rol rol
        List~Producto~ carrito
        Usuario()
        Usuario(...)
        getters/setters
    }

    class Categoria {
        Long id
        String nombre
        String icono
        boolean activa
        List~Producto~ productos
        Categoria()
        Categoria(...)
        getters/setters
    }

    class Producto {
        Long id
        String nombre
        String marca
        int cantidad
        String SKU
        EstadoProducto estadoProducto
        Double precio
        Categoria categoria
        Producto()
        Producto(...)
        getters/setters
    }

    class Movimiento {
        Long id
        Producto producto
        TipoMovimiento tipo
        int cantidad
        LocalDateTime fecha
        Movimiento()
        Movimiento(...)
        getters/setters
    }

    class Rol {
        <<enumeration>>
        USUARIO
        ADMINISTRADOR
    }
    class TipoPractica {
        <<enumeration>>
        CLINICA
        CONSULTORIO
        LABORATORIO
        ESTUDIANTE
    }
    class EstadoProducto {
        <<enumeration>>
        ACTIVO
        STOCK_BAJO
        AGOTADO
    }
    class TipoMovimiento {
        <<enumeration>>
        ENTRADA
        SALIDA
        AJUSTE
    }

    Categoria "1" --> "0..*" Producto : contiene
    Producto "1" --> "1" Categoria : pertenece
    Producto "1" --> "0..*" Movimiento : registra
    Usuario "0..*" --> "0..*" Producto : carrito
    Usuario --> Rol
    Usuario --> TipoPractica
    Producto --> EstadoProducto
    Movimiento --> TipoMovimiento
```

Las clases del diagrama corresponden al código Java:

- [Repositorio GitHub de Dentmarc](https://github.com/isa1ascm/Dentmarc)
- [Rama `feat-administracion`](https://github.com/isa1ascm/Dentmarc/tree/feat-administracion)
- [Usuario.java](../src/main/java/com/example/Dentmarc/models/Usuario.java)
- [Categoria.java](../src/main/java/com/example/Dentmarc/models/Categoria.java)
- [Producto.java](../src/main/java/com/example/Dentmarc/models/Producto.java)
- [Movimiento.java](../src/main/java/com/example/Dentmarc/models/Movimiento.java)
- [Rol.java](../src/main/java/com/example/Dentmarc/models/enums/Rol.java)
- [TipoPractica.java](../src/main/java/com/example/Dentmarc/models/enums/TipoPractica.java)
- [EstadoProducto.java](../src/main/java/com/example/Dentmarc/models/enums/EstadoProducto.java)
- [TipoMovimiento.java](../src/main/java/com/example/Dentmarc/models/enums/TipoMovimiento.java)

Lombok genera los métodos de acceso y los constructores indicados por
`@Data`, `@NoArgsConstructor` y `@AllArgsConstructor`. Las asociaciones se
implementan con `@ManyToOne`, `@OneToMany` y `@ManyToMany`.

## Modelo entidad-relación

```mermaid
erDiagram
    USUARIOS {
        BIGINT id PK
        VARCHAR email UK
        VARCHAR contrasenia
        VARCHAR nombre
        VARCHAR apellidos
        VARCHAR telefono
        VARCHAR tipo_practica
        VARCHAR rol
    }
    CATEGORIAS {
        BIGINT id PK
        VARCHAR nombre UK
        VARCHAR icono
        BOOLEAN activa
    }
    PRODUCTOS {
        BIGINT id PK
        VARCHAR nombre
        VARCHAR marca
        INT cantidad
        VARCHAR SKU UK
        VARCHAR estado_producto
        DECIMAL precio
        BIGINT id_categoria FK
    }
    MOVIMIENTOS {
        BIGINT id PK
        BIGINT id_producto FK
        VARCHAR tipo
        INT cantidad
        DATETIME fecha
    }
    CARRITO {
        BIGINT id_usuario PK, FK
        BIGINT id_producto PK, FK
    }

    CATEGORIAS ||--o{ PRODUCTOS : clasifica
    PRODUCTOS ||--o{ MOVIMIENTOS : tiene
    USUARIOS ||--o{ CARRITO : agrega
    PRODUCTOS ||--o{ CARRITO : contiene
```

## Persistencia

Las interfaces de acceso a datos se encuentran en
`src/main/java/com/example/Dentmarc/repository` y extienden
`JpaRepository`:

- [UsuarioRepository.java](../src/main/java/com/example/Dentmarc/repository/UsuarioRepository.java)
- [CategoriaRepository.java](../src/main/java/com/example/Dentmarc/repository/CategoriaRepository.java)
- [ProductoRepository.java](../src/main/java/com/example/Dentmarc/repository/ProductoRepository.java)
- [MovimientoRepository.java](../src/main/java/com/example/Dentmarc/repository/MovimientoRepository.java)

La lógica de negocio está en los servicios y los controladores solo coordinan
las solicitudes web y el patrón POST/Redirect/GET.
