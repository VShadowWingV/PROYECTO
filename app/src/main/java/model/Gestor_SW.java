/**
 * Gestor_SW: Esta clase gestiona los participantes y la lógica de los torneos de tipo Swiss.
 */
package model;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Gestor_SW {
    private int n_rondas;
    private int jugadores_iniciales;
    private int ronda_actual;
    private boolean es_multijugador;
    private int partidas_set;
    private int jugadores_partida;
    private ArrayList<Participante_Tipo_SW> lista_participantes = new ArrayList<>();

    // SINGLETON
    private static Gestor_SW instancia;

    private Gestor_SW() {
        // Constructor privado para evitar instanciación externa
    }

    /**
     * getInstance: Método estático que devuelve la instancia única de Gestor_SW.
     * @return instancia única de Gestor_SW.
     */
    public static Gestor_SW getInstance() {
        if (instancia == null) {
            instancia = new Gestor_SW();
        }
        return instancia;
    }

    // Métodos de acceso a los atributos

    // Métodos de acceso y modificación de la lista de participantes

    public ArrayList<Participante_Tipo_SW> getLista_participantes() {
        return lista_participantes;
    }

    public void setLista_participantes(ArrayList<Participante_Tipo_SW> lista_participantes) {
        this.lista_participantes = lista_participantes;
    }

    // Métodos de acceso y modificación de la cantidad de rondas

    public int getN_rondas() {
        return n_rondas;
    }

    public void setN_rondas(int n_rondas) {
        this.n_rondas = n_rondas;
    }

    // Métodos de acceso y modificación de la cantidad de jugadores iniciales

    public int getJugadores_iniciales() {
        return jugadores_iniciales;
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

    // Métodos de acceso y modificación del tipo de torneo (multijugador o no)

    public boolean es_multijugador() {
        return es_multijugador;
    }

    public void setEs_multijugador(boolean es_multijugador) {
        this.es_multijugador = es_multijugador;
    }

    // Métodos de acceso y modificación de la cantidad de partidas por set

    public int getPartidas_set() {
        return partidas_set;
    }

    public void setPartidas_set(int partidas_set) {
        this.partidas_set = partidas_set;
    }

    // Métodos de acceso y modificación de la cantidad de jugadores por partida

    public int getJugadores_partida() {
        return jugadores_partida;
    }

    public void setJugadores_partida(int jugadores_partida) {
        this.jugadores_partida = jugadores_partida;
    }

    // Otros métodos

    /**
     * aniadirParticipanteSW: Método para añadir un participante al torneo.
     * @param participanteTipoRr Participante a añadir.
     */
    public void aniadirParticipanteSW(Participante_Tipo_SW participanteTipoRr){
        this.lista_participantes.add(participanteTipoRr);
    }

    /**
     * getParticpanteListaID: Método para obtener un participante de la lista por su ID.
     * @param id ID del participante a buscar.
     * @return Participante_Tipo_SW si se encuentra, null en caso contrario.
     */
    public Participante_Tipo_SW getParticpanteListaID(int id) {
        Participante_Tipo_SW participante_tipo_sw = null;
        for (Participante_Tipo_SW p: lista_participantes) {
            if (p.getId() == id) {
                participante_tipo_sw = p;
            }
        }
        return participante_tipo_sw;
    }

    /**
     * incRondaActual: Método para incrementar la ronda actual del torneo.
     */
    public void incRondaActual(){
        this.ronda_actual++;
    }

    /**
     * devolverSublistaEnfrentamientoI: Método para obtener la sublista de participantes que se enfrentarán en la partida i.
     * @param i Ronda del torneo.
     * @return ArrayList de Participante_Tipo_SW con los participantes que se enfrentarán en la partida i.
     */
    public ArrayList<Participante_Tipo_SW> devolverSublistaEnfrentamientoI(int i){
        ArrayList<Participante_Tipo_SW> listaEnfrentamientoI = new ArrayList<>();
        if (this.es_multijugador) {
            // Si es multijugador, se obtiene la sublista según la cantidad de jugadores por partida
            if (i > 0 && i <= (jugadores_iniciales/jugadores_partida)) {
                for (int j = (i-1)*jugadores_partida; j < ((i-1)*jugadores_partida + jugadores_partida); j++) {
                    listaEnfrentamientoI.add(lista_participantes.get(j));
                }
            }
        } else {
            // Si no es multijugador, se gestionan los enfrentamientos para evitar repeticiones
            if (i > 0 && i <= (jugadores_iniciales/2)) {
                ArrayList<Participante_Tipo_SW>[] vectorDeArrayListsEnf = new ArrayList[this.jugadores_iniciales/2];
                HashSet<Participante_Tipo_SW> participantesConEnfrentamiento = new HashSet<>();

                for (int j = 0; j < this.jugadores_iniciales/2; j++) {
                    vectorDeArrayListsEnf[j] = new ArrayList<>();
                    Participante_Tipo_SW p1 = new Participante_Tipo_SW(0,"",""),
                            p2 = new Participante_Tipo_SW(0,"","");
                    boolean e1 = false, e2 = false;

                    for (int k = 0; k < this.lista_participantes.size(); k++) {
                        if (!e1 && !e2) {
                            if (!participantesConEnfrentamiento.contains(lista_participantes.get(k))) {
                                e1 = true;
                                p1 = lista_participantes.get(k);
                                participantesConEnfrentamiento.add(lista_participantes.get(k));
                            }
                        } else if (e1 && !e2) {
                            if (!participantesConEnfrentamiento.contains(lista_participantes.get(k))
                                    && !p1.getLista_oponentes().contains(lista_participantes.get(k))) {
                                e2 = true;
                                p2 = lista_participantes.get(k);
                                participantesConEnfrentamiento.add(lista_participantes.get(k));
                            }
                        }
                    }

                    if (e1 && e2) {
                        vectorDeArrayListsEnf[j].add(p1);
                        vectorDeArrayListsEnf[j].add(p2);
                    } else if(e1 && !e2) {
                        // En caso de que no haya más participantes sin enfrentamiento, se añade uno aleatorio
                        vectorDeArrayListsEnf[j].add(p1);
                        boolean encontrado = false;
                        int n = 0;
                        while (!encontrado && n<lista_participantes.size()) {
                            if (!participantesConEnfrentamiento.contains(lista_participantes.get(n))) {
                                vectorDeArrayListsEnf[j].add(lista_participantes.get(n));
                                participantesConEnfrentamiento.add(lista_participantes.get(n));
                            }
                            n++;
                        }
                    }
                }

                listaEnfrentamientoI = vectorDeArrayListsEnf[i-1];
            } else if(((jugadores_iniciales % 2) == 1) && (i == (jugadores_iniciales/2) + 1)) {
                listaEnfrentamientoI.add(lista_participantes.get(jugadores_iniciales-1));
            }
        }
        return listaEnfrentamientoI;
    }

    /**
     * Actualiza la instancia única del Gestor_SW con los datos deserializados desde un archivo JSON.
     *
     * Este método permite cargar los datos desde un archivo JSON en la instancia única existente del Gestor_SW.
     * La instancia única se actualiza con los datos deserializados del archivo JSON proporcionado.
     *
     * @param archivoJson El archivo JSON del que se cargarán los datos.
     * @throws JsonSyntaxException Si hay un error de sintaxis al analizar el JSON.
     * @throws JsonIOException Si ocurre un error de E/S al leer el archivo JSON.
     */
    public synchronized void actualizarDesdeJson(File archivoJson) {
        try {
            Gson gson = new Gson();
            Gestor_SW gestorAuxiliar = new Gestor_SW();
            gestorAuxiliar = gson.fromJson(new JsonReader(new FileReader(archivoJson)),Gestor_SW.class);

            this.setN_rondas(gestorAuxiliar.getN_rondas());
            this.setJugadores_iniciales(gestorAuxiliar.getJugadores_iniciales());
            this.setRonda_actual(gestorAuxiliar.getRonda_actual());
            this.setEs_multijugador(gestorAuxiliar.es_multijugador);
            this.setPartidas_set(gestorAuxiliar.getPartidas_set());
            this.setJugadores_partida(gestorAuxiliar.getJugadores_partida());

            for (Participante_Tipo_SW part: gestorAuxiliar.getLista_participantes()) {
                Participante_Tipo_SW aniadir = new Participante_Tipo_SW(part.getId(), part.getNombre(), part.getAlias());
                aniadir.setVictorias_totales(part.getVictorias_totales());
                aniadir.setDerrotas_totales(part.getDerrotas_totales());
                aniadir.setPuntuacion(part.getPuntuacion());
                aniadir.setN_byes(part.getN_byes());

                aniadir.setLista_oponentes(new ArrayList<>());
                this.getLista_participantes().add(aniadir);
            }

            for (Participante_Tipo_SW part: this.getLista_participantes()) {
                Participante_Tipo_SW flujo = gestorAuxiliar.getParticpanteListaID(part.getId());
                if (!flujo.getLista_oponentes().isEmpty()) {
                    for (Participante_Tipo_SW op: flujo.getLista_oponentes()) {
                        part.addOponente(this.getParticpanteListaID(op.getId()));
                    }
                }
            }

        } catch (JsonSyntaxException | JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLista_participantes(), getN_rondas(), getJugadores_iniciales(), getRonda_actual(), es_multijugador, getPartidas_set(), getJugadores_partida());
    }
}
