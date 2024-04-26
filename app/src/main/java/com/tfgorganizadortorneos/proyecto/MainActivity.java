package com.tfgorganizadortorneos.proyecto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

import model.Gestor_SW;
import model.Gestor_TC;

public class MainActivity extends AppCompatActivity {

    Gestor_SW gestor_sw = Gestor_SW.getInstance();
    Gestor_TC gestor_tc = Gestor_TC.getInstance();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String fileNameS = "torneo_sw_tmp.json";
        File directoryS = new File(getApplicationContext().getExternalFilesDir(null), "TorneoSW");
        File temporalSuizo = new File(directoryS, fileNameS);

        String fileNameT = "torneo_tc_tmp.json";
        File directoryT = new File(getApplicationContext().getExternalFilesDir(null), "TorneoTC");
        File temporalTopCut = new File(directoryT, fileNameT);

        if (temporalSuizo.exists()) {
            Intent intent = new Intent(MainActivity.this, SwissActivity.class);
            startActivity(intent);
            finish();
        } else if(temporalTopCut.exists()) {
            Intent intent = new Intent(MainActivity.this, TopCutActivity.class);
            startActivity(intent);
            finish();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}