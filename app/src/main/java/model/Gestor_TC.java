package model;

import java.util.ArrayList;

public class Gestor_TC {
    private ArrayList<Participante_Tipo_TC> lista_participantes = new ArrayList<>();
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
}
