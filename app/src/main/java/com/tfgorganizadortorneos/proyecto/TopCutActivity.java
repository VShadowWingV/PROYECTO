package com.tfgorganizadortorneos.proyecto;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import model.Gestor_TC;
import model.Participante_Tipo_TC;

public class TopCutActivity extends AppCompatActivity implements View.OnTouchListener {

    // Elementos vista
    Button bt_sig;
    TextView tv_j1;
    TextView tv_j2;
    TextView tv_rondas;
    EditText et_res_j1;
    EditText et_res_j2;
    LinearLayout layoutBracket;
    FrameLayout layoutJugadores;
    ArrayList<TextView> lista_tv_jugadores = new ArrayList<>();

    // Elementos modelo
    Gestor_TC gestor_tc = Gestor_TC.getInstance();
    float previousX, previousY;
    float limitePantallaBracket;
    boolean esFinal = false;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_top_cut);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bt_sig = findViewById(R.id.bt_sig);
        tv_j1 = findViewById(R.id.tv_jug1);
        tv_j2 = findViewById(R.id.tv_jug2);
        tv_rondas = findViewById(R.id.tv_rondas);
        et_res_j1 = findViewById(R.id.et_res_jug1);
        et_res_j2 = findViewById(R.id.et_res_jug2);
        layoutBracket = findViewById(R.id.layoutBracket);
        layoutJugadores = findViewById(R.id.layoutJugadores);

        // Datos con las vistas para hacer cálculos de inicialización
        int[] posicionLayout = new int[2];
        esFinal = false;
        layoutBracket.getLocationOnScreen(posicionLayout);
        limitePantallaBracket = posicionLayout[1] + layoutBracket.getLayoutParams().height;

        // Inicialización de valores con archivo tmp
        String fileNameS = "torneo_tc_tmp.json";
        File directoryS = new File(getApplicationContext().getExternalFilesDir(null), "TorneoTC");
        File temporalTopCut = new File(directoryS, fileNameS);
        try {
            if(temporalTopCut.exists()) {
                gestor_tc.actualizarDesdeJson(temporalTopCut);
            } else {
                gestor_tc.generarArchivoJSONTorneoTopCut("TorneoTC", "tmp", getApplicationContext());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Generar TV participantes y rondas
        tv_rondas.setText("Ronda: "  + String.valueOf(gestor_tc.getRonda_actual()) + "/" + String.valueOf(gestor_tc.getN_rondas()));
        cargarParticipantesTextView(layoutJugadores);

        // Boton
        bt_sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Participante_Tipo_TC j1 = null, j2 = null;
                j1 = devolverJugadorJ1TextView();
                j2 = devolverJugadorJ2TextView();

                if (j1 != null && j2 != null) {
                    if (comprobarEditText()) {
                        // Comprobaciones de datos y actualizaciones, si es lb tengo que comprobar que este eliminado a 2
                        if(gestor_tc.isLoser_bracket()) {
                            if (j1.getEliminado() == 2 || j2.getEliminado() == 2) {
                                Toast.makeText(getApplicationContext(), "Uno de los jugadores ya está eliminado del TOP, inválido", Toast.LENGTH_SHORT).show();
                            } else if(j1.getEliminado() != j2.getEliminado() && !calcularFinalReal()){
                                Toast.makeText(getApplicationContext(), "Los jugadores están en diferentes lados del BRACKET y no es la FINAL, inválido", Toast.LENGTH_SHORT).show();
                            } else if (j1.getPool() == j2.getPool() && gestor_tc.getEnfrentamientos_registrados().size() < gestor_tc.getJugadores_iniciales()/2) {
                                Toast.makeText(getApplicationContext(), "Los jugadores son del mismo POOL y es la ronda 1 de winners, inválido", Toast.LENGTH_SHORT).show();
                            } else {
                                // Datos para procesar en el gestor -> Participantes
                                if (Integer.parseInt(et_res_j1.getText().toString()) < Integer.parseInt(et_res_j2.getText().toString())) {
                                    j1.incEliminado();
                                } else {
                                    j2.incEliminado();
                                }
                                j1.incVictorias(Integer.parseInt(et_res_j1.getText().toString()));
                                j2.incVictorias(Integer.parseInt(et_res_j2.getText().toString()));
                                j1.incDerrotas(Integer.parseInt(et_res_j2.getText().toString()));
                                j2.incDerrotas(Integer.parseInt(et_res_j1.getText().toString()));

                                // Datos para procesar en el gesot -> Enfrentamiento
                                Toast.makeText(getApplicationContext(), j1.getNombre() + " vs " + j2.getNombre(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Añadiendo enfrentamiento nº " + String.valueOf(gestor_tc.getEnfrentamientos_registrados().size() + 1), Toast.LENGTH_SHORT).show();
                                gestor_tc.aniadirEnfrentamientoRegistrado(j1.getNombre(),j2.getNombre(),
                                        et_res_j1.getText().toString() + " - " + et_res_j2.getText().toString());

                                // Si es la final real (nos quedan solo dos participantes sin eliminar) el siguiente enfrentamiento que añadamos
                                // finalizará el torneo
                                if (calcularFinalReal()) {
                                    esFinal = true;
                                } else if (esFinal) {
                                    Toast.makeText(getApplicationContext(),"Finalizando torneo, generando resultados..." , Toast.LENGTH_SHORT).show();
                                    try {
                                        gestor_tc.generarArchivoJSONTorneoTopCut("TorneoTC",String.valueOf(Math.abs(gestor_tc.hashCode())), getApplicationContext());
                                        borrarArchivoJsonTemporal("TorneoTC");
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        } else {
                            if (j1.getEliminado() == 1 || j2.getEliminado() == 1) {
                                Toast.makeText(getApplicationContext(), "Uno de los jugadores ya está eliminado del TOP, inválido", Toast.LENGTH_SHORT).show();
                            } else if (j1.getPool() == j2.getPool() && gestor_tc.getEnfrentamientos_registrados().size() < gestor_tc.getJugadores_iniciales()/2) {
                                Toast.makeText(getApplicationContext(), "Los jugadores son del mismo POOL y es la ronda 1 de winners, inválido", Toast.LENGTH_SHORT).show();
                            } else {
                                // Datos para procesar en el gestor -> Participantes
                                if (Integer.parseInt(et_res_j1.getText().toString()) < Integer.parseInt(et_res_j2.getText().toString())) {
                                    j1.incEliminado();
                                } else {
                                    j2.incEliminado();
                                }
                                j1.incVictorias(Integer.parseInt(et_res_j1.getText().toString()));
                                j2.incVictorias(Integer.parseInt(et_res_j2.getText().toString()));
                                j1.incDerrotas(Integer.parseInt(et_res_j2.getText().toString()));
                                j2.incDerrotas(Integer.parseInt(et_res_j1.getText().toString()));

                                // Datos para procesar en el gesot -> Enfrentamiento
                                Toast.makeText(getApplicationContext(), j1.getNombre() + " vs " + j2.getNombre(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Añadiendo enfrentamiento nº " + String.valueOf(gestor_tc.getEnfrentamientos_registrados().size() + 1), Toast.LENGTH_SHORT).show();
                                gestor_tc.aniadirEnfrentamientoRegistrado(j1.getNombre(),j2.getNombre(),
                                        et_res_j1.getText().toString() + " - " + et_res_j2.getText().toString());

                                // Si es la final real (nos quedan solo dos participantes sin eliminar) el siguiente enfrentamiento que añadamos
                                // finalizará el torneo
                                if (calcularFinalReal()) {
                                    esFinal = true;
                                } else if (esFinal) {
                                    Toast.makeText(getApplicationContext(),"Finalizando torneo, generando resultados..." , Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        if (calcularRondaEnfrentamientos(gestor_tc.getRonda_actual()) == gestor_tc.getEnfrentamientos_registrados().size()) {
                            gestor_tc.incRondaActual();
                            restablecerParticipantesTextView();
                            if (gestor_tc.getRonda_actual() <= gestor_tc.getN_rondas()) {
                                tv_rondas.setText("Ronda: "  + String.valueOf(gestor_tc.getRonda_actual()) + "/" + String.valueOf(gestor_tc.getN_rondas()));
                                try {
                                    borrarArchivoJsonTemporal("TorneoTC");
                                    gestor_tc.generarArchivoJSONTorneoTopCut("TorneoTC", "tmp", getApplicationContext());
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        et_res_j1.setText("");
                        et_res_j2.setText("");
                    } else if (et_res_j1.getText().toString().equals("") && et_res_j2.getText().toString().equals("")) {
                        restablecerParticipantesTextView();
                    } else {
                        Toast.makeText(getApplicationContext(), "Datos incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Método para poder draggear elementos tipo VIEW por la pantalla de la aplicación.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float currentX, currentY;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Guardar las coordenadas iniciales al presionar
                view.bringToFront();
                previousX = motionEvent.getRawX();
                previousY = motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = motionEvent.getRawX();
                currentY = motionEvent.getRawY();
                float dx = currentX - previousX;
                float dy = currentY - previousY;

                float newX = view.getX() + dx;
                float newY = view.getY() + dy;

                newX = Math.max(0, Math.min(newX, screenWidth - view.getWidth()));
                newY = Math.max(0, Math.min(newY, screenHeight - view.getHeight()));

                // Muro invisible
                if (numJugadoresEnBracket() > 2 && view.getY() < limitePantallaBracket) {
                    view.setX(newX);
                    view.setY(limitePantallaBracket + 10);
                } else {
                    view.setX(newX);
                    view.setY(newY);
                    previousX = currentX;
                    previousY = currentY;
                }
                break;
        }
        return true;
    }

    /**
     * Genera datos de prueba para un torneo top cut.
     */
    public void generarDatosPruebaTopCut() {
        gestor_tc.aniadirParticipanteTC(new Participante_Tipo_TC(0, "Juan", "DMN", 1));
        gestor_tc.aniadirParticipanteTC(new Participante_Tipo_TC(1, "Pedro", "MSG", 1));
        gestor_tc.aniadirParticipanteTC(new Participante_Tipo_TC(2, "Sergio", "LLS", 2));
        gestor_tc.aniadirParticipanteTC(new Participante_Tipo_TC(3, "Álvaro", "RAT", 2));
        gestor_tc.aniadirParticipanteTC(new Participante_Tipo_TC(4, "Guille", "NCR", 3));
        gestor_tc.aniadirParticipanteTC(new Participante_Tipo_TC(5, "Dani", "RIZ", 3));
        gestor_tc.aniadirParticipanteTC(new Participante_Tipo_TC(6, "Fulgencio", "NEC", 4));
        gestor_tc.aniadirParticipanteTC(new Participante_Tipo_TC(7, "Mauricio", "PEC", 4));

        gestor_tc.setRonda_actual(1);
        gestor_tc.setN_rondas(3);
        gestor_tc.setPartidas_set(5);
        gestor_tc.setJugadores_iniciales(gestor_tc.getLista_participantes().size());
        gestor_tc.setNum_pools(4);
        gestor_tc.setLoser_bracket(true);

    }

    /**
     * Genera vistas de TextView para cada participante del torneo y los coloca en el diseño especificado.
     * @param containerLayout El FrameLayout donde se colocarán los TextViews de los participantes.
     */
    public void cargarParticipantesTextView(FrameLayout containerLayout) {

        Set<Integer> coloresUsados = new HashSet<>();

        int anchoTv = 1000 / 4;
        int largoTv = 100;
        int margen = 15;

        for (int i = 0; i < gestor_tc.getJugadores_iniciales() / 4; i++) {
            for (int j = 0; j < 4; j++) {
                TextView tv_part = new TextView(getApplicationContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        anchoTv,
                        largoTv
                );
                tv_part.setLayoutParams(layoutParams);
                tv_part.setText(gestor_tc.getLista_participantes().get(i*4 + j).getAlias());
                tv_part.setTextColor(Color.BLACK);
                tv_part.setGravity(Gravity.CENTER);
                tv_part.setBackgroundColor(generarColorAleatorio(coloresUsados));
                tv_part.setTranslationX(j * (anchoTv + margen) + margen);
                tv_part.setTranslationY(i * (largoTv + margen) + margen + limitePantallaBracket);
                tv_part.setOnTouchListener(this);

                containerLayout.addView(tv_part);
                lista_tv_jugadores.add(tv_part);
            }
        }
    }

    /**
     * Actualiza las vistas de los participantes en función del progreso del torneo y los resultados.
     */
    public void restablecerParticipantesTextView() {

        int anchoTv = 1000 / 4;
        int largoTv = 100;
        int margen = 15;

        for (int i = 0; i < gestor_tc.getJugadores_iniciales() / 4; i++) {
            for (int j = 0; j < 4; j++) {
                TextView tv_part = lista_tv_jugadores.get(i*4 + j);
                if (gestor_tc.getLista_participantes().get(i*4 + j).getEliminado() == 2 && gestor_tc.isLoser_bracket()) {
                    // Rojo y Blanco si esta eliminado del torneo
                    tv_part.setTextColor(Color.WHITE);
                    tv_part.setBackgroundColor(Color.RED);
                } else if (gestor_tc.getLista_participantes().get(i*4 + j).getEliminado() == 1 && !gestor_tc.isLoser_bracket()) {
                    // Rojo y Blanco si esta eliminado del torneo
                    tv_part.setTextColor(Color.WHITE);
                    tv_part.setBackgroundColor(Color.RED);
                } else if(gestor_tc.getLista_participantes().get(i*4 + j).getEliminado() == 1 && gestor_tc.isLoser_bracket()) {
                    // Verde y Blanco para loser bracket
                    tv_part.setTextColor(Color.WHITE);
                    tv_part.setBackgroundColor(Color.GREEN);
                }
                tv_part.setTranslationX(j * (anchoTv + margen) + margen);
                tv_part.setTranslationY(i * (largoTv + margen) + margen + limitePantallaBracket);
            }
        }
    }

    /**
     * Cuenta el número de participantes que están actualmente en el bracket.
     * @return El número de participantes en el bracket.
     */
    public int numJugadoresEnBracket() {
        int num_jugadores = 0;
        int[] loc = new int[2];

        for (TextView tv : lista_tv_jugadores) {
            tv.getLocationOnScreen(loc);
            if (loc[1] < limitePantallaBracket + 100) {
                num_jugadores++;
            }
        }

        return num_jugadores;
    }

    /**
     * Elimina un archivo JSON temporal utilizado para almacenar datos del torneo.
     * @param nombreDirectorio El nombre del directorio donde se encuentra el archivo temporal.
     */
    public void borrarArchivoJsonTemporal(String nombreDirectorio) {
        String fileName = "torneo_tc_tmp.json";
        File directory = new File(getApplicationContext().getExternalFilesDir(null), nombreDirectorio);
        File file = null;
        if (directory.exists()) {
            file = new File(directory, fileName);
        }

        if (file != null) {
            if(file.delete()){
                Log.i("TMP","Archivo borrado");
            }
        }
    }

    /**
     * Devuelve el participante que está representado por la vista de TextView tv_j1.
     * @return El participante representado por tv_j1.
     */
    public Participante_Tipo_TC devolverJugadorJ1TextView() {

        Participante_Tipo_TC j1 = null;
        int[] posicionJ1 = new int[2];
        tv_j1.getLocationOnScreen(posicionJ1);

        for (TextView tv : lista_tv_jugadores) {
            int[] posTv = new int[2];
            tv.getLocationOnScreen(posTv);

            boolean estaEnJ1 = posTv[0] >= posicionJ1[0] &&
                    posTv[1] >= posicionJ1[1] &&
                    (posTv[0] + tv.getWidth()) <= (posicionJ1[0] + tv_j1.getWidth()) &&
                    (posTv[1] + tv.getHeight()) <= (posicionJ1[1] + tv_j1.getHeight());

            if (estaEnJ1) {
                j1 = gestor_tc.getParticpanteListaAlias(tv.getText().toString());
            }
        }

        return j1;
    }

    /**
     * Devuelve el participante que está representado por la vista de TextView tv_j2.
     * @return El participante representado por tv_j2.
     */
    public Participante_Tipo_TC devolverJugadorJ2TextView() {

        Participante_Tipo_TC j2 = null;
        int[] posicionJ2 = new int[2];
        tv_j2.getLocationOnScreen(posicionJ2);

        for (TextView tv : lista_tv_jugadores) {
            int[] posTv = new int[2];
            tv.getLocationOnScreen(posTv);

            boolean estaEnJ2 = posTv[0] >= posicionJ2[0] &&
                    posTv[1] >= posicionJ2[1] &&
                    (posTv[0] + tv.getWidth()) <= (posicionJ2[0] + tv_j2.getWidth()) &&
                    (posTv[1] + tv.getHeight()) <= (posicionJ2[1] + tv_j2.getHeight());

            if (estaEnJ2) {
                j2 = gestor_tc.getParticpanteListaAlias(tv.getText().toString());
            }
        }

        return j2;
    }

    /**
     * Valida si los datos ingresados en los EditTexts para los resultados de los jugadores son correctos.
     * @return True si los datos son válidos, False si no lo son.
     */
    public boolean comprobarEditText() {
        boolean correctos = true;
        try {
            int res1 = Integer.parseInt(et_res_j1.getText().toString());
            int res2 = Integer.parseInt(et_res_j2.getText().toString());

            correctos =  (res1 < res2 || res1 > res2)
                    && (res1 <= (gestor_tc.getPartidas_set()/2 + 1) && res2 <= (gestor_tc.getPartidas_set()/2 + 1))
                    && (res1 == (gestor_tc.getPartidas_set()/2 + 1) || res2 == gestor_tc.getPartidas_set()/2 + 1);

        } catch (NumberFormatException e) {
            correctos = false;
        }
        return correctos;
    }

    /**
     * Calcula el número total de enfrentamientos para una ronda específica del torneo.
     * @param ronda El número de la ronda para la que se desea calcular el número de enfrentamientos.
     * @return El número total de enfrentamientos para la ronda especificada.
     */
    public int calcularRondaEnfrentamientos(int ronda) {
        int enfrentamientosR;
        // Siguiendo la siguiente progresion de enfrentamientos: jug/2 -> jug/4 -> jug/8 ...  Se que tengo que registrar 4 -> 6 -> 7
        // enfrentamientos para ir avanzando las rondas. De esa forma solo necesito el numero de enfrentamientos guardados para saber
        // en que ronda estamos y avanzar en consecuencia. En lb es igual pero con la secuencia 6 -> 9 -> 10 o lo que es lo mismo, sumar
        // tambien el anterior numero de la sucesion.
        if (gestor_tc.isLoser_bracket()) {
            enfrentamientosR = gestor_tc.getJugadores_iniciales()/2 + gestor_tc.getJugadores_iniciales()/4;
        } else {
            enfrentamientosR = gestor_tc.getJugadores_iniciales()/2;
        }
        for (int i = 2; i <= ronda; i++) {
            if (gestor_tc.isLoser_bracket()) {
                enfrentamientosR = enfrentamientosR + (int) (gestor_tc.getJugadores_iniciales()/Math.pow(2,i)) + (int) (gestor_tc.getJugadores_iniciales()/Math.pow(2,i+1));
            } else {
                enfrentamientosR += (int) (gestor_tc.getJugadores_iniciales()/Math.pow(2,i));
            }
        }
        return enfrentamientosR;
    }

    /**
     * Determina si el torneo ha alcanzado la fase de la final real, donde solo quedan dos participantes.
     * @return True si el torneo ha alcanzado la final real, False en caso contrario.
     */
    public boolean calcularFinalReal() {
        int elminados = 0;
        boolean es_final = true;
        for (Participante_Tipo_TC part: gestor_tc.getLista_participantes()) {
           if (gestor_tc.isLoser_bracket()) {
               if (part.getEliminado() == 2) {
                   elminados++;
               }
           } else {
               if (part.getEliminado() == 1) {
                   elminados++;
               }
           }
        }

        es_final = elminados == gestor_tc.getJugadores_iniciales() - 2;
        return es_final;
    }

    /**
     * Genera un color aleatorio que no ha sido utilizado previamente.
     * @param coloresUsados Un conjunto de colores que ya han sido utilizados.
     * @return Un color RGB aleatorio que no está presente en el conjunto de colores utilizados previamente.
     */
    public static int generarColorAleatorio(Set<Integer> coloresUsados) {
        int color;
        Random RANDOM = new Random();
        coloresUsados.add(Color.argb(255, 0, 0, 255));
        coloresUsados.add(Color.argb(255, 255, 0, 0));
        // ACCESIBILIDAD
        int minRGBValue = 150;
        coloresUsados.add(Color.BLACK);
        do {
            int red = minRGBValue + RANDOM.nextInt(256 - minRGBValue);
            int green = minRGBValue + RANDOM.nextInt(256 - minRGBValue);
            int blue = minRGBValue + RANDOM.nextInt(256 - minRGBValue);
            color = Color.argb(255, red, green, blue);
        } while (coloresUsados.contains(color));
        coloresUsados.add(color);
        return color;
    }

}