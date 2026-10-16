package ni.edu.uam.evaluacion_javafx_citastecnicas.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaTecnica {
    private final int id;
    private final int clienteId;
    private final String nombreCliente;
    private final LocalDate fecha;
    private final LocalTime hora;
    private final String tipoServicio;
    private final String descripcion;
    private final String estado;

    public CitaTecnica(int id, int clienteId, String nombreCliente, LocalDate fecha,
                       LocalTime hora, String tipoServicio, String descripcion, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.nombreCliente = nombreCliente;
        this.fecha = fecha;
        this.hora = hora;
        this.tipoServicio = tipoServicio;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getId() { return id; }
    public int getClienteId() { return clienteId; }
    public String getNombreCliente() { return nombreCliente; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public String getTipoServicio() { return tipoServicio; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
}
