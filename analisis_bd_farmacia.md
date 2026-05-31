# Análisis de Base de Datos — Sistema de Farmacia Multisede

## Resumen ejecutivo

El esquema actual tiene una base funcional, pero presenta problemas críticos para un sistema que opera en **múltiples sedes**. Los principales issues son: stock acoplado a una sola sede, falta de trazabilidad de movimientos, y una tabla `orden` incompleta.

---

## Problemas críticos 🔴

### 1. Stock por sede no está modelado correctamente

`medicamento` tiene `id_sede` y `stock_total` directamente, lo que implica que **un medicamento existe en una sola sede**. En un sistema multisede esto es incorrecto.

**Solución: tabla intermedia `medicamento_sede`**

```sql
-- Eliminar id_sede y stock_total de medicamento
-- Crear tabla puente:
CREATE TABLE medicamento_sede (
    id_medicamento INT  NOT NULL,
    id_sede        INT  NOT NULL,
    stock_total    INT  NOT NULL DEFAULT 0,
    PRIMARY KEY (id_medicamento, id_sede),
    FOREIGN KEY (id_medicamento) REFERENCES medicamento(id_medicamento),
    FOREIGN KEY (id_sede)        REFERENCES sede(id_sede)
);
```

Así el mismo medicamento puede existir en N sedes con su propio stock independiente.

---

### 2. `lote` no tiene `id_sede`

Un lote llega a una sede específica. Sin este campo no puedes saber dónde está físicamente cada lote, lo que hace imposible gestionar inventario por ubicación.

```sql
ALTER TABLE lote 
ADD COLUMN id_sede INT NOT NULL,
ADD CONSTRAINT fk_lote_sede FOREIGN KEY (id_sede) REFERENCES sede(id_sede);
```

---

### 3. `orden` está incompleta

Actualmente solo tiene `id_orden`, `fecha` e `id_gestor`. Faltan campos esenciales:

| Campo faltante | Por qué es necesario |
|---|---|
| `id_usuario` FK | ¿Quién generó la orden? |
| `id_sede` FK | ¿En qué sede se ejecuta? |
| `tipo VARCHAR(30)` | ¿Es compra, transferencia entre sedes, devolución? |
| `estado VARCHAR(30)` | PENDIENTE, APROBADA, RECHAZADA, etc. |

> **Nota:** `id_gestor BIGINT` parece una FK hacia `usuario`, pero no está declarada como tal. Debe reemplazarse por `id_usuario INT` con FK explícita.

---

## Problemas importantes 🟠

### 4. `stock_total` como columna almacenada es peligroso

Si tienes `lote` con `stock_lote` por cada lote, el stock total **debería calcularse desde ahí**, no guardarse como columna. Mantener ambos sincronizados manualmente es fuente directa de bugs e inconsistencias.

**Solución recomendada: vista calculada**

```sql
CREATE VIEW vista_stock_medicamento_sede AS
SELECT 
    l.id_medicamento,
    l.id_sede,
    SUM(l.stock_lote) AS stock_total
FROM lote l
WHERE l.fecha_caducidad > CURRENT_DATE
GROUP BY l.id_medicamento, l.id_sede;
```

---

### 5. Falta tabla de movimientos de stock (kardex)

Sin esta tabla no puedes auditar quién consumió qué, ni reconstruir el historial de inventario ante discrepancias.

```sql
CREATE TABLE movimiento_stock (
    id_movimiento  BIGINT       PRIMARY KEY AUTO_INCREMENT,
    id_medicamento INT          NOT NULL,
    id_sede        INT          NOT NULL,
    id_lote        INT,
    tipo           VARCHAR(30)  NOT NULL, -- ENTRADA, SALIDA, TRANSFERENCIA, VENCIMIENTO, AJUSTE
    cantidad       INT          NOT NULL,
    fecha          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario     INT          NOT NULL, -- quién registró el movimiento
    observacion    VARCHAR(255),
    FOREIGN KEY (id_medicamento) REFERENCES medicamento(id_medicamento),
    FOREIGN KEY (id_sede)        REFERENCES sede(id_sede),
    FOREIGN KEY (id_lote)        REFERENCES lote(id_lote),
    FOREIGN KEY (id_usuario)     REFERENCES usuario(id_usuario)
);
```

---

### 6. Transferencias entre sedes no están modeladas

Es un flujo crítico en sistemas multisede. Una opción es usar la misma tabla `orden` con `tipo = 'TRANSFERENCIA'` y añadir un campo `id_sede_destino`:

```sql
ALTER TABLE orden ADD COLUMN id_sede_destino INT NULL,
ADD CONSTRAINT fk_orden_sede_destino FOREIGN KEY (id_sede_destino) REFERENCES sede(id_sede);
```

---

## Mejoras menores 🟡

### 7. Tipos de datos sobredimensionados

| Campo | Actual | Recomendado |
|---|---|---|
| `telefono VARCHAR(255)` | usuario | `VARCHAR(20)` |
| `password VARCHAR(255)` | usuario | `CHAR(60)` si usas bcrypt |
| `tipo VARCHAR(255)` | notificacion | `VARCHAR(50)` |

---

### 8. `notificacion` con FKs opcionales sueltas

Tener `id_medicamento` e `id_sede` directos en notificación mezcla responsabilidades. Si las notificaciones son de distintos tipos (stock bajo, vencimiento, orden), considera:

```sql
-- Opción simple: agregar id_referencia + tipo
ALTER TABLE notificacion 
ADD COLUMN entidad_tipo VARCHAR(50), -- 'MEDICAMENTO', 'ORDEN', 'SEDE'
ADD COLUMN entidad_id   BIGINT;      -- ID del registro referenciado
```

---

## Esquema corregido — resumen de tablas

```
lote              → + id_sede FK
medicamento       → - id_sede, - stock_total
medicamento_sede  → NUEVA (id_medicamento + id_sede + stock_total)
orden             → + id_usuario FK, + id_sede FK, + id_sede_destino FK, + tipo, + estado
movimiento_stock  → NUEVA (kardex completo)
usuario           → telefono y password con tipos ajustados
notificacion      → FKs opcionales consolidadas
```

---

## Prioridad de cambios

| # | Cambio | Prioridad |
|---|---|---|
| 1 | Crear `medicamento_sede` y quitar `id_sede`/`stock_total` de `medicamento` | 🔴 Crítico |
| 2 | Agregar `id_sede` a `lote` | 🔴 Crítico |
| 3 | Completar `orden` con `id_usuario`, `id_sede`, `tipo`, `estado` | 🔴 Crítico |
| 4 | Crear `movimiento_stock` (kardex) | 🟠 Importante |
| 5 | Modelar transferencias entre sedes | 🟠 Importante |
| 6 | Convertir `stock_total` en vista calculada | 🟡 Recomendado |
| 7 | Ajustar tipos de datos sobredimensionados | 🟡 Recomendado |
| 8 | Refactorizar FKs de `notificacion` | 🟡 Recomendado |
