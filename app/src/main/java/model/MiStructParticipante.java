/**
 * MiStructParticipante: Esta clase define una estructura para representar la información de un participante en un torneo.
 */
package model;

public class MiStructParticipante {

    public int id;
    // Este entero también me sirve en caso de no ser multi, 1 -> Victoria, 2 -> Derrota, 0 -> Empate
    public int posicion;
    public int victorias;
    public int derrotas;
    public int  [] idOponentes;
    public boolean esBye;
    public boolean esGuardado;

}
