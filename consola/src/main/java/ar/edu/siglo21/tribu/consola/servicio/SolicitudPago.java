package ar.edu.siglo21.tribu.consola.servicio;

import ar.edu.siglo21.tribu.consola.modelo.Inscripcion;
import ar.edu.siglo21.tribu.consola.modelo.MedioPago;

public class SolicitudPago {

    private final Inscripcion inscripcion;
    private final MedioPago medioPago;

    public SolicitudPago(Inscripcion inscripcion, MedioPago medioPago) {
        this.inscripcion = inscripcion;
        this.medioPago = medioPago;
    }

    public Inscripcion getInscripcion() {
        return inscripcion;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }
}
