package com.tfgorganizadortorneos.proyecto.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.tfgorganizadortorneos.proyecto.PlayerActivity;
import com.tfgorganizadortorneos.proyecto.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Gestor_SW;
import model.Gestor_TC;

public class BFragmentFormatSel extends Fragment implements AdapterView.OnItemSelectedListener{

    // Elementos vista
    Spinner sp_formato;
    Button bt_ini;
    EditText et_participantes;
    EditText et_rondas;
    EditText et_partidas_set;
    CheckBox cb_opc;

    // Elementos modelo
    // Variable pseudoglobal
    static String FORMATO_SELECCIONADO = null;
    boolean avanzar = true;
    Gestor_SW gestor_sw = Gestor_SW.getInstance();
    Gestor_TC gestor_tc = Gestor_TC.getInstance();
    List<String> lista_formatos = new ArrayList<>(Arrays.asList("Swiss", "Top Cut"));
    int num_participantes = -1;
    int num_rondas = -1;
    int num_ps_jpe = -1;

    public BFragmentFormatSel() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.b_fragment_format, container, false);

        // Obtener elementos VIEW
        sp_formato = view.findViewById(R.id.sp_formato);
        bt_ini = view.findViewById(R.id.bt_ini);
        et_participantes = view.findViewById(R.id.num_participantes);
        et_rondas = view.findViewById(R.id.num_rondas);
        et_partidas_set = view.findViewById(R.id.num_partidos_set);
        cb_opc = view.findViewById(R.id.cb_opc);

        // Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, lista_formatos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_formato.setAdapter(adapter);
        sp_formato.setOnItemSelectedListener(this);

        // EditText participantes
        et_participantes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    num_participantes = Integer.parseInt(et_participantes.getText().toString());
                    int rondas = (int) (Math.log(num_participantes) / Math.log(2));
                    if (FORMATO_SELECCIONADO.equals("Top Cut") && cb_opc.isChecked()){
                        et_rondas.setText(String.valueOf(rondas*2));
                    } else if(FORMATO_SELECCIONADO.equals("Top Cut") && !cb_opc.isChecked()){
                        et_rondas.setText(String.valueOf(rondas));
                    }
                } catch (NumberFormatException e) {
                    Log.e("ERROR","Error al convertir la cadena en un número entero: " + e.getMessage());
                }
            }
        });

        // CheckBox
        cb_opc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    num_participantes = Integer.parseInt(et_participantes.getText().toString());
                    int rondas = (int) (Math.log(num_participantes) / Math.log(2));
                    if (FORMATO_SELECCIONADO.equals("Top Cut") && cb_opc.isChecked()){
                        et_rondas.setText(String.valueOf(rondas*2));
                    } else if(FORMATO_SELECCIONADO.equals("Top Cut") && !cb_opc.isChecked()){
                        et_rondas.setText(String.valueOf(rondas));
                    }
                } catch (NumberFormatException e) {
                    Log.e("ERROR","Error al convertir la cadena en un número entero: " + e.getMessage());
                }
            }
        });

        // Botón
        bt_ini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comprobarEditText()){
                    Toast.makeText(getContext(), "DATOS CORRECTOS", Toast.LENGTH_SHORT).show();
                    Log.i("DATOS",String.valueOf(num_rondas));
                    Log.i("DATOS",String.valueOf(num_participantes));
                    Log.i("DATOS",String.valueOf(num_ps_jpe));

                    // Añadiendo la informacion a la instancia singleton de los gestores;
                    gestor_tc.setN_rondas(num_rondas);
                    gestor_tc.setJugadores_iniciales(num_participantes);
                    gestor_tc.setPartidas_set(num_ps_jpe);
                    gestor_tc.setRonda_actual(1);
                    gestor_tc.setLoser_bracket(cb_opc.isChecked());

                    gestor_sw.setEs_multijugador(cb_opc.isChecked());
                    gestor_sw.setN_rondas(num_rondas);
                    gestor_sw.setJugadores_iniciales(num_participantes);
                    gestor_sw.setRonda_actual(1);
                    gestor_sw.setEs_multijugador(cb_opc.isChecked());
                    if (!cb_opc.isChecked()) {
                        gestor_sw.setJugadores_partida(2);
                        gestor_sw.setPartidas_set(num_ps_jpe);
                    } else {
                        gestor_sw.setJugadores_partida(num_ps_jpe);
                        gestor_sw.setPartidas_set(1);
                    }

                    if (FORMATO_SELECCIONADO.equals("Top Cut")) {
                        // PROMPT para añador al gestor_tc las pools
                        mostrarAlertPools(inflater);
                    } else {
                        Intent intent = new Intent(getContext(), PlayerActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        TextView selectedTextView = (TextView) view;
        int color = ContextCompat.getColor(requireContext(), R.color.black);
        selectedTextView.setTextColor(color);

        FORMATO_SELECCIONADO = sp_formato.getSelectedItem().toString();
        if (sp_formato.getSelectedItem().toString().equals("Top Cut")){
            et_rondas.setEnabled(false);
        } else {
            et_rondas.setEnabled(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * comprobarEditText: Método para comprobar si los datos introducidos en los EditText son válidos.
     * @return true si los datos son válidos, false de lo contrario.
     */
    public boolean comprobarEditText(){

        boolean correctos = true;

        // Comprobación formato
        try {
            num_participantes = Integer.parseInt(et_participantes.getText().toString());
            num_rondas = Integer.parseInt(et_rondas.getText().toString());
            num_ps_jpe = Integer.parseInt(et_partidas_set.getText().toString());

        } catch (NumberFormatException e) {
            correctos = false;
            Log.e("ERROR","Error al convertir la cadena en un número entero: " + e.getMessage());
            Toast.makeText(getContext(), "Error, datos no numéricos", Toast.LENGTH_SHORT).show();
        }

        // Comprobación participantes
        if (correctos) {
            if (FORMATO_SELECCIONADO != null && FORMATO_SELECCIONADO.equals("Swiss")){
                if(num_participantes < 6) {
                    correctos = false;
                    Toast.makeText(getContext(), "Error, en SW los participantes son 6+", Toast.LENGTH_SHORT).show();
                }
            }
            if (FORMATO_SELECCIONADO != null && FORMATO_SELECCIONADO.equals("Top Cut")){
                if(num_participantes <= 2 || ((num_participantes & (num_participantes - 1)) != 0)) {
                    correctos = false;
                    Toast.makeText(getContext(), "Error, en TC los participantes son 4, 8, 16...", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Comprobación rondas -> La comprobación del TC se calcula con logaritmos
        if (correctos) {
            int min_rondas = (int) (Math.log(num_participantes) / Math.log(2));
            if (FORMATO_SELECCIONADO != null && FORMATO_SELECCIONADO.equals("Swiss")){
                if(num_rondas < min_rondas){
                    correctos = false;
                    Toast.makeText(getContext(), "Error, en RR las rondas min. son Log2(Jug)" , Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Comprobación partidas por set y de jugadores por mesa, son excluyentes y dependen del checkbox
        if (correctos) {
            if (FORMATO_SELECCIONADO != null && FORMATO_SELECCIONADO.equals("Swiss") && cb_opc.isChecked()){
                if ((num_participantes % num_ps_jpe) != 0){
                    correctos = false;
                    Toast.makeText(getContext(), "Error, los jugadores no se reparten entre las mesas" , Toast.LENGTH_SHORT).show();
                }
            } else if ((num_ps_jpe % 2) == 0 || num_ps_jpe > 5) {
                correctos = false;
                Toast.makeText(getContext(), "Error, el numero de p/s es par o muy alto" , Toast.LENGTH_SHORT).show();
            }
        }

        return correctos;

    }

    /**
     * getFORMATO_SELECCIONADO: Método para obtener el formato de torneo seleccionado.
     * @return El formato de torneo seleccionado.
     */
    public static String getFORMATO_SELECCIONADO(){
        return FORMATO_SELECCIONADO;
    }

    /**
     * mostrarAlertPools: Método para mostrar un cuadro de diálogo para ingresar el número de pools en el caso de Top Cut.
     * @param inflater El inflador de diseño utilizado para inflar el diseño del cuadro de diálogo.
     */
    public void mostrarAlertPools(LayoutInflater inflater) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View dialogView = inflater.inflate(R.layout.dialog_prompt_pools, null);
        builder.setView(dialogView);
        EditText editTextPrompt = dialogView.findViewById(R.id.et_pools);

        // En el método onClick del botón bt_ini
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = editTextPrompt.getText().toString();
                // No existe ninguna potencia de 2 divisible entre 3
                int dato_in = 3;

                try {
                    dato_in = Integer.parseInt(userInput);
                } catch (NumberFormatException e) {

                }

                if (num_participantes % dato_in == 0) {
                    avanzar = true;
                } else {
                    avanzar = false;
                    Toast.makeText(getContext(), "POOLS INCORRECTAS", Toast.LENGTH_SHORT).show();
                }

                if (avanzar) {
                    gestor_tc.setNum_pools(dato_in);
                    Intent intent = new Intent(getContext(), PlayerActivity.class);
                    startActivity(intent);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
}