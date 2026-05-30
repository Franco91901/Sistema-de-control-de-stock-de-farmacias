# Plan de Migración a Full Stack — Farmacia Multisede

## Estado actual

Monolito Spring Boot con Thymeleaf (server-side rendering), **reorganizado a arquitectura hexagonal**.

- **Backend:** Java 17 + Spring Boot 4.0.0 + Spring Security + JWT
- **DB:** MySQL — base `bd_DAW1`
- **Frontend actual:** Thymeleaf (templates HTML en el servidor — pendiente de eliminar)
- **Puerto:** 9090

### Estructura de paquetes (hexagonal)

```
com.proyecto/
├── domain/
│   ├── model/        ← Entidades JPA (8): Usuario, Rol, Sede, Medicamento, Lote, Notificacion, Orden, DetalleOrden
│   └── repository/   ← Repositorios JPA (8)
├── application/
│   ├── service/      ← Servicios + interfaces (17)
│   ├── dto/          ← DTOs request/response (21)
│   └── mapper/       ← MapStruct mappers (7)
├── infrastructure/
│   ├── controller/   ← Controllers REST + MVC (14)
│   ├── config/       ← SecurityConfig
│   └── security/     ← JwtAuthenticationFilter, JwtService
└── FarmaciaApplication.java
```

---

## Objetivo

Separar el proyecto en dos capas independientes:

| Capa | Tecnología | Puerto |
|------|-----------|--------|
| Backend | Spring Boot — API REST pura | 9090 |
| Frontend | TBD (React / Vue / Angular) | 5173 / 4200 |

---

## Fase 0 — Reorganización hexagonal ✅ COMPLETADA

- [x] `domain/model/` — 8 entidades
- [x] `domain/repository/` — 8 repositorios
- [x] `application/service/` — 17 servicios
- [x] `application/dto/` — 21 DTOs
- [x] `application/mapper/` — 7 mappers
- [x] `infrastructure/controller/` — 14 controladores
- [x] `infrastructure/config/` — SecurityConfig
- [x] `infrastructure/security/` — JWT

---

## Fase 1 — Limpieza del backend

### Eliminar (no aportan valor en full stack)

- [ ] Directorio `bin/` — compilación antigua duplicada (46 clases obsoletas)
- [ ] `src/main/resources/templates/` — 23 templates Thymeleaf
- [ ] Dependencia `spring-boot-starter-thymeleaf` en `pom.xml`
- [ ] Controladores MVC que retornan vistas HTML:
  - [ ] `UsuarioController` (reemplazado por `UsuarioRestController`)
  - [ ] `AdminUsuarioController` (reemplazado por `AdminUsuarioRestController`)
  - [ ] `TransportistaController` (reemplazado por `TransportistaRestController`)
  - [ ] Endpoints HTML de `MedicamentoController` y `LoteController`
  - [ ] `SedeController` (solo vistas)
  - [ ] `DashboardController` y `HomeController` (solo redirecciones)
- [ ] Código comentado muerto:
  - [ ] `SecurityConfig.java` líneas 83–124
  - [ ] `UsuarioController.java` líneas 75–103
  - [ ] `GestorController.java` línea 144

### Conservar

- Todos los `*RestController`
- `GestorController` (endpoints JSON + reportes PDF)
- `NotificacionController`, `StockController`
- Capa service, repository, DTOs, mappers, entidades
- Security / JWT
- Archivos `.jrxml` (reportes PDF)

---

## Fase 2 — Correcciones del backend

- [ ] Mover credenciales de `application.properties` a variables de entorno
- [ ] Actualizar JWT: `jjwt 0.9.1` → `jjwt-api 0.12.x`
- [ ] Configurar CORS para permitir peticiones del frontend
- [ ] Agregar Swagger / OpenAPI 3 para documentar la API
- [ ] Resolver `sedeId = 1L` hardcodeado en `GestorController` — obtener desde el usuario autenticado
- [ ] Unificar tipos de ID (`Integer` vs `Long`) → todo `Long`
- [ ] Agregar `bin/` al `.gitignore`
- [ ] Evaluar migración de DB con Flyway o Liquibase (reemplazar `ddl-auto=update`)

---

## Fase 3 — Construcción del frontend

> Tecnología a definir.

### Módulos por rol

| Rol | Módulos |
|-----|---------|
| Admin | Usuarios, Sedes |
| Farmacéutico | Medicamentos, Lotes, Stock, Notificaciones |
| Gestor | Stock general, Notificaciones, Órdenes, Reportes PDF |
| Transportista | Órdenes (ver + avanzar estado) |

### Flujo de autenticación

1. Login → `POST /api/usuarios/login` → recibe JWT
2. Frontend guarda JWT en `localStorage` / `sessionStorage`
3. Cada request incluye header `Authorization: Bearer <token>`
4. Backend valida JWT y retorna datos según rol

---

## Endpoints REST disponibles (resumen)

| Recurso | Base URL | Métodos |
|---------|----------|---------|
| Auth | `/api/usuarios` | POST login, POST registro, GET me |
| Medicamentos | `/farmaceutico/medicamentos/api` | GET, POST, PUT, DELETE |
| Lotes | `/farmaceutico/lotes/api` | GET, POST, PUT, DELETE |
| Stock | `/farmaceutico/stock/api` | GET |
| Notificaciones | `/farmaceutico/notificaciones/api` | GET |
| Usuarios (admin) | `/api/admin/usuarios` | GET, PUT, DELETE |
| Gestor | `/gestor/ordenes`, `/gestor/reportes` | GET, POST |
| Transportista | `/api/transportista/ordenes` | GET, PUT |

---

## Entidades del dominio

```
Sede ──< Medicamento ──< Lote
          │
          └──< Notificacion
Orden ──< DetalleOrden ──> Medicamento
Usuario >── Rol
Usuario >── Sede
```

---

## Notas

- El proyecto es para gestión de inventario en **múltiples sedes en Perú**.
- Cada farmacéutico pertenece a una sede; gestor y admin tienen visibilidad global.
- Los reportes se generan con JasperReports y se sirven como PDF desde el backend.
