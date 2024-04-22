/**
 * Participante_Tipo_SW: Esta clase extiende la clase Participante y representa un tipo específico de participante
 * utilizado en el contexto de un torneo de tipo "Swiss". Contiene atributos adicionales como puntuación, lista de
 * oponentes y número de byes.
 */
package model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Participante_Tipo_SW extends Participante{

    @SerializedName("puntuacion")
    private int puntuacion;
    @SerializedName("listaOponentes")
    private ArrayList<Participante_Tipo_SW> lista_oponentes;
    @SerializedName("byes")
    private int n_byes;

    /**
     * Constructor de Participante_Tipo_SW.
     * @param id El ID del participante.
     * @param nombre El nombre del participante.
     * @param alias El alias del participante.
     */
    public Participante_Tipo_SW(int id, String nombre, String alias) {
        super(id, nombre, alias);
        this.puntuacion = 0;
        this.lista_oponentes = new ArrayList<>();
        this.n_byes = 0;
    }

    // Getters y setters

    /**
     * Devuelve la puntuación del participante.
     * @return La puntuación del participante.
     */
    public int getPuntuacion() {
        return puntuacion;
    }

    /**
     * Establece la puntuación del participante.
     * @param puntuacion La puntuación del participante.
     */
    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    /**
     * Devuelve la lista de oponentes del participante.
     * @return La lista de oponentes del participante.
     */
    public ArrayList<Participante_Tipo_SW> getLista_oponentes() {
        return lista_oponentes;
    }

    /**
     * Establece la lista de oponentes del participante.
     * @param lista_oponentes La lista de oponentes del participante.
     */
    public void setLista_oponentes(ArrayList<Participante_Tipo_SW> lista_oponentes) {
        this.lista_oponentes = lista_oponentes;
    }

    /**
     * Devuelve el número de byes del participante.
     * @return El número de byes del participante.
     */
    public int getN_byes() {
        return n_byes;
    }

    /**
     * Establece el número de byes del participante.
     * @param n_byes El número de byes del participante.
     */
    public void setN_byes(int n_byes) {
        this.n_byes = n_byes;
    }

    /**
     * Agrega un oponente a la lista de oponentes del participante.
     * @param oponente El oponente a agregar.
     */
    public void addOponente(Participante_Tipo_SW oponente) {
        lista_oponentes.add(oponente);
    }

    /**
     * Incrementa el número de byes del participante en uno.
     */
    public void incN_byes(){
        this.n_byes++;
    }

    /**
     * Incrementa la puntuación del participante en una cantidad dada.
     * @param n La cantidad de puntuación a incrementar.
     */
    public void incPunct(int n) {
        this.puntuacion += n;
    }

    /**
     * Calcula el ratio de victorias medio de los oponentes del participante.
     * @return El ratio de victorias medio de los oponentes del participante.
     */
    public float getRatioVictoriasMedioOp() {
        float mediaOp = 0f;
        int count = 0;
        if (!lista_oponentes.isEmpty()) {
            for (Participante_Tipo_SW oponente : lista_oponentes) {
                if (oponente != null) {
                    mediaOp += oponente.getRatioVictorias();
                    count++;
                }
            }
            if (count > 0) {
                mediaOp = mediaOp / count;
            }
        }
        return mediaOp;
    }

    /**
     * Convierte el objeto a formato JSON.
     * @return El objeto en formato JSON.
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", getId());
            jsonObject.put("nombre", getNombre());
            jsonObject.put("alias", getAlias());
            jsonObject.put("puntuacion", puntuacion);
            jsonObject.put("n_byes", n_byes);

            JSONArray oponentesArray = new JSONArray();
            for (Participante_Tipo_SW oponente : lista_oponentes) {
                JSONObject oponenteObject = new JSONObject();
                oponenteObject.put("id", oponente.getId());
                oponenteObject.put("nombre", oponente.getNombre());
                oponenteObject.put("alias", oponente.getAlias());
                oponentesArray.put(oponenteObject);
            }
            jsonObject.put("oponentes", oponentesArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }
}
