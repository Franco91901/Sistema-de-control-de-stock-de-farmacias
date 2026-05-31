# Farmacia Multisede — Backend API

Sistema de gestión de inventario farmacéutico para cadenas con múltiples sedes. Permite controlar medicamentos, lotes, stock, órdenes de reposición y alertas automáticas por sede, con acceso diferenciado por rol.

---

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 4.0.0 |
| Web | Spring WebMVC |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL 8 |
| Seguridad | Spring Security + OAuth2 Resource Server (JWT / Nimbus) |
| Mapeo de objetos | MapStruct 1.5.5 |
| Reducción de boilerplate | Lombok 1.18.32 |
| Reportes PDF | JasperReports 6.20.0 |
| Validación | Jakarta Bean Validation |
| Build | Maven |

---

## Arquitectura

El proyecto aplica **Arquitectura Hexagonal** (Ports & Adapters). Cada módulo de dominio está aislado en tres capas:

```
domain/
  model/       ← entidades JPA y enums
  repository/  ← interfaces (puertos de salida)
application/
  service/     ← lógica de negocio
  dto/         ← objetos de transferencia (request / response)
  mapper/      ← conversión entidad ↔ DTO (MapStruct)
infrastructure/
  controller/  ← adaptadores de entrada (REST)
  config/      ← configuración Spring (Security, beans)
  security/    ← filtros JWT, contexto de autenticación
```

Esta separación permite que la lógica de negocio no dependa del framework ni de la base de datos.

---

## Estructura de paquetes

```
com.proyecto
├── auth/                      ← módulo de autenticación y usuarios
│   ├── domain/model/          ← Usuario, Rol
│   ├── application/service/   ← AuthService, UsuarioService
│   └── infrastructure/
│       ├── controller/        ← AuthController, UsuarioController, AdminUsuarioController
│       ├── security/          ← JwtFilter, JwtService
│       └── config/            ← SecurityConfig
│
├── core/
│   ├── sede/                  ← gestión de sedes
│   ├── medicamento/           ← catálogo de medicamentos
│   ├── lote/                  ← lotes por sede (caducidad, stock)
│   ├── stock/                 ← vista consolidada de stock
│   ├── notificacion/          ← alertas de bajo stock y próximos a caducar
│   └── orden/                 ← órdenes de compra/transferencia y su seguimiento
│
└── shared/
    ├── dto/                   ← ApiResponse<T> (envoltorio uniforme)
    ├── exception/             ← EntityNotFoundException, GlobalExceptionHandler
    └── security/              ← AuthContext (datos del usuario autenticado)
```

---

## Roles y acceso

| Rol | Acceso |
|-----|--------|
| `ROLE_ADMIN` | CRUD de usuarios del sistema |
| `ROLE_GESTOR` | Stock global entre sedes, notificaciones, generación de órdenes, reportes PDF |
| `ROLE_FARMACEUTICO` | Medicamentos, lotes y stock de su propia sede |
| `ROLE_TRANSPORTISTA` | Lista y avance de estado de órdenes en tránsito |

---

## Endpoints principales

Todos los endpoints requieren el header `Authorization: Bearer <token>` excepto los de autenticación.

