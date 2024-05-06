/**
 * MiStructParticipante: Esta clase define una estructura para representar la información de un participante en un torneo.
 */
package model;

import org.json.JSONException;
import org.json.JSONObject;

public class MiStructEnfrentamiento {
    String nombreP1;
    String nombreP2;
    String resultado;

    /**
     * Convierte el objeto a formato JSON.
     * @return El objeto en formato JSON.
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nombreP1", nombreP1);
            jsonObject.put("nombreP2", nombreP2);
            jsonObject.put("resultado", resultado);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

}