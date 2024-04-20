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

    public Participante_Tipo_SW(int id, String nombre, String alias) {
        super(id, nombre, alias);
        this.puntuacion = 0;
        this.lista_oponentes = new ArrayList<>();
        this.n_byes = 0;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public ArrayList<Participante_Tipo_SW> getLista_oponentes() {
        return lista_oponentes;
    }

    public void setLista_oponentes(ArrayList<Participante_Tipo_SW> lista_oponentes) {
        this.lista_oponentes = lista_oponentes;
    }

    public int getN_byes() {
        return n_byes;
    }

    public void setN_byes(int n_byes) {
        this.n_byes = n_byes;
    }

    public void addOponente(Participante_Tipo_SW oponente) {
        lista_oponentes.add(oponente);
    }

    public void incN_byes(){
        this.n_byes++;
    }

    public void incPunct(int n) {
        this.puntuacion += n;
    }

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

    public JSONObject toJson() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", getId());jsonObject.put("nombre", getNombre());
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
