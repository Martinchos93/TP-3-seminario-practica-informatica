package ar.edu.siglo21.tribu.consola.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pago {

    private int id;
    private final Inscripcion inscripcion;
    private final String medio;
    private final String detalle;
    private final BigDecimal importe;
    private final EstadoPago estado;
    private final String motivoRechazo;
    private final LocalDateTime fecha;

    public Pago(Inscripcion inscripcion, MedioPago medioPago, BigDecimal importe, EstadoPago estado, String motivoRechazo) {
        this.inscripcion = inscripcion;
        this.medio = medioPago.getTipo();
        this.detalle = medioPago.getDetalle();
        this.importe = importe;
        this.estado = estado;
        this.motivoRechazo = motivoRechazo;
        this.fecha = LocalDateTime.now();
    }

    public boolean fueAprobado() {
        return estado == EstadoPago.APROBADO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Inscripcion getInscripcion() {
        return inscripcion;
    }

    public String getMedio() {
        return medio;
    }

    public String getDetalle() {
        return detalle;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
