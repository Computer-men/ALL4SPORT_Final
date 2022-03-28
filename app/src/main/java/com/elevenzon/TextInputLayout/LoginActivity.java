package com.elevenzon.TextInputLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView register;
    TextInputLayout emailError, passError;
    TextView verif;
    String recupmail;
    String recupmdp;
    String line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        email = (EditText) findViewById(R.id.qte);
        password = (EditText) findViewById(R.id.ref);
        login = (Button) findViewById(R.id.add);
        register = (TextView) findViewById(R.id.register);
        emailError = (TextInputLayout) findViewById(R.id.emailError);
        passError = (TextInputLayout) findViewById(R.id.passError);
        verif = (TextView) findViewById(R.id.test);

        verif.setText("Chargement");

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                verif.setText("En attente");
                if (isNetworkAvailable())
                {
                    recupmail = email.getText().toString();
                    recupmail = recupmail.replaceFirst("@", "%40");
                    recupmdp = password.getText().toString();

                    verif.setText("Connecté");

                    URL url;
                    try {
                        // Il faut changer l'url selon l'adresse IP
                        url = new URL("http://192.168.239.2/ALL4SPORT-master/API/connexion.php/?email="+recupmail+"&password="+recupmdp);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        line = rd.readLine();
                        System.out.println(line);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    verif.setText("Non connecté");
                }

                if (line.equals("réussie")) {
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                } else if (line.equals("echec")) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Erreur d'authentification, veuillez réssayer.", Toast.LENGTH_SHORT);
                    toast.setMargin(50, 50);
                    toast.show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to RegisterActivity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }else {return false;}

    }
}