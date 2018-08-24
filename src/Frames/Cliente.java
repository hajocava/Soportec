/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Frames;

/**
 *
 * @author hajoc
 */
public class Cliente {
    private String nombre,domicilio, rfc, email, telefono;
    
    public Cliente()
    {
        this.nombre = null;
        this.domicilio = null;
        this.rfc = null;
        this.email = null;
        this.telefono = null;
    }
    
    public Cliente(String nombre,String domicilio, String rfc, String email, String telefono)
    {
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.rfc = rfc;
        this.email = email;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    
    
}
