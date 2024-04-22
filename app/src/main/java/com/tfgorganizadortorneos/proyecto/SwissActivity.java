package com.tfgorganizadortorneos.proyecto;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.tfgorganizadortorneos.proyecto.fragments.BFragmentInRes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import model.Comparadores;
import model.Gestor_SW;
import model.MiStructParticipante;
import model.Participante_Tipo_SW;

public class SwissActivity extends AppCompatActivity {

    // Elementos vista
    Button bt_izda;
    Button bt_dcha;
    Button bt_sig_r;
    TableLayout tb_posiciones;
    TextView tv_ronda_actual;

    // Elementos modelo
    Gestor_SW gestor_sw = Gestor_SW.getInstance();
    int enfrentamiento_mostrar = 1;
    MiStructParticipante  [] structDatosGuardar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_swiss);

        // Obtener elementos VIEW
        bt_izda = findViewById(R.id.bt_enf_izda);
        bt_dcha = findViewById(R.id.bt_enf_dcha);
        bt_sig_r = findViewById(R.id.bt_sig_r);
        tb_posiciones = findViewById(R.id.tb_posiciones);
        tv_ronda_actual = findViewById(R.id.tv_ronda_actual);

        // Creacion del contenedor para el fragment en tiempo de ejecución
        FragmentContainerView fragmentContainerView = new FragmentContainerView(getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                devolverDP(325));
        fragmentContainerView.setLayoutParams(layoutParams);
        fragmentContainerView.setId(View.generateViewId());

        LinearLayout mainLayout = findViewById(R.id.main);
        mainLayout.addView(fragmentContainerView);

        // Inicializar STRUCTS y ronda inicial
        inicializarStructDatosGuardar();
        tv_ronda_actual.setGravity(Gravity.CENTER);
        tv_ronda_actual.setText("RONDA: " + gestor_sw.getRonda_actual() + "/" + gestor_sw.getN_rondas());

        // Cargar tabla inicial
        gestor_sw.getLista_participantes().sort(new Comparadores.ComparadorClasificacion());
        cargarElementosTabla(gestor_sw.getLista_participantes());

        // Cargar el 1er enfrentamiento
        Collections.shuffle(gestor_sw.getLista_participantes());
        cargarFragmentMesa(gestor_sw.devolverSublistaEnfrentamientoI(enfrentamiento_mostrar), fragmentContainerView, structDatosGuardar);

        // Botones para moverse por los enfrentamientos
        bt_dcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int suma = 0;
                if (gestor_sw.getJugadores_iniciales()%gestor_sw.getJugadores_partida() == 1) {suma = 1;}
                if (enfrentamiento_mostrar >= (gestor_sw.getJugadores_iniciales()/gestor_sw.getJugadores_partida() + suma)) {
                    enfrentamiento_mostrar = 1;
                } else {
                    enfrentamiento_mostrar++;
                }
                cargarFragmentMesa(gestor_sw.devolverSublistaEnfrentamientoI(enfrentamiento_mostrar), fragmentContainerView, structDatosGuardar);
            }
        });
        bt_izda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int suma = 0;
                if (gestor_sw.getJugadores_iniciales()%gestor_sw.getJugadores_partida() == 1) {suma = 1;}
                if (enfrentamiento_mostrar <= 1) {
                    enfrentamiento_mostrar = (gestor_sw.getJugadores_iniciales()/gestor_sw.getJugadores_partida() + suma);
                } else {
                    enfrentamiento_mostrar--;
                }
                cargarFragmentMesa(gestor_sw.devolverSublistaEnfrentamientoI(enfrentamiento_mostrar), fragmentContainerView, structDatosGuardar);
            }
        });

        // Botón para avanzar ronda o finalizar torneo
        bt_sig_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Comprobamos si se han registrado todos los datos para los enfrentamientos desde el fragment
                boolean todosGuardados = true;
                for (int i = 0; i < structDatosGuardar.length; i++) {
                    todosGuardados = structDatosGuardar[i].esGuardado;
                }

                if (todosGuardados && (gestor_sw.getRonda_actual() <= gestor_sw.getN_rondas())) {
                    Toast.makeText(getApplicationContext(), "Avanzando ronda...", Toast.LENGTH_SHORT).show();
                    gestor_sw.incRondaActual();

                    transferirDatosEnfrentamientosGestor();

                    if (gestor_sw.getRonda_actual() > gestor_sw.getN_rondas()) {
                        // FINAL DEL TORNEO
                        Toast.makeText(getApplicationContext(), "Fin del torneo, generando resultados... ", Toast.LENGTH_SHORT).show();
                        gestor_sw.getLista_participantes().sort(new Comparadores.ComparadorClasificacion());
                        cargarElementosTabla(gestor_sw.getLista_participantes());

                        try {
                            generarArchivoJSONTorneoSuizo("TorneoSW");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    } else if(gestor_sw.getRonda_actual() <= gestor_sw.getN_rondas()) {
                        // SIGUIENTE RONDA
                        tv_ronda_actual.setText("RONDA: " + gestor_sw.getRonda_actual() + "/" + gestor_sw.getN_rondas());
                        gestor_sw.getLista_participantes().sort(new Comparadores.ComparadorClasificacion());
                        cargarElementosTabla(gestor_sw.getLista_participantes());
                        gestor_sw.getLista_participantes().sort(new Comparadores.ComparadorByeInverso());

                        // REINCIAMOS VECTOR PARA GUARDAR NUEVOS DATOS Y CARGAMOS PRIMER ENFRENTAMIENTO
                        inicializarStructDatosGuardar();
                        enfrentamiento_mostrar = 1;
                        cargarFragmentMesa(gestor_sw.devolverSublistaEnfrentamientoI(enfrentamiento_mostrar), fragmentContainerView, structDatosGuardar);
                    }
                } else {
                    if (gestor_sw.getRonda_actual() > gestor_sw.getN_rondas()) {
                        Toast.makeText(getApplicationContext(), "El torneo ya ha finalizado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Faltan enfrentamientos de la ronda " + gestor_sw.getRonda_actual(), Toast.LENGTH_SHORT).show();
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
     * Convierte píxeles en densidad de píxeles (dp) para adaptarse a diferentes densidades de pantalla.
     *
     * @param px Píxeles a convertir.
     * @return Píxeles convertidos en dp.
     */
    public int devolverDP(int px) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }

    /**
     * Carga el fragmento que muestra la mesa de enfrentamientos.
     *
     * @param listaEnfrentamiento   Lista de participantes en el enfrentamiento.
     * @param fragmentContainerView Contenedor del fragmento.
     * @param structInfo            Información de la estructura de datos.
     */
    public void cargarFragmentMesa(ArrayList<Participante_Tipo_SW> listaEnfrentamiento, FragmentContainerView fragmentContainerView, MiStructParticipante [] structInfo){
        BFragmentInRes fragment = new BFragmentInRes(listaEnfrentamiento, structInfo);

        FragmentManager fragmentManager = getSupportFragmentManager();
        // TRANSACCION INICIAL FRAGMENT
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(fragmentContainerView.getId(), fragment);
        // COMMIT
        transaction.commit();
    }

    /**
     * Carga los elementos de la tabla de posiciones.
     *
     * @param listaParticipantesOrden Lista de participantes ordenados.
     */
    public void cargarElementosTabla(ArrayList<Participante_Tipo_SW> listaParticipantesOrden){
        tb_posiciones.removeAllViews();

        for (int i = 0; i < listaParticipantesOrden.size(); i++) {

            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams c_params = new TableRow.LayoutParams(
                    0,
                    1000/4,
                    1
            );

            // CREAR CADA FILA DE JUGADOR
            TextView tvNombre = new TextView(this);
            tvNombre.setText(listaParticipantesOrden.get(i).getNombre());
            tvNombre.setLayoutParams(c_params);
            tvNombre.setGravity(Gravity.CENTER);

            TextView tvAlias = new TextView(this);
            tvAlias.setText(listaParticipantesOrden.get(i).getAlias());
            tvAlias.setLayoutParams(c_params);
            tvAlias.setGravity(Gravity.CENTER);

            TextView tvPunct = new TextView(this);
            tvPunct.setText(String.valueOf(listaParticipantesOrden.get(i).getPuntuacion()));
            tvPunct.setLayoutParams(c_params);
            tvPunct.setGravity(Gravity.CENTER);

            TextView tvWinLoss = new TextView(this);
            tvWinLoss.setText(new StringBuilder().append(listaParticipantesOrden.get(i).getVictorias_totales())
                    .append(" / ")
                    .append(listaParticipantesOrden.get(i).getDerrotas_totales()));
            tvWinLoss.setLayoutParams(c_params);
            tvWinLoss.setGravity(Gravity.CENTER);

            TextView nByes = new TextView(this);
            nByes.setText(String.valueOf(listaParticipantesOrden.get(i).getN_byes()));
            nByes.setLayoutParams(c_params);
            nByes.setGravity(Gravity.CENTER);

            tableRow.addView(tvNombre);
            tableRow.addView(tvAlias);
            tableRow.addView(tvPunct);
            tableRow.addView(tvWinLoss);
            tableRow.addView(nByes);

            tb_posiciones.addView(tableRow);
        }
    }

    /**
     * Inicializa la estructura de datos para guardar información.
     */
    public void inicializarStructDatosGuardar() {
        structDatosGuardar = new MiStructParticipante[gestor_sw.getJugadores_iniciales()];
        for (int i = 0; i < structDatosGuardar.length; i++) {
            structDatosGuardar[i] = new MiStructParticipante();
            structDatosGuardar[i].id = gestor_sw.getLista_participantes().get(i).getId();
            structDatosGuardar[i].posicion = -1;
            structDatosGuardar[i].victorias = -1;
            structDatosGuardar[i].derrotas = -1;
            structDatosGuardar[i].idOponentes = new int[gestor_sw.getJugadores_partida() - 1];
            Arrays.fill(structDatosGuardar[i].idOponentes, -1);
            structDatosGuardar[i].esBye = false;
            structDatosGuardar[i].esGuardado = false;
        }
    }

    /**
     * Genera datos de prueba para un torneo suizo.
     */
    public void generarDatosPruebaSuizo() {
        gestor_sw.aniadirParticipanteSW(new Participante_Tipo_SW(0,"Sergio","El lloros"));
        gestor_sw.aniadirParticipanteSW(new Participante_Tipo_SW(1,"Pepe","El demonio"));
        gestor_sw.aniadirParticipanteSW(new Participante_Tipo_SW(2,"Juan","La rata"));
        gestor_sw.aniadirParticipanteSW(new Participante_Tipo_SW(3,"Pedro","El F2P"));
        gestor_sw.aniadirParticipanteSW(new Participante_Tipo_SW(4,"Guille","El sobres"));
        gestor_sw.aniadirParticipanteSW(new Participante_Tipo_SW(5,"Alvaro","El quejas"));
        gestor_sw.aniadirParticipanteSW(new Participante_Tipo_SW(6,"Dani","El de los counters"));
        gestor_sw.aniadirParticipanteSW(new Participante_Tipo_SW(7,"Reven","El Economista"));
        gestor_sw.setJugadores_iniciales(gestor_sw.getLista_participantes().size());

        gestor_sw.setPartidas_set(3);
        gestor_sw.setEs_multijugador(false);
        gestor_sw.setJugadores_partida(2);
        gestor_sw.setN_rondas(4);
        gestor_sw.setRonda_actual(4);
    }

    /**
     * Genera un archivo JSON para el torneo suizo.
     *
     * @param nombreDirectorio Nombre del directorio donde se guardará el archivo.
     */
    public void generarArchivoJSONTorneoSuizo(String nombreDirectorio) throws JSONException {
        // JSON:
        JSONArray jsonArray = new JSONArray();
        // Elementos del torneo:
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("n_rondas", gestor_sw.getN_rondas());
        jsonObject.put("jugadores_iniciales", gestor_sw.getJugadores_iniciales());
        jsonObject.put("ronda_actual", gestor_sw.getRonda_actual());
        jsonObject.put("es_multijugador", gestor_sw.es_multijugador());
        jsonObject.put("partidas_set", gestor_sw.getPartidas_set());
        jsonObject.put("jugadores_partida", gestor_sw.getJugadores_partida());
        jsonArray.put(jsonObject);

        // Lista de participantes
        for (Participante_Tipo_SW participante : gestor_sw.getLista_participantes()) {
            JSONObject participanteJson = participante.toJson();
            jsonArray.put(participanteJson);
        }

        String json_res = jsonArray.toString();

        String fileName = "torneo_sw_" + Math.abs(gestor_sw.hashCode()) + ".json";
        File directory = new File(getApplicationContext().getExternalFilesDir(null), nombreDirectorio);

        // E/S Ficheros
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Toast.makeText(getApplicationContext(), "Creando el directorio: " + directory.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error al crear el directorio: " + directory.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Existe el directorio: " + directory.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }

        File file = new File(directory, fileName);
        try {
            // Escribir en el archivo JSON
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json_res);
            fileWriter.flush();
            fileWriter.close();

            Toast.makeText(getApplicationContext(), "Archivo JSON creado correctamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error al crear el archivo JSON", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Transfiere los datos de los enfrentamientos al gestor.
     */
    public void transferirDatosEnfrentamientosGestor() {
        for (int i = 0; i < structDatosGuardar.length; i++) {
            Participante_Tipo_SW participante_tipo_sw = gestor_sw.getParticpanteListaID(structDatosGuardar[i].id);
            participante_tipo_sw.incVictorias(structDatosGuardar[i].victorias);
            participante_tipo_sw.incDerrotas(structDatosGuardar[i].derrotas);

            // PUNTOS
            if (gestor_sw.es_multijugador()) {
                participante_tipo_sw.incPunct(gestor_sw.getJugadores_partida() - structDatosGuardar[i].posicion + 1);
            } else {
                switch (structDatosGuardar[i].posicion) {
                    case 1:
                        participante_tipo_sw.incPunct(3);
                        break;
                    case 2:
                        participante_tipo_sw.incPunct(0);
                        break;
                    case 0:
                        participante_tipo_sw.incPunct(1);
                        break;
                    default:
                        // participante_tipo_sw.incPunct(0);
                }
            }

            // OPONENTES
            for (int j = 0; j < structDatosGuardar[i].idOponentes.length; j++) {
                participante_tipo_sw.addOponente(gestor_sw.getParticpanteListaID(structDatosGuardar[i].idOponentes[j]));
            }

            // BYES
            if (structDatosGuardar[i].esBye) {
                participante_tipo_sw.incN_byes();
            }
        }
    }
}