package model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Participante_Tipo_TC extends Participante{

    @SerializedName("pool")
    private int pool;
    @SerializedName("eliminado")
    // Esta variable guarda el estado de un participante para comprobar junto a si es loser bracket
    // si el jugador está o no eliminado: 0 -> Ninguna derrota, 1-> Una derrota (eliminado si no loser)
    // 2 -> Dos derrotas (eliminado si loser)
    private int eliminado;

    /**
     * Constructor de Participante_Tipo_SW.
     * @param id El ID del participante.
     * @param nombre El nombre del participante.
     * @param alias El alias del participante.
     * @param pool El pool del participante
     */
    public Participante_Tipo_TC(int id, String nombre, String alias, int pool) {
        super(id, nombre, alias);
        this.pool = pool;
        this.eliminado = 0;
    }

    // Getters y setters

    /**
     * Devuelve la pool del participante.
     * @return La pool del participante.
     */
    public int getPool() {
        return pool;
    }

    /**
     * Establece la pool del participante.
     * @param pool La pool del participante.
     */
    public void setPool(int pool) {
        this.pool = pool;
    }

    /**
     * Devuelve el estado del participante.
     * @return El estado del participante.
     */
    public int getEliminado() {
        return eliminado;
    }

    /**
     * Establece el estado del participante.
     * @param eliminado El estado del participante.
     */
    public void setEliminado(int eliminado) {
        this.eliminado = eliminado;
    }

    /**
     * Incrementa el estado del participante.
     */
    public void incEliminado() {
        this.eliminado++;
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
            jsonObject.put("victorias_totales", getVictorias_totales());
            jsonObject.put("derrotas_totales", getDerrotas_totales());
            jsonObject.put("pool", getPool());
            jsonObject.put("eliminado", getEliminado());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

}
