package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class Validaciones {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern CUIT = Pattern.compile("\\d{2}-\\d{8}-\\d");
    private static final int[] PESOS_CUIT = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

    private Validaciones() {
    }

    public static String texto(String valor, String campo, int maximo) throws ValidacionException {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionException("El campo " + campo + " es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new ValidacionException("El campo " + campo + " admite hasta " + maximo + " caracteres");
        }
        return limpio;
    }

    public static String email(String valor) throws ValidacionException {
        String limpio = texto(valor, "email", 120).toLowerCase();
        if (!EMAIL.matcher(limpio).matches()) {
            throw new ValidacionException("El email " + valor + " no tiene un formato válido");
        }
        return limpio;
    }

    public static BigDecimal importePositivo(BigDecimal valor, String campo) throws ValidacionException {
        if (valor == null || valor.signum() <= 0) {
            throw new ValidacionException("El " + campo + " debe ser mayor que cero");
        }
        return valor;
    }

    public static int enteroPositivo(int valor, String campo) throws ValidacionException {
        if (valor <= 0) {
            throw new ValidacionException("El campo " + campo + " debe ser mayor que cero");
        }
        return valor;
    }

    public static String cuit(String valor) throws ValidacionException {
        if (valor == null || !CUIT.matcher(valor.trim()).matches()) {
            throw new ValidacionException("La CUIT debe tener el formato 00-00000000-0");
        }
        String digitos = valor.trim().replace("-", "");
        int suma = 0;
        for (int i = 0; i < PESOS_CUIT.length; i++) {
            suma += (digitos.charAt(i) - '0') * PESOS_CUIT[i];
        }
        int verificador = 11 - suma % 11;
        if (verificador == 11) {
            verificador = 0;
        }
        if (verificador == 10 || verificador != digitos.charAt(10) - '0') {
            throw new ValidacionException("La CUIT " + valor + " no es válida: el dígito verificador no coincide");
        }
        return valor.trim();
    }

    public static boolean pasaLuhn(String numero) {
        int suma = 0;
        boolean duplicar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = numero.charAt(i) - '0';
            if (duplicar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            duplicar = !duplicar;
        }
        return suma % 10 == 0;
    }
}
