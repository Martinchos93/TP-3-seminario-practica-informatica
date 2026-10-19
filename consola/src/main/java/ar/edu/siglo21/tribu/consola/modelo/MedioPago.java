package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.PagoRechazadoException;
import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class MedioPago {

    private final String titular;

    protected MedioPago(String titular) throws ValidacionException {
        this.titular = Validaciones.texto(titular, "titular", 120);
    }

    public abstract BigDecimal calcularImporteFinal(BigDecimal precio);

    public abstract void autorizar() throws PagoRechazadoException;

    public abstract String getTipo();

    public abstract String getDetalle();

    public String getTitular() {
        return titular;
    }

    protected static BigDecimal aplicarPorcentaje(BigDecimal importe, int porcentaje) {
        BigDecimal factor = BigDecimal.valueOf(100 + porcentaje).divide(BigDecimal.valueOf(100));
        return importe.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return getTipo() + " " + getDetalle() + " a nombre de " + titular;
    }
}
