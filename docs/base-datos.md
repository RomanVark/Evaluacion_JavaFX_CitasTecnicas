# Instalación y arquitectura de PostgreSQL

1. Crear la base `gestion_citas` en pgAdmin.
2. Abrir Query Tool en **esa misma base**.
3. Ejecutar `gestion_citas.sql`.
4. Establecer `DB_URL`, `DB_USER`, `DB_PASSWORD` en IntelliJ; no publicar valores privados.
5. Iniciar `mvn clean javafx:run` y pulsar **Reintentar conexión** si el servidor no respondía.

## Diseño

- `cliente.id`: identidad y PK; correo UNIQUE; campos NOT NULL.
- `cita_tecnica.id`: identidad y PK; `cliente_id` FK NOT NULL a cliente.
- `ON DELETE RESTRICT`: solo se puede eliminar un cliente sin citas asociadas.
- `CHECK`: restringe los estados a cuatro opciones.
- `ConexionBD`: obtiene Connection, ejecuta SELECT 1 con Statement y ResultSet.
- `DAO`: ejecuta PreparedStatement, usa ResultSet y cierra con try-with-resources.

## Comprobación

Ejecutar el script `sql/verificacion_integridad.sql` en una base de pruebas y
registrar los NOTICE obtenidos. Ese script revierte sus datos de prueba.