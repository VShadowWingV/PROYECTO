package model;

import java.util.Comparator;

public class Comparadores {

    // Puntuación -> Win/Loss RT -> Win/Loss RT OP -> Menor Byes
    public static class ComparadorClasificacion implements Comparator<Participante_Tipo_SW> {
        private static final int PESO_PUNTUACION = 4;
        private static final int PESO_BYES = 3;
        private static final int PESO_RATIO_RT = 2;
        private static final int PESO_RATIO_OP = 1;

        @Override
        public int compare(Participante_Tipo_SW p1, Participante_Tipo_SW p2) {
            // Comparaciones
            int comparacionPuntuacion = Integer.compare(p2.getPuntuacion(), p1.getPuntuacion());
            int comparacionByes = Integer.compare(p1.getN_byes(), p2.getN_byes());
            int comparacionRatioRT = Float.compare(p2.getRatioVictorias(), p1.getRatioVictorias());
            int comparacionRatioOp = Float.compare(p2.getRatioVictoriasMedioOp(), p1.getRatioVictoriasMedioOp());

            // Comparar por menor número de Byes
            return PESO_PUNTUACION*comparacionPuntuacion + PESO_BYES*comparacionByes + comparacionRatioRT*PESO_RATIO_RT + comparacionRatioOp*PESO_RATIO_OP;
        }
    }

    public static class ComparadorByeInverso implements Comparator<Participante_Tipo_SW> {
        private static final int PESO_PUNTUACION = 4;
        private static final int PESO_BYES = -3;
        private static final int PESO_RATIO_RT = 2;
        private static final int PESO_RATIO_OP = 1;

        @Override
        public int compare(Participante_Tipo_SW p1, Participante_Tipo_SW p2) {
            // Comparaciones
            int comparacionPuntuacion = Integer.compare(p2.getPuntuacion(), p1.getPuntuacion());
            int comparacionByes = Integer.compare(p1.getN_byes(), p2.getN_byes());
            int comparacionRatioRT = Float.compare(p2.getRatioVictorias(), p1.getRatioVictorias());
            int comparacionRatioOp = Float.compare(p2.getRatioVictoriasMedioOp(), p1.getRatioVictoriasMedioOp());

            // Comparar por menor número de Byes
            return PESO_PUNTUACION*comparacionPuntuacion + PESO_BYES*comparacionByes + comparacionRatioRT*PESO_RATIO_RT + comparacionRatioOp*PESO_RATIO_OP;
        }
    }
}
