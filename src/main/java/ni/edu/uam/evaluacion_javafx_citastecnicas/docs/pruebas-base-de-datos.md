# Verificación del esquema

Ejecutar `sql/verificacion_integridad.sql` conectado a una base de pruebas
`gestion_citas`. El script hace ROLLBACK de sus registros de prueba.

- Comprobar SQLSTATE 23505: correo UNIQUE.
- Comprobar SQLSTATE 23503: FK restrictiva.
- Comprobar SQLSTATE 23514: estado CHECK.
- Comprobar SQLSTATE 23502: dato NOT NULL.
- Comprobar `SELECT 1` desde `ConexionBD.comprobarConexion()`.

Documentar fecha, equipo, resultado real y cualquier fallo.