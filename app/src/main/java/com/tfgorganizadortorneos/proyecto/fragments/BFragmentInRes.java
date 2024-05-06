package com.tfgorganizadortorneos.proyecto.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.tfgorganizadortorneos.proyecto.R;

import java.util.ArrayList;

import model.Gestor_SW;
import model.MiStructParticipante;
import model.Participante_Tipo_SW;

public class BFragmentInRes extends Fragment {

    // Elementos modelo
    ArrayList<Participante_Tipo_SW> listaEnfrentamiento;
    Gestor_SW gestor_sw = Gestor_SW.getInstance();
    private ArrayList<TextView> textViewList = new ArrayList<>();
    private ArrayList<EditText> editTextList = new ArrayList<>();
    MiStructParticipante[] structDatosGuardar;

    /**
     * Constructor del fragmento.
     * @param listaEnfrentamiento La lista de participantes en el enfrentamiento.
     * @param structDatosGuardar El arreglo de estructuras para guardar los datos.
     */
    public BFragmentInRes(ArrayList<Participante_Tipo_SW> listaEnfrentamiento, MiStructParticipante[] structDatosGuardar) {
        if (listaEnfrentamiento != null && structDatosGuardar != null) {
            this.listaEnfrentamiento = listaEnfrentamiento;
            this.structDatosGuardar = structDatosGuardar;
        } else {
            this.listaEnfrentamiento = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.b_fragment_result, container, false);

        // Obtener elementos VIEW
        LinearLayout containerLayout = view.findViewById(R.id.l_container);

        // Para agrupar los participantes por mesa de 2 en 2 y que se pueda ver mejor
        crearAgrupacionEnfrentamientosSublista(containerLayout);

        // Añadimos boton para guardar los resultados de este enfrentamineto
        Button button = new Button(getContext());
        button.setText("Guardar");
        containerLayout.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comprobarEditText()) {
                    for (int i = 0; i < structDatosGuardar.length; i++) {
                        for (Participante_Tipo_SW part: listaEnfrentamiento) {
                            guardarParticipanteInformacion(part, i);
                        }
                    }
                    Toast.makeText(getContext(), "CORRECTOS", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "INCORRECTOS", Toast.LENGTH_SHORT).show();
                }

