package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

import java.math.BigDecimal;

public class TarjetaDebito extends Tarjeta {

    public TarjetaDebito(String titular, String numero) throws ValidacionException {
        super(titular, numero);
    }

    @Override
    public BigDecimal calcularImporteFinal(BigDecimal precio) {
        return aplicarPorcentaje(precio, 0);
    }

    @Override
    public String getTipo() {
        return "TARJETA_DEBITO";
    }
}
