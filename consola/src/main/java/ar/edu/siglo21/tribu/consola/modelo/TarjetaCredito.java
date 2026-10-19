package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

import java.math.BigDecimal;

public class TarjetaCredito extends Tarjeta {

    private final int cuotas;

    public TarjetaCredito(String titular, String numero, int cuotas) throws ValidacionException {
        super(titular, numero);
        if (cuotas != 1 && cuotas != 3 && cuotas != 6) {
            throw new ValidacionException("Las cuotas permitidas son 1, 3 o 6");
        }
        this.cuotas = cuotas;
    }

    public int getCuotas() {
        return cuotas;
    }

    @Override
    public BigDecimal calcularImporteFinal(BigDecimal precio) {
        int recargo = switch (cuotas) {
            case 3 -> 10;
            case 6 -> 20;
            default -> 0;
        };
        return aplicarPorcentaje(precio, recargo);
    }

    @Override
    public String getTipo() {
        return "TARJETA_CREDITO";
    }

    @Override
    public String getDetalle() {
        return super.getDetalle() + " en " + cuotas + (cuotas == 1 ? " cuota" : " cuotas");
    }
}
