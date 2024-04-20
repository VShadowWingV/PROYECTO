package model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class Gestor_SW {
    private ArrayList<Participante_Tipo_SW> lista_participantes = new ArrayList<>();
    private int n_rondas;
    private int jugadores_iniciales;
    private int ronda_actual;
    private boolean es_multijugador;
    private int partidas_set;
    private int jugadores_partida;

    //SINGLETON
    private static Gestor_SW instancia;

    private Gestor_SW() {
        // Constructor privado para evitar instanciación externa
    }

    public static Gestor_SW getInstance() {
        if (instancia == null) {
            instancia = new Gestor_SW();
        }
        return instancia;
    }

    public ArrayList<Participante_Tipo_SW> getLista_participantes() {
        return lista_participantes;
    }

    public void setLista_participantes(ArrayList<Participante_Tipo_SW> lista_participantes) {
        this.lista_participantes = lista_participantes;
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

    public boolean es_multijugador() {
        return es_multijugador;
    }

    public void setEs_multijugador(boolean es_multijugador) {
        this.es_multijugador = es_multijugador;
    }

    public int getPartidas_set() {
        return partidas_set;
    }

    public void setPartidas_set(int partidas_set) {
        this.partidas_set = partidas_set;
    }

    public int getJugadores_partida() {
        return jugadores_partida;
    }

    public void setJugadores_partida(int jugadores_partida) {
        this.jugadores_partida = jugadores_partida;
    }

    public void aniadirParticipanteSW(Participante_Tipo_SW participanteTipoRr){
        this.lista_participantes.add(participanteTipoRr);
    }

    public Participante_Tipo_SW getParticpanteListaID(int id) {
        Participante_Tipo_SW participante_tipo_sw = null;
        for (Participante_Tipo_SW p: lista_participantes) {
            if (p.getId() == id) {
                participante_tipo_sw = p;
            }
        }
        return participante_tipo_sw;
    }

    public Participante_Tipo_SW getParticpanteListaNombre(String nombre) {
        Participante_Tipo_SW participante_tipo_sw = null;
        for (Participante_Tipo_SW p: lista_participantes) {
            if (p.getNombre().equals(nombre)) {
                participante_tipo_sw = p;
            }
        }
        return participante_tipo_sw;
    }

    public void incRondaActual(){
        this.ronda_actual++;
    }

    public ArrayList<Participante_Tipo_SW> devolverSublistaEnfrentamientoI(int i){
        ArrayList<Participante_Tipo_SW> listaEnfrentamientoI = new ArrayList<>();
        if (this.es_multijugador) {
            // Si es multijugador cojo para cada enfrentamiento i su ordenación y el numero de jugadores por partida
            if (i > 0 && i <= (jugadores_iniciales/jugadores_partida)) {
                for (int j = (i-1)*jugadores_partida; j < ((i-1)*jugadores_partida + jugadores_partida); j++) {
                    listaEnfrentamientoI.add(lista_participantes.get(j));
                }
            }
        } else {
            // Si no es multijugador tengo que intentar que no se repitan los enfrentamientos usando la lista de oponentes
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
                        // Esto es para contemplar el caso de que no exista ningun participante sin enfrentamiento al que no te hayas enfrentado.
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

    @Override
    public int hashCode() {
        return Objects.hash(getLista_participantes(), getN_rondas(), getJugadores_iniciales(), getRonda_actual(), es_multijugador, getPartidas_set(), getJugadores_partida());
    }
}
