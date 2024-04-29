package model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Participante_Tipo_TC extends Participante{

    private int pool;
    // Esta variable guarda el estado de un participante para comprobar junto a si es loser bracket
    // si el jugador está o no eliminado: 0 -> Ninguna derrota, 1-> Una derrota (eliminado si no loser)
    // 2 -> Dos derrotas (eliminado si loser)
    private int eliminado;
    public Participante_Tipo_TC(int id, String nombre, String alias, int pool) {
        super(id, nombre, alias);
        this.pool = pool;
        this.eliminado = 0;
    }

    public int getPool() {
        return pool;
    }

    public void setPool(int pool) {
        this.pool = pool;
    }

    public int getEliminado() {
        return eliminado;
    }

    public void setEliminado(int eliminado) {
        this.eliminado = eliminado;
    }

    public void incEliminado() {
        this.eliminado++;
    }

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
