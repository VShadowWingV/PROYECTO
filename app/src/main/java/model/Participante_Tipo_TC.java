package model;

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


}
