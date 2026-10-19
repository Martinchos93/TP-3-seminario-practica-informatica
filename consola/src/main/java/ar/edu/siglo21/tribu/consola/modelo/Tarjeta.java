package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.PagoRechazadoException;
import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

public abstract class Tarjeta extends MedioPago {

    private final String numero;

    protected Tarjeta(String titular, String numero) throws ValidacionException {
        super(titular);
        String digitos = numero == null ? "" : numero.replace(" ", "");
        if (!digitos.matches("\\d{16}")) {
            throw new ValidacionException("El número de tarjeta debe tener 16 dígitos");
        }
        if (!Validaciones.pasaLuhn(digitos) && !esNumeroDePrueba(digitos)) {
            throw new ValidacionException("El número de tarjeta no es válido");
        }
        this.numero = digitos;
    }

    private static boolean esNumeroDePrueba(String digitos) {
        return digitos.endsWith("0000") || digitos.endsWith("1111");
    }

    @Override
    public void autorizar() throws PagoRechazadoException {
        if (numero.endsWith("0000")) {
            throw new PagoRechazadoException("fondos insuficientes");
        }
        if (numero.endsWith("1111")) {
            throw new PagoRechazadoException("tarjeta vencida");
        }
    }

    @Override
    public String getDetalle() {
        return "terminada en " + numero.substring(12);
    }
}
