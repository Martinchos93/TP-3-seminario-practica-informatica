package ar.edu.siglo21.tribu.consola;

import ar.edu.siglo21.tribu.consola.excepciones.PersistenciaException;
import ar.edu.siglo21.tribu.consola.servicio.Plataforma;
import ar.edu.siglo21.tribu.consola.vista.Consola;
import ar.edu.siglo21.tribu.consola.vista.MenuPrincipal;

import java.util.Locale;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Locale.setDefault(Locale.forLanguageTag("es-AR"));
        try (Scanner entrada = new Scanner(System.in)) {
            Consola consola = new Consola(entrada);
            consola.mostrar("Conectando con la base de datos MySQL...");
            Plataforma plataforma = new Plataforma();
            consola.mostrarExito("Datos cargados.");
            new MenuPrincipal(plataforma, consola).iniciar();
        } catch (PersistenciaException e) {
            System.out.println("[ERROR] " + e.getMessage());
            System.out.println("Verificá que MySQL esté corriendo (docker compose up -d mysql).");
            System.exit(1);
        } catch (Consola.FinDeEntradaException e) {
            System.out.println();
            System.out.println("Fin de la entrada. Hasta luego.");
        }
    }
}
