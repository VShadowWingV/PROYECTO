package model;

import com.google.gson.annotations.SerializedName;

public class Participante {

    @SerializedName("id")
    private int id;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("alias")
    private String alias;
    @SerializedName("victorias")
    private int victorias_totales;
    @SerializedName("derrotas")
    private int derrotas_totales;

    public Participante(int id, String nombre, String alias) {
        this.id = id;
        this.nombre = nombre;
        this.alias = alias;
        this.victorias_totales = 0;
        this.derrotas_totales = 0;
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

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getVictorias_totales() {
        return victorias_totales;
    }

    public void setVictorias_totales(int victorias_totales) {
        this.victorias_totales = victorias_totales;
    }

    public int getDerrotas_totales() {
        return derrotas_totales;
    }

    public void setDerrotas_totales(int derrotas_totales) {
        this.derrotas_totales = derrotas_totales;
    }

    public void incVictorias(int n){
        this.victorias_totales+=n;
    }

    public void incDerrotas(int n){
        this.derrotas_totales+=n;
    }

    public float getRatioVictorias(){
        if ((victorias_totales + derrotas_totales) == 0) {
            return 0;
        }
        return ((float) victorias_totales /(victorias_totales+derrotas_totales))*100;
    }

    @Override
    public String toString() {
        return "Participante {" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", alias='" + alias + '\'' +
                ", victorias_totales=" + victorias_totales +
                ", derrotas_totales=" + derrotas_totales +
                '}';
    }
}
