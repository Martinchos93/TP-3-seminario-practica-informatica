package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

public class Academia {

    private int id;
    private String razonSocial;
    private String cuit;

    public Academia(int id, String razonSocial, String cuit) throws ValidacionException {
        this.id = id;
        setRazonSocial(razonSocial);
        setCuit(cuit);
    }

    public Academia(String razonSocial, String cuit) throws ValidacionException {
        this(0, razonSocial, cuit);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) throws ValidacionException {
        this.razonSocial = Validaciones.texto(razonSocial, "razón social", 150);
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) throws ValidacionException {
        this.cuit = Validaciones.cuit(cuit);
    }

    @Override
    public String toString() {
        return razonSocial + " (CUIT " + cuit + ")";
    }
}
