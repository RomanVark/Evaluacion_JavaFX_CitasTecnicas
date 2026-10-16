# Pruebas del módulo Clientes (Persona 2)

**Entorno de pruebas:** IntelliJ + Java 21 + PostgreSQL real.

| Caso | Procedimiento | Esperado | Observado | Captura |
|---|---|---|---|---|
| Cliente válido | Registrar nombres, apellidos, teléfono, correo | Fila nueva en tabla | Pendiente | — |
| Duplicado | Guardar de nuevo el correo anterior | Rechazo UNIQUE | Pendiente | — |
| Edición | Seleccionar cliente y cambiar teléfono | Actualizar y persistir | Pendiente | — |
| Búsqueda | Buscar parte de apellido en mayúsculas | Coincidencia parcial ILIKE | Pendiente | — |
| Validación | Dejar nombre vacío y usar correo inválido | No permite guardar | Pendiente | — |
| Borrado restringido | Borrar cliente con cita | Rechazo con motivo | Pendiente | — |
| Borrado permitido | Borrar cliente sin cita, confirmar | Eliminación física | Pendiente | — |

Registrar capturas **reales** en `docs/capturas/` después de las pruebas.