                //Comprobar datos
                logStruct();
            }
        });

        return view;
    }

    /**
     * Método para comprobar que los EditText contienen datos válidos.
     * @return True si los datos son válidos, False si no lo son.
     */
    public boolean comprobarEditText() {
        boolean correctos = true;
        if (gestor_sw.es_multijugador()) {
            for (int i = 0; i < editTextList.size(); i++) {
                try {
                    int valor = Integer.parseInt(editTextList.get(i).getText().toString());
                    if (valor < 1 || valor > gestor_sw.getJugadores_partida()) {
                        correctos = false;
                    }
                } catch (NumberFormatException e) {
                    correctos = false;
                }
            }
        } else if(listaEnfrentamiento.size() == 2){
            try {
                int valor1 = Integer.parseInt(editTextList.get(0).getText().toString());
                int valor2 = Integer.parseInt(editTextList.get(1).getText().toString());

                if (valor1 > (gestor_sw.getPartidas_set()/2 + 1) ||
                        valor2 > (gestor_sw.getPartidas_set()/2 + 1) ||
                        valor1 < 0 || valor2 < 0) {
                    correctos = false;
                } else {
                    if (valor1 == (gestor_sw.getPartidas_set()/2 + 1) && valor2 == (gestor_sw.getPartidas_set()/2 + 1)) {
                        correctos = false;
                    }
                }
            } catch (NumberFormatException e) {
                correctos = false;
            }
        }
        return correctos;
    }

    /**
     * Método para imprimir información de la estructura de datos en el log.
     */
    public void logStruct() {
        if (structDatosGuardar != null) {
            for (int i = 0; i < structDatosGuardar.length; i++) {
                Log.i("STRUCT",structDatosGuardar[i].id + " - " + structDatosGuardar[i].esGuardado +
                        " -> V/D:" + structDatosGuardar[i].victorias + "/" + structDatosGuardar[i].derrotas + " -- " +
                        "P:" + structDatosGuardar[i].posicion + " -- " +
                        "B:" + structDatosGuardar[i].esBye);
                StringBuilder sb = new StringBuilder("O: ");
                for (int j = 0; j < structDatosGuardar[i].idOponentes.length; j++) {
                    sb.append("[" + String.valueOf(structDatosGuardar[i].idOponentes[j]) + "]");
                }
                Log.i("STRUCT", sb.toString());
            }
        }
    }

    /**
     * Método para crear la disposición de los enfrentamientos en la interfaz de usuario.
     * @param containerLayout El layout contenedor donde se agregarán los elementos.
     */
    public void crearAgrupacionEnfrentamientosSublista(LinearLayout containerLayout) {
        for (int i = 0; i < listaEnfrentamiento.size(); i+=2) {
            LinearLayout verticalLayoutAgrupacion = new LinearLayout(getContext());
            verticalLayoutAgrupacion.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    1
            ));
            verticalLayoutAgrupacion.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < 2; j++) {
                if (i + j < listaEnfrentamiento.size()) {
                    LinearLayout horizontalLayoutParticipante = new LinearLayout(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            1
                    );
                    horizontalLayoutParticipante.setLayoutParams(layoutParams);
                    verticalLayoutAgrupacion.addView(horizontalLayoutParticipante);

                    // Para cada participante creamos dos TV para su nombre y su resultado.
                    TextView textViewPr = new TextView(getContext());
                    EditText editTextRes = new EditText(getContext());

                    textViewPr.setText(listaEnfrentamiento.get(i+j).getAlias());
                    textViewPr.setGravity(Gravity.CENTER);

                    editTextRes.setHint(" RES/POS ");
                    editTextRes.setInputType(InputType.TYPE_CLASS_NUMBER);

                    textViewPr.setLayoutParams(layoutParams);
                    editTextRes.setLayoutParams(layoutParams);

                    textViewList.add(textViewPr);
                    editTextList.add(editTextRes);
                    horizontalLayoutParticipante.addView(textViewPr);
                    horizontalLayoutParticipante.addView(editTextRes);
                }
            }

            containerLayout.addView(verticalLayoutAgrupacion);
        }
    }

    /**
     * Método para registrar información en la estructura de datos.
     * @param part El participante del enfrentamiento.
     * @param pos La posición en la estructura de datos.
     */
    public void guardarParticipanteInformacion(Participante_Tipo_SW part, int pos) {
        if (part.getId() == structDatosGuardar[pos].id) {
            // Se han actualizado o guardado los datos de los jugadores del enfrentamiento
            structDatosGuardar[pos].esGuardado = true;

            // Localizamos el EditText que corresponde a tu alias
            EditText etJugadorI = null;
            for (int j = 0; j < editTextList.size(); j++) {
                if (textViewList.get(j).getText().toString().equals(part.getAlias())) {
                    etJugadorI = editTextList.get(j);
                }
            }

            // Hacemos correlación de datos con el EditText
            if (etJugadorI != null) {
                if (gestor_sw.es_multijugador()){
                    // Si estamos en multi -> Tu posicion es lo que el usuario ha introducido en el ET correspondiente
                    structDatosGuardar[pos].posicion = Integer.parseInt(etJugadorI.getText().toString());
                    // Si estamos en multi -> Es victoria si la posicion introducida es < la mitad de los jugadores por partida, en caso contrario es derrota
                    if(Integer.parseInt(etJugadorI.getText().toString()) <= (gestor_sw.getJugadores_partida()/2) ) {
                        structDatosGuardar[pos].victorias = 1;
                        structDatosGuardar[pos].derrotas = 0;
                    } else {
                        structDatosGuardar[pos].victorias = 0;
                        structDatosGuardar[pos].derrotas = 1;
                    }
                } else {
                    // Si no estamos en multi necesitamos el editText del oponente
                    EditText etJugadorOtro = null;
                    for (int j = 0; j < editTextList.size(); j++) {
                        if (!textViewList.get(j).getText().toString().equals(part.getAlias())) {
                            etJugadorOtro = editTextList.get(j);
                        }
                    }
                    // Si el editText es mayor que el del oponente es posicion = 1, si es menor posicion = 2, si son iguales es posicion = 0
                    // Independientemente hay que añadir a las victorias tu edit text y a tus derrotas el del oponente
                    if (etJugadorOtro != null) {
                        if (Integer.parseInt(etJugadorI.getText().toString()) > Integer.parseInt(etJugadorOtro.getText().toString())) {
                            structDatosGuardar[pos].posicion = 1;
                        } else if(Integer.parseInt(etJugadorI.getText().toString()) < Integer.parseInt(etJugadorOtro.getText().toString())) {
                            structDatosGuardar[pos].posicion = 2;
                        } else if(Integer.parseInt(etJugadorI.getText().toString()) == Integer.parseInt(etJugadorOtro.getText().toString())) {
                            structDatosGuardar[pos].posicion = 0;
                        }

                        structDatosGuardar[pos].victorias = Integer.parseInt(etJugadorI.getText().toString());
                        structDatosGuardar[pos].derrotas = Integer.parseInt(etJugadorOtro.getText().toString());
                    }
                }
            }

            // Por las condiciones del torneo solo existe BYE si el enfrentamiento es de un unico jugador
            // En este caso al darle a guardar se pondran victorias igual a la mitad del BOX + 1, 0 derrotas, y un BYE
            // HAY QUE GUARDAR ESTE RESULTADO IGUAL CON EL BOTON
            if (listaEnfrentamiento.size() == 1) {
                structDatosGuardar[pos].posicion = 1;
                structDatosGuardar[pos].victorias = gestor_sw.getPartidas_set()/2 + 1;
                structDatosGuardar[pos].derrotas = 0;
                structDatosGuardar[pos].esBye = true;
            }

            // Para guardar los ids de tus oponentes -> Almacenamos todos los ids del enfrntamiento en la lista menos el propio
            int oponenteAniadido = 0;
            for (int j = 0; j < listaEnfrentamiento.size(); j++) {
                if (!textViewList.get(j).getText().toString().equals(part.getAlias())) {
                    structDatosGuardar[pos].idOponentes[oponenteAniadido] = listaEnfrentamiento.get(j).getId();
                    oponenteAniadido++;
                }
            }
        }
    }
}
