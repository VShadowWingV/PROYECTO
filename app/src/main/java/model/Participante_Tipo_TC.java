package model;

public class Participante_Tipo_TC extends Participante{

    private int pool;
    public Participante_Tipo_TC(int id, String nombre, String alias) {
        super(id, nombre, alias);
    }

    public int getPool() {
        return pool;
    }

    public void setPool(int pool) {
        this.pool = pool;
    }


}
