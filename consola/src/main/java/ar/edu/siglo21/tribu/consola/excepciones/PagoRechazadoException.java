package ar.edu.siglo21.tribu.consola.excepciones;

public class PagoRechazadoException extends TribuException {

    private final String motivo;

    public PagoRechazadoException(String motivo) {
        super("Pago rechazado: " + motivo);
        this.motivo = motivo;
    }

    public String getMotivo() {
        return motivo;
    }
}
