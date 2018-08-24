
package Frames;

public class Equipo {
    private String articulo, marca, modelo, password,
            falla, notas, estado, reparador, costo, fecha;
    private int bateria, cargador;
    
    public Equipo()
    {
        this.articulo = null;
        this.marca = null;
        this.modelo = null;
        this.password = null;
        this.falla = null;
        this.notas = null;
        this.estado = null;
        this.reparador = null;
        this.bateria = 0;
        this.cargador = 0;
        this.costo = null;
    }
    
    public Equipo(String articulo, String marca, String modelo, String password,
            String falla, String notas, String estado, String reparador,
            int bateria, int cargador, String costo,String fecha)
    {
        this.articulo = articulo;
        this.marca = marca;
        this.modelo = modelo;
        this.password = password;
        this.falla = falla;
        this.notas = notas;
        this.estado = estado;
        this.reparador = reparador;
        this.bateria = bateria;
        this.cargador = cargador;
        this.costo = costo;
        this.fecha = fecha;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFalla() {
        return falla;
    }

    public void setFalla(String falla) {
        this.falla = falla;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReparador() {
        return reparador;
    }

    public void setReparador(String reparador) {
        this.reparador = reparador;
    }

    public int getBateria() {
        return bateria;
    }

    public void setBateria(int bateria) {
        this.bateria = bateria;
    }

    public int getCargador() {
        return cargador;
    }

    public void setCargador(int cargador) {
        this.cargador = cargador;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    
    

}
