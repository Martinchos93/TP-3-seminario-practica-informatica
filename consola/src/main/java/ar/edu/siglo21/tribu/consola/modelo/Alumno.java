package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

public class Alumno {

    private int id;
    private String nombre;
    private String apellido;
    private String email;

    public Alumno(int id, String nombre, String apellido, String email) throws ValidacionException {
        this.id = id;
        setNombre(nombre);
        setApellido(apellido);
        setEmail(email);
    }

    public Alumno(String nombre, String apellido, String email) throws ValidacionException {
        this(0, nombre, apellido, email);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) throws ValidacionException {
        this.nombre = Validaciones.texto(nombre, "nombre", 80);
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) throws ValidacionException {
        this.apellido = Validaciones.texto(apellido, "apellido", 80);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws ValidacionException {
        this.email = Validaciones.email(email);
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " <" + email + ">";
    }
}