### Autenticación
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/login` | Obtener token JWT |
| POST | `/api/auth/register` | Registrar usuario |
| GET | `/api/auth/me` | Datos del usuario autenticado |

### Administración de usuarios _(ADMIN)_
| Método | Ruta |
|--------|------|
| GET | `/api/admin/usuarios` |
| GET | `/api/admin/usuarios/{email}` |
| PUT | `/api/admin/usuarios/{email}` |
| DELETE | `/api/admin/usuarios/{email}` |

### Sedes
| Método | Ruta |
|--------|------|
| GET / POST | `/api/sedes` |
| GET / PUT / DELETE | `/api/sedes/{id}` |

### Medicamentos _(ámbito: sede del usuario)_
| Método | Ruta |
|--------|------|
| GET / POST | `/api/medicamentos` |
| GET / PUT / DELETE | `/api/medicamentos/{id}` |
| GET | `/api/medicamentos/buscar?nombre=` |
| GET | `/api/medicamentos/bajo-stock` |

### Lotes _(ámbito: sede del usuario)_
| Método | Ruta |
|--------|------|
| GET / POST | `/api/lotes` |
| GET / PUT / DELETE | `/api/lotes/{id}` |
| GET | `/api/lotes/medicamento/{id}` |
| GET | `/api/lotes/stock` |
| GET | `/api/lotes/proximos-caducar` |
| GET | `/api/lotes/caducados` |
| POST | `/api/lotes/{id}/retirar?cantidad=` |
| POST | `/api/lotes/retirar-vencidos` |

### Stock
| Método | Ruta |
|--------|------|
| GET | `/api/stock` |
| GET | `/api/stock/medicamento/{id}` |
| GET | `/api/stock/bajo-stock` |
| GET | `/api/stock/estadisticas` |

### Notificaciones
| Método | Ruta |
|--------|------|
| GET | `/api/notificaciones` |
| GET | `/api/notificaciones/pendientes` |
| GET | `/api/notificaciones/tipo/{tipo}` |
| GET | `/api/notificaciones/{id}` |
| GET | `/api/notificaciones/estadisticas` |
| POST | `/api/notificaciones/generar-automaticas` |

### Gestor _(GESTOR)_
| Método | Ruta |
|--------|------|
| GET | `/api/gestor/stock` |
| GET | `/api/gestor/stock/medicamento/{id}` |
| GET | `/api/gestor/stock/sede/{id}` |
| GET | `/api/gestor/notificaciones` |
| GET | `/api/gestor/notificaciones/{id}` |
| GET / DELETE | `/api/gestor/ordenes` / `/api/gestor/ordenes/{id}` |
| POST | `/api/gestor/ordenes/generar?idNotificacion=&cantidad=` |
| GET | `/api/gestor/reportes/stock-sede/pdf` |
| GET | `/api/gestor/reportes/stock-medicamento/pdf` |
| GET | `/api/gestor/reportes/notificaciones/pdf` |
| GET | `/api/gestor/reportes/ordenes/pdf` |

### Transportista _(TRANSPORTISTA)_
| Método | Ruta |
|--------|------|
| GET | `/api/transportista/ordenes` |
| GET | `/api/transportista/ordenes/{id}` |
| PUT | `/api/transportista/ordenes/{id}/avanzar` |

---

## Modelo de datos

```
Rol ──< Usuario >── Sede
                     │
              ┌──────┴──────┐
              │             │
         MedicamentoSede  Lote
              │             │
         Medicamento    MovimientoStock
              │
        ┌─────┴──────┐
        │            │
   DetalleOrden  Notificacion
        │
      Orden >── Sede (origen)
            >── Sede (destino, nullable)
```

**Enums utilizados:**

| Entidad | Campo | Valores |
|---------|-------|---------|
| `MovimientoStock` | `tipo` | `ENTRADA`, `SALIDA`, `TRANSFERENCIA`, `VENCIMIENTO`, `AJUSTE` |
| `Orden` | `tipo` | `COMPRA`, `TRANSFERENCIA`, `DEVOLUCION` |
| `Orden` | `estado` | `PENDIENTE`, `APROBADA`, `RECHAZADA`, `COMPLETADA` |
| `DetalleOrden` | `estado` | `PENDIENTE`, `EN_PREPARACION`, `EN_RUTA`, `ENTREGADO` |
| `Notificacion` | `tipo` | `BAJO_STOCK`, `PROXIMO_CADUCAR` |
| `Notificacion` | `estado` | `PENDIENTE`, `ATENDIDA`, `RESUELTA` |

---

## Configuración y ejecución

### Requisitos previos
- Java 17+
- MySQL 8
- Maven 3.9+

### Pasos

1. Crear la base de datos:
```sql
CREATE DATABASE bd_DAW1;
```

2. Configurar credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bd_DAW1
spring.datasource.username=root
spring.datasource.password=mysql
```

3. Compilar y ejecutar:
```bash
mvn spring-boot:run
```

Al iniciar, Hibernate crea/actualiza las tablas (`ddl-auto=update`) y `data.sql` carga los datos iniciales automáticamente.

---

## Usuarios de prueba

| Email | Contraseña | Rol |
|-------|-----------|-----|
| `admin@farmacia.pe` | `password` | ADMIN |
| `gestor@farmacia.pe` | `password` | GESTOR |
| `gestor2@farmacia.pe` | `password` | GESTOR |
| `transportista@farmacia.pe` | `password` | TRANSPORTISTA |
| `farmac1@farmacia.pe` | `password` | FARMACÉUTICO — Sede Central |
| `farmac2@farmacia.pe` | `password` | FARMACÉUTICO — Sede Miraflores |
| `farmac3@farmacia.pe` | `password` | FARMACÉUTICO — Sede San Isidro |

### Ejemplo de login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "gestor@farmacia.pe", "password": "password"}'
```

El token JWT retornado se usa en las siguientes peticiones:
```bash
curl http://localhost:8080/api/gestor/stock \
  -H "Authorization: Bearer <token>"
```
