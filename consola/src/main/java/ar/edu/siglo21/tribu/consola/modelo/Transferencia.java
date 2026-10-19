package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.PagoRechazadoException;
import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

import java.math.BigDecimal;

public class Transferencia extends MedioPago {

    private static final int DESCUENTO = 5;

    private final String cbu;

    public Transferencia(String titular, String cbu) throws ValidacionException {
        super(titular);
        if (cbu == null || !cbu.trim().matches("\\d{22}")) {
            throw new ValidacionException("El CBU debe tener 22 dígitos");
        }
        this.cbu = cbu.trim();
    }

    @Override
    public BigDecimal calcularImporteFinal(BigDecimal precio) {
        return aplicarPorcentaje(precio, -DESCUENTO);
    }

    @Override
    public void autorizar() throws PagoRechazadoException {
        if (cbu.startsWith("000")) {
            throw new PagoRechazadoException("CBU inexistente");
        }
    }

    @Override
    public String getTipo() {
        return "TRANSFERENCIA";
    }

    @Override
    public String getDetalle() {
        return "desde CBU terminado en " + cbu.substring(18);
    }
}
