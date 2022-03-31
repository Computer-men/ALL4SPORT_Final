package com.elevenzon.TextInputLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Mainqr extends AppCompatActivity implements View.OnClickListener {

    Button scanBtn ;
    String line;
    String produit;
    String villeActuelle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        scanBtn = findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();

        villeActuelle = (String) getIntent().getSerializableExtra("villeActuelle");
        System.out.println(villeActuelle);

    }

    @Override
    public void onClick(View v) {
        scanCode();
    }

    private void scanCode() {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null ){
            if (result.getContents() != null){
                produit = result.getContents();
                // Connexion BDD
                URL url;
                try {
                    url = new URL("http://192.168.92.2/ALL4SPORT_API-master/API/produit.php/?produit="+produit);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    line = rd.readLine();
                    System.out.println(line);}
                catch (IOException e) {
                    e.printStackTrace();
                }
                // Fin connexion BDD

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(line);
                builder.setTitle(produit);
                builder.setPositiveButton("Réessayez", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        scanCode();
                    }
                }).setNegativeButton("Ajoutez le produit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Intent i = new Intent(getApplicationContext(), AddProduitActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("produit",produit);
                        if (villeActuelle != null) {
                            extras.putString("villeActuelle", villeActuelle);
                        }
                        i.putExtras(extras);
                        startActivity(i);

                        // finish();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Toast.makeText(this, "No result", Toast.LENGTH_LONG).show();

            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}