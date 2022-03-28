package com.elevenzon.TextInputLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AddProduitActivity extends AppCompatActivity {

    Button add;
    EditText produit, etagere, section, rangee, module, qte, ref, entrepot;
    String recupProduit, recupEtagere, recupSection, recupRangee, recupModule, recupQte, recupRef, recupEntrepot;
    String line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduit);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        qte = (EditText) findViewById(R.id.qte);
        produit = (EditText) findViewById(R.id.ref);
        entrepot = (EditText) findViewById(R.id.entrepot);
        rangee = (EditText) findViewById(R.id.rangee);
        section = (EditText) findViewById(R.id.section);
        etagere = (EditText) findViewById(R.id.etagere);
        module = (EditText) findViewById(R.id.module);
        add = (Button) findViewById(R.id.add);

        String reference = (String) getIntent().getSerializableExtra("produit");

        if (reference != null) {
            produit.setText(reference);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recupProduit = produit.getText().toString();
                recupEtagere = etagere.getText().toString();
                recupSection = section.getText().toString();
                recupRangee = rangee.getText().toString();
                recupModule = module.getText().toString();
                recupQte = qte.getText().toString();
                //recupRef = ref.getText().toString();
                recupEntrepot = entrepot.getText().toString();


                URL url;
                try {
                    // Il faut changer l'url selon l'adresse IP
                    url = new URL("http://192.168.239.2/ALL4SPORT-master/API/ajouterStock.php?produit="+recupProduit+"&qte="+recupQte+"&etagere="+recupEtagere+"&section="+recupSection+"&rangee="+recupRangee+"&module="+recupModule+"&entrepot="+recupEntrepot);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    line = rd.readLine();
                    System.out.println(line);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (line.equals("Ajouté")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Produit ajouté avec succès", Toast.LENGTH_SHORT);
                    toast.setMargin(50, 50);
                    toast.show();
                    Intent i = new Intent(AddProduitActivity.this, HomeActivity.class);
                    startActivity(i);
                } else if (line.equals("Erreur")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Erreur, veuillez réessayer", Toast.LENGTH_SHORT);
                    toast.setMargin(50, 50);
                    toast.show();
                }
            }
        });
    }
}