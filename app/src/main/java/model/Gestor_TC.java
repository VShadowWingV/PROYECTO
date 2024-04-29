package model;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Gestor_TC {
    private ArrayList<Participante_Tipo_TC> lista_participantes = new ArrayList<>();
    private ArrayList<MiStructEnfrentamiento> enfrentamientosRegistrados = new ArrayList<>();
    private int n_rondas;
    private int jugadores_iniciales;
    private int ronda_actual;
    private boolean loser_bracket;
    private int partidas_set;
    private int num_pools;

    //SINGLETON
    private static Gestor_TC instancia;

    private Gestor_TC() {
        // Constructor privado para evitar instanciación externa
    }

    public static Gestor_TC getInstance() {
        if (instancia == null) {
            instancia = new Gestor_TC();
        }
        return instancia;
    }

    public ArrayList<Participante_Tipo_TC> getLista_participantes() {
        return lista_participantes;
    }

    public void setLista_participantes(ArrayList<Participante_Tipo_TC> lista_participantes) {
        this.lista_participantes = lista_participantes;
    }

    public ArrayList<MiStructEnfrentamiento> getEnfrentamientosRegistrados() {
        return enfrentamientosRegistrados;
    }

    public void setEnfrentamientosRegistrados(ArrayList<MiStructEnfrentamiento> enfrentamientosRegistrados) {
        this.enfrentamientosRegistrados = enfrentamientosRegistrados;
    }

    public int getN_rondas() {
        return n_rondas;
    }

    public void setN_rondas(int n_rondas) {
        this.n_rondas = n_rondas;
    }

    public int getJugadores_iniciales(){
        return this.jugadores_iniciales;
    }

    public void setJugadores_iniciales(int jugadores_iniciales) {
        this.jugadores_iniciales = jugadores_iniciales;
    }

    public int getRonda_actual() {
        return ronda_actual;
    }

    public void setRonda_actual(int ronda_actual) {
        this.ronda_actual = ronda_actual;
    }

    public boolean isLoser_bracket() {
        return loser_bracket;
    }

    public void setLoser_bracket(boolean loser_bracket) {
        this.loser_bracket = loser_bracket;
    }

    public int getPartidas_set() {
        return partidas_set;
    }

    public void setPartidas_set(int partidas_set) {
        this.partidas_set = partidas_set;
    }

    public int getNum_pools() {
        return num_pools;
    }

    public void setNum_pools(int num_pools) {
        this.num_pools = num_pools;
    }

    public void aniadirParticipanteTC(Participante_Tipo_TC participanteTipoTc){
        this.lista_participantes.add(participanteTipoTc);
    }

    public void aniadirEnfrentamientoRegistrado(String np1, String np2, String res) {
        MiStructEnfrentamiento aniadir = new MiStructEnfrentamiento();
        aniadir.nombreP1 = np1;
        aniadir.nombreP2 = np2;
        aniadir.resultado = res;
        this.enfrentamientosRegistrados.add(aniadir);
    }

    public Participante_Tipo_TC getParticpanteListaAlias(String alias) {
        Participante_Tipo_TC participante_tipo_tc = null;
        for (Participante_Tipo_TC p: lista_participantes) {
            if (p.getAlias().equals(alias)) {
                participante_tipo_tc = p;
            }
        }
        return participante_tipo_tc;
    }

    public void incRondaActual(){
        this.ronda_actual++;
    }

    public void generarArchivoJSONTorneoTopCut(String nombreDirectorio, String nombreArchivo, Context context) throws JSONException {
        // JSON:
        // Elementos del torneo:
        JSONObject jsonObjectG = new JSONObject();
        jsonObjectG.put("n_rondas", instancia.getN_rondas());
        jsonObjectG.put("jugadores_iniciales", instancia.getJugadores_iniciales());
        if (instancia.getRonda_actual() > instancia.getN_rondas()) {
            jsonObjectG.put("ronda_actual", instancia.getN_rondas());
        } else {
            jsonObjectG.put("ronda_actual", instancia.getRonda_actual());
        }
        jsonObjectG.put("loser_bracket", instancia.isLoser_bracket());
        jsonObjectG.put("partidas_set", instancia.getPartidas_set());
        jsonObjectG.put("num_pools", instancia.getNum_pools());

        JSONArray jsonArrayPart = new JSONArray();
        // Lista de participantes
        for (Participante_Tipo_TC participante : instancia.getLista_participantes()) {
            JSONObject participanteJson = participante.toJson();
            jsonArrayPart.put(participanteJson);
        }
        jsonObjectG.put("lista_participantes",jsonArrayPart);

        String json_res = jsonObjectG.toString();

        String fileName = "torneo_tc_" + nombreArchivo + ".json";
        File directory = new File(context.getExternalFilesDir(null), nombreDirectorio);

        // E/S Ficheros
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Toast.makeText(context, "Creando el directorio: " + directory.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al crear el directorio: " + directory.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        }

        File file = new File(directory, fileName);
        try {
            // Escribir en el archivo JSON
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json_res);
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            Toast.makeText(context, "Error al crear el archivo JSON", Toast.LENGTH_SHORT).show();
        }
    }

    public synchronized void actualizarDesdeJson(File archivoJson) {
        try {
            Gson gson = new Gson();
            Gestor_TC gestorAuxiliar = new Gestor_TC();
            gestorAuxiliar = gson.fromJson(new JsonReader(new FileReader(archivoJson)),Gestor_TC.class);

            this.setN_rondas(gestorAuxiliar.getN_rondas());
            this.setJugadores_iniciales(gestorAuxiliar.getJugadores_iniciales());
            this.setRonda_actual(gestorAuxiliar.getRonda_actual());
            this.setLoser_bracket(gestorAuxiliar.isLoser_bracket());
            this.setPartidas_set(gestorAuxiliar.getPartidas_set());
            this.setNum_pools(gestorAuxiliar.getNum_pools());

            for (Participante_Tipo_TC part: gestorAuxiliar.getLista_participantes()) {
                Participante_Tipo_TC aniadir = new Participante_Tipo_TC(part.getId(), part.getNombre(), part.getAlias(), part.getPool());
                aniadir.setVictorias_totales(part.getVictorias_totales());
                aniadir.setDerrotas_totales(part.getDerrotas_totales());
                aniadir.setEliminado(part.getEliminado());
                this.getLista_participantes().add(aniadir);
            }

        } catch (JsonSyntaxException | JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLista_participantes(), getN_rondas(), getJugadores_iniciales(), getRonda_actual(), isLoser_bracket(), getPartidas_set(), getNum_pools());
    }
}
