-- Ejecutar conectado a la base gestion_citas (crearla antes desde pgAdmin o psql).
-- Esquema obligatorio: un cliente puede tener muchas citas.
CREATE TABLE IF NOT EXISTS cliente (
                                       id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                       nombres VARCHAR(80) NOT NULL,
                                       apellidos VARCHAR(80) NOT NULL,
                                       telefono VARCHAR(20) NOT NULL,
                                       correo VARCHAR(120) NOT NULL UNIQUE,
                                       activo BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS cita_tecnica (
                                            id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                            cliente_id INTEGER NOT NULL,
                                            fecha DATE NOT NULL,
                                            hora TIME NOT NULL,
                                            tipo_servicio VARCHAR(80) NOT NULL,
                                            descripcion VARCHAR(255) NOT NULL,
                                            estado VARCHAR(20) NOT NULL
                                                CHECK (estado IN ('Pendiente', 'En proceso', 'Finalizada', 'Cancelada')),
                                            CONSTRAINT fk_cita_cliente FOREIGN KEY (cliente_id)
                                                REFERENCES cliente(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_cita_fecha ON cita_tecnica(fecha);
CREATE INDEX IF NOT EXISTS idx_cita_estado ON cita_tecnica(estado);