/**
 * Gestor_TC: Esta clase gestiona los participantes y la lógica de los torneos de tipo Top Cut.
 */
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
    private ArrayList<MiStructEnfrentamiento> enfrentamientos_registrados = new ArrayList<>();
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

    /**
     * getInstance: Método estático que devuelve la instancia única de Gestor_TC.
     * @return instancia única de Gestor_TC.
     */
    public static Gestor_TC getInstance() {
        if (instancia == null) {
            instancia = new Gestor_TC();
        }
        return instancia;
    }

    // Métodos de acceso a los atributos

    // Métodos de acceso y modificación de la lista de participantes

    public ArrayList<Participante_Tipo_TC> getLista_participantes() {
        return lista_participantes;
    }

    public void setLista_participantes(ArrayList<Participante_Tipo_TC> lista_participantes) {
        this.lista_participantes = lista_participantes;
    }

    // Métodos de acceso y modificación de la lista de enfrentamientos

    public ArrayList<MiStructEnfrentamiento> getEnfrentamientos_registrados() {
        return enfrentamientos_registrados;
    }

    public void setEnfrentamientos_registrados(ArrayList<MiStructEnfrentamiento> enfrentamientos_registrados) {
        this.enfrentamientos_registrados = enfrentamientos_registrados;
    }

    // Métodos de acceso y modificación de la cantidad de rondas

    public int getN_rondas() {
        return n_rondas;
    }

    public void setN_rondas(int n_rondas) {
        this.n_rondas = n_rondas;
    }

    // Métodos de acceso y modificación de la cantidad de jugadores iniciales

    public int getJugadores_iniciales(){
        return this.jugadores_iniciales;
    }

    public void setJugadores_iniciales(int jugadores_iniciales) {
        this.jugadores_iniciales = jugadores_iniciales;
    }

    // Métodos de acceso y modificación de la ronda actual

    public int getRonda_actual() {
        return ronda_actual;
    }

    public void setRonda_actual(int ronda_actual) {
        this.ronda_actual = ronda_actual;
    }

    // Métodos de acceso y modificación del tipo de torneo (loser bracket)

    public boolean isLoser_bracket() {
        return loser_bracket;
    }

    public void setLoser_bracket(boolean loser_bracket) {
        this.loser_bracket = loser_bracket;
    }

    // Métodos de acceso y modificación de la cantidad de partidas por set

    public int getPartidas_set() {
        return partidas_set;
    }

    public void setPartidas_set(int partidas_set) {
        this.partidas_set = partidas_set;
    }

    // Métodos de acceso y modificación de la cantidad de pools

    public int getNum_pools() {
        return num_pools;
    }

    public void setNum_pools(int num_pools) {
        this.num_pools = num_pools;
    }

    // Otros métodos

    /**
     * aniadirParticipanteTC: Método para añadir un participante al torneo.
     * @param participanteTipoTc Participante a añadir.
     */
    public void aniadirParticipanteTC(Participante_Tipo_TC participanteTipoTc){
        this.lista_participantes.add(participanteTipoTc);
    }

    /**
     * aniadirEnfrentamientoRegistrado: Método para añadir un enfrentamiento registrado.
     * @param np1 Nombre del primer participante.
     * @param np2 Nombre del segundo participante.
     * @param res Resultado del enfrentamiento.
     */
    public void aniadirEnfrentamientoRegistrado(String np1, String np2, String res) {
        MiStructEnfrentamiento aniadir = new MiStructEnfrentamiento();
        aniadir.nombreP1 = np1;
        aniadir.nombreP2 = np2;
        aniadir.resultado = res;
        this.enfrentamientos_registrados.add(aniadir);
    }

    /**
     * getParticpanteListaAlias: Método para obtener un participante de la lista por su alias.
     * @param alias Alias del participante a buscar.
     * @return Participante_Tipo_TC si se encuentra, null en caso contrario.
     */
    public Participante_Tipo_TC getParticpanteListaAlias(String alias) {
        Participante_Tipo_TC participante_tipo_tc = null;
        for (Participante_Tipo_TC p: lista_participantes) {
            if (p.getAlias().equals(alias)) {
                participante_tipo_tc = p;
            }
        }
        return participante_tipo_tc;
    }

    /**
     * incRondaActual: Método para incrementar la ronda actual del torneo.
     */
    public void incRondaActual(){
        this.ronda_actual++;
    }

    /**
     * generarArchivoJSONTorneoTopCut: Método para generar un archivo JSON del torneo Top Cut.
     * @param nombreDirectorio Nombre del directorio donde se guardará el archivo.
     * @param nombreArchivo Nombre del archivo en el que se guardará la información del torneo.
     * @param context Contexto de la aplicación para obtener la ruta de los archivos.
     * @throws JSONException Si ocurre un error al crear el objeto JSON.
     */
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

        // Lista de participantes
        JSONArray jsonArrayPart = new JSONArray();
        for (Participante_Tipo_TC participante : instancia.getLista_participantes()) {
            JSONObject participanteJson = participante.toJson();
            jsonArrayPart.put(participanteJson);
        }
        jsonObjectG.put("lista_participantes",jsonArrayPart);

        // Lista de enfrentamientos
        JSONArray jsonArrayEnf = new JSONArray();
        for (MiStructEnfrentamiento enfrentamiento: enfrentamientos_registrados) {
            JSONObject enfrentamientoJson = enfrentamiento.toJson();
            jsonArrayEnf.put(enfrentamientoJson);
        }
        jsonObjectG.put("enfrentamientos_registrados",jsonArrayEnf);

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

    /**
     * actualizarDesdeJson: Método para actualizar la instancia única del Gestor_TC desde un archivo JSON.
     * @param archivoJson El archivo JSON del que se cargarán los datos.
     * @throws JsonSyntaxException Si hay un error de sintaxis al analizar el JSON.
     * @throws JsonIOException Si ocurre un error de E/S al leer el archivo JSON.
     */
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

            for (MiStructEnfrentamiento enf: gestorAuxiliar.getEnfrentamientos_registrados()) {
                this.aniadirEnfrentamientoRegistrado(enf.nombreP1,enf.nombreP2,enf.resultado);
            }

        } catch (JsonSyntaxException | JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Genera un valor hash único basado en el estado del objeto.
     * Este valor se utiliza para indexar y organizar objetos en estructuras de datos como tablas hash.
     * La combinación de varios atributos del objeto garantiza la unicidad del valor hash.
     *
     * @return El valor hash generado para el objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getLista_participantes(), getN_rondas(), getJugadores_iniciales(), getRonda_actual(), isLoser_bracket(), getPartidas_set(), getNum_pools());
    }
}
