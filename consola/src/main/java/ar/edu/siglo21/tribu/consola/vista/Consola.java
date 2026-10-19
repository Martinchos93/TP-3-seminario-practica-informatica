package ar.edu.siglo21.tribu.consola.vista;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Consola {

    private final Scanner entrada;

    public Consola(Scanner entrada) {
        this.entrada = entrada;
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        try {
            return entrada.nextLine().trim();
        } catch (NoSuchElementException e) {
            throw new FinDeEntradaException();
        }
    }

    public int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
                mostrarError("Ingresá un número entre " + minimo + " y " + maximo + ".");
            } catch (NumberFormatException e) {
                mostrarError("\"" + texto + "\" no es un número entero.");
            }
        }
    }

    public BigDecimal leerImporte(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).replace(",", ".");
            try {
                BigDecimal valor = new BigDecimal(texto);
                if (valor.signum() > 0) {
                    return valor;
                }
                mostrarError("El importe debe ser mayor que cero.");
            } catch (NumberFormatException e) {
                mostrarError("\"" + texto + "\" no es un importe válido.");
            }
        }
    }

    public boolean confirmar(String mensaje) {
        String respuesta = leerTexto(mensaje + " (s/n): ");
        return respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("si");
    }

    public void mostrar(String texto) {
        System.out.println(texto);
    }

    public void mostrarTitulo(String titulo) {
        System.out.println();
        System.out.println("--- " + titulo + " ---");
    }

    public void mostrarExito(String texto) {
        System.out.println("[OK] " + texto);
    }

    public void mostrarError(String texto) {
        System.out.println("[ERROR] " + texto);
    }

    public void pausar() {
        leerTexto("Presioná Enter para volver al menú...");
    }

    public static class FinDeEntradaException extends RuntimeException {
        public FinDeEntradaException() {
            super("Se terminó la entrada estándar");
        }
    }
}
