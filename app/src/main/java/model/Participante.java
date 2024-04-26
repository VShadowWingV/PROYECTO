/**
 * Participante: Esta clase representa un participante en un torneo, con sus atributos como id, nombre, alias,
 * victorias totales y derrotas totales.
 */
package model;

import com.google.gson.annotations.SerializedName;

public class Participante {

    @SerializedName("id")
    private int id;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("alias")
    private String alias;
    @SerializedName("victorias_totales")
    private int victorias_totales;
    @SerializedName("derrotas_totales")
    private int derrotas_totales;

    /**
     * Constructor de Participante.
     * @param id El id del participante.
     * @param nombre El nombre del participante.
     * @param alias El alias del participante.
     */
    public Participante(int id, String nombre, String alias) {
        this.id = id;
        this.nombre = nombre;
        this.alias = alias;
        this.victorias_totales = 0;
        this.derrotas_totales = 0;
    }

    // Getters y setters

    /**
     * Devuelve el ID del participante.
     * @return El ID del participante.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del participante.
     * @param id El ID del participante.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre del participante.
     * @return El nombre del participante.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del participante.
     * @param nombre El nombre del participante.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el alias del participante.
     * @return El alias del participante.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Establece el alias del participante.
     * @param alias El alias del participante.
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Devuelve el número de victorias totales del participante.
     * @return El número de victorias totales del participante.
     */
    public int getVictorias_totales() {
        return victorias_totales;
    }

    /**
     * Establece el número de victorias totales del participante.
     * @param victorias_totales El número de victorias totales del participante.
     */
    public void setVictorias_totales(int victorias_totales) {
        this.victorias_totales = victorias_totales;
    }

    /**
     * Devuelve el número de derrotas totales del participante.
     * @return El número de derrotas totales del participante.
     */
    public int getDerrotas_totales() {
        return derrotas_totales;
    }

    /**
     * Establece el número de derrotas totales del participante.
     * @param derrotas_totales El número de derrotas totales del participante.
     */
    public void setDerrotas_totales(int derrotas_totales) {
        this.derrotas_totales = derrotas_totales;
    }

    /**
     * Incrementa el número de victorias totales del participante en una cantidad dada.
     * @param n La cantidad de victorias a incrementar.
     */
    public void incVictorias(int n){
        this.victorias_totales += n;
    }

    /**
     * Incrementa el número de derrotas totales del participante en una cantidad dada.
     * @param n La cantidad de derrotas a incrementar.
     */
    public void incDerrotas(int n){
        this.derrotas_totales += n;
    }

    /**
     * Calcula el ratio de victorias del participante.
     * @return El ratio de victorias del participante.
     */
    public float getRatioVictorias(){
        if ((victorias_totales + derrotas_totales) == 0) {
            return 0;
        }
        return ((float) victorias_totales /(victorias_totales+derrotas_totales))*100;
    }

    /**
     * Devuelve una representación en cadena del participante.
     * @return Una representación en cadena del participante.
     */
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
