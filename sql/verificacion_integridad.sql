-- Pruebas aisladas: todos los cambios se revierten al finalizar.
BEGIN;
DO $$
    DECLARE cliente_prueba INTEGER;
    BEGIN
        INSERT INTO cliente(nombres,apellidos,telefono,correo,activo)
        VALUES ('Prueba','Temporal','8000','prueba.integridad@example.com',true)
        RETURNING id INTO cliente_prueba;

        BEGIN
            INSERT INTO cliente(nombres,apellidos,telefono,correo,activo)
            VALUES ('Otro','Duplicado','8001','prueba.integridad@example.com',true);
            RAISE EXCEPTION 'ERROR: UNIQUE no rechazó un correo duplicado';
        EXCEPTION WHEN unique_violation THEN
            RAISE NOTICE 'OK: UNIQUE correo rechazó el duplicado';
        END;

        BEGIN
            INSERT INTO cita_tecnica(cliente_id,fecha,hora,tipo_servicio,descripcion,estado)
            VALUES (cliente_prueba, CURRENT_DATE, '09:00', 'Prueba', 'Prueba', 'No permitido');
            RAISE EXCEPTION 'ERROR: CHECK permitió estado inválido';
        EXCEPTION WHEN check_violation THEN
            RAISE NOTICE 'OK: CHECK rechazó estado inválido';
        END;

        BEGIN
            INSERT INTO cliente(nombres,apellidos,telefono,correo,activo)
            VALUES (NULL,'Invalido','8002','prueba.null@example.com',true);
            RAISE EXCEPTION 'ERROR: NOT NULL permitió valor nulo';
        EXCEPTION WHEN not_null_violation THEN
            RAISE NOTICE 'OK: NOT NULL rechazó un campo nulo';
        END;

        INSERT INTO cita_tecnica(cliente_id,fecha,hora,tipo_servicio,descripcion,estado)
        VALUES (cliente_prueba, CURRENT_DATE, '09:00', 'Prueba', 'Prueba', 'Pendiente');

        BEGIN
            DELETE FROM cliente WHERE id=cliente_prueba;
            RAISE EXCEPTION 'ERROR: FK permitió eliminar un cliente con citas';
        EXCEPTION WHEN foreign_key_violation OR restrict_violation THEN
            RAISE NOTICE 'OK: la FK impide eliminar un cliente con citas';
        END;

        DELETE FROM cita_tecnica WHERE cliente_id=cliente_prueba;
        DELETE FROM cliente WHERE id=cliente_prueba;
        RAISE NOTICE 'OK: se permite eliminar la cita y luego el cliente';
    END $$;
ROLLBACK;