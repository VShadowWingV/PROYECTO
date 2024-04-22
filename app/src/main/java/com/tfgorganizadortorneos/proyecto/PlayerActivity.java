package com.tfgorganizadortorneos.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tfgorganizadortorneos.proyecto.fragments.BFragmentFormatSel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Gestor_SW;
import model.Gestor_TC;
import model.Participante_Tipo_SW;
import model.Participante_Tipo_TC;

public class PlayerActivity extends AppCompatActivity {

    // Elementos vista
    EditText et_nombre;
    EditText et_alias;
    Spinner sp_pool;
    Button bt_aniadir;

    // Elementos modelo
    String formato_main = BFragmentFormatSel.getFORMATO_SELECCIONADO();
    Gestor_SW gestor_sw = Gestor_SW.getInstance();
    Gestor_TC gestor_tc = Gestor_TC.getInstance();
    List<Integer> lista_pools = new ArrayList<>();

    int contParticipantes = 0;
    int [] participantes_restantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);

        // Obtener elementos VIEW
        et_nombre = findViewById(R.id.et_nombre);
        et_alias = findViewById(R.id.et_alias);
        sp_pool = findViewById(R.id.sp_pool);
        bt_aniadir = findViewById(R.id.bt_aniadir);

        if (!formato_main.equals("Top Cut")){
            sp_pool.setEnabled(false);
        } else {
            establecerPools();
            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lista_pools);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_pool.setAdapter(adapter);
        }

        // Botón
        bt_aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Comprobaciones nombre y alias aquí -> Añadir más en el bool si se considera
                boolean comp_nom = !et_nombre.getText().toString().equals("");
                boolean comp_al = !et_alias.getText().toString().equals("");
                if (comp_nom && comp_al) {
                    guardarParticipanteFormato(formato_main);
                } else {
                    Toast.makeText(getApplicationContext(), "DATOS INCORRECTOS", Toast.LENGTH_SHORT).show();
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
     * establecerPools: Crea un vector de pools para comprobar si todas las pools estan completas. Ejemplo: Si tenemos
     * pool 1 y pool 2 con 8 participantes creara el siguiente vector [4][4] e irá restando segun vayas cumplimentado la pool.
     */
    public void establecerPools(){
        for (int i = 1; i <= gestor_tc.getNum_pools(); i++) {
            lista_pools.add(i);
        }
        int participantes_por_pool = gestor_tc.getJugadores_iniciales()/gestor_tc.getNum_pools();
        participantes_restantes = new int[gestor_tc.getNum_pools()];
        for (int i = 0; i < participantes_restantes.length; i++) {
            participantes_restantes[i] = participantes_por_pool;
        }
        Log.i("Vector pools", Arrays.toString(participantes_restantes));

    }

    /**
     * guardarParticipanteFormato: Método para guardar un participante en el torneo según el formato seleccionado.
     * @param formato_main El formato del torneo (Top Cut o Swiss).
     */
    public void guardarParticipanteFormato(String formato_main) {
        switch (formato_main) {
            case "Top Cut":
                if (contParticipantes < (gestor_tc.getJugadores_iniciales() - 1)) {
                    if (participantes_restantes[(int) sp_pool.getSelectedItem() - 1] > 0) {
                        gestor_tc.aniadirParticipanteTC(new Participante_Tipo_TC(contParticipantes,et_nombre.getText().toString(),et_alias.getText().toString()));
                        gestor_tc.getLista_participantes().get(contParticipantes).setPool((int) sp_pool.getSelectedItem());
                        Log.i("Participante añadido", gestor_tc.getLista_participantes().get(contParticipantes).toString());
                        Toast.makeText(getApplicationContext(), "Participante añadido: " + (contParticipantes + 1), Toast.LENGTH_SHORT).show();
                        participantes_restantes[(int) sp_pool.getSelectedItem() - 1]--;
                        contParticipantes++;

                        // Vaciar texto participante
                        et_nombre.setText("");
                        et_alias.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "Pool completa, no valido", Toast.LENGTH_SHORT).show();
                    }
                } else if (contParticipantes == (gestor_sw.getJugadores_iniciales() - 1)){
                    gestor_tc.aniadirParticipanteTC(new Participante_Tipo_TC(contParticipantes,et_nombre.getText().toString(),et_alias.getText().toString()));
                    gestor_tc.getLista_participantes().get(contParticipantes).setPool((int) sp_pool.getSelectedItem());
                    Log.i("Participante añadido", gestor_tc.getLista_participantes().get(contParticipantes).toString());
                    Toast.makeText(getApplicationContext(), "Participante añadido: " + (contParticipantes + 1), Toast.LENGTH_SHORT).show();
                    participantes_restantes[(int) sp_pool.getSelectedItem() - 1]--;
                    contParticipantes++;

                    Toast.makeText(getApplicationContext(), "Iniciando torneo TC...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PlayerActivity.this, TopCutActivity.class);
                    startActivity(intent);
                }
                break;
            case "Swiss":
                if (contParticipantes < (gestor_sw.getJugadores_iniciales() - 1)) {
                    gestor_sw.aniadirParticipanteSW(new Participante_Tipo_SW(contParticipantes,et_nombre.getText().toString(),et_alias.getText().toString()));
                    Log.i("Participante añadido", gestor_sw.getLista_participantes().get(contParticipantes).toString());
                    Toast.makeText(getApplicationContext(), "Participante añadido: " + (contParticipantes + 1), Toast.LENGTH_SHORT).show();
                    contParticipantes++;

                    // Vaciar texto participante
                    et_nombre.setText("");
                    et_alias.setText("");

                } else if (contParticipantes == (gestor_sw.getJugadores_iniciales() - 1)){
                    gestor_sw.aniadirParticipanteSW(new Participante_Tipo_SW(contParticipantes,et_nombre.getText().toString(),et_alias.getText().toString()));
                    Log.i("Participante añadido", gestor_sw.getLista_participantes().get(contParticipantes).toString());
                    Toast.makeText(getApplicationContext(), "Participante añadido: " + (contParticipantes + 1), Toast.LENGTH_SHORT).show();
                    contParticipantes++;

                    Toast.makeText(getApplicationContext(), "Iniciando torneo SW...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PlayerActivity.this, SwissActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

}