package com.elevenzon.TextInputLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    Button QR, Loca, add_id;
    TextView tv_presenceEntrepot, tv_valeurVilleActuelleAffichage;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    FusedLocationProviderClient fusedLocationProviderClient;
    String line;
    boolean entrepotEstPresent = false;

    public static final int DEFAULT_UPDATE_INTERVAL = 5;
    public static final int FAST_UPDATE_INTERVAL = 2;
    private static final int PERMISSIONS_FINE_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        QR = (Button) findViewById(R.id.QR);
        Loca = (Button) findViewById(R.id.Loca);
        add_id = (Button) findViewById(R.id.add_item);
        tv_presenceEntrepot = (TextView) findViewById(R.id.tv_presenceEntrepot);
        tv_valeurVilleActuelleAffichage = (TextView) findViewById(R.id.tv_valeurVilleActuelleAffichage);

        locationRequest = LocationRequest.create()
                .setInterval(100)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);
        locationRequest.setInterval(DEFAULT_UPDATE_INTERVAL * 1000);
        locationRequest.setFastestInterval(FAST_UPDATE_INTERVAL * 1000);
        locationRequest.setPriority(locationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };
        
        Loca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainLoca.class);
                startActivity(intent);
            }
        });

        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Mainqr.class);
                Bundle extras = new Bundle();
                if (entrepotEstPresent) {
                    extras.putString("villeActuelle", String.valueOf(tv_valeurVilleActuelleAffichage.getText()));
                }
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        add_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddProduitActivity.class);
                startActivity(i);
            }
        });
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        updateGPS();
    }

    private void updateGPS() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomeActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> test = fusedLocationProviderClient.getLastLocation();
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        boolean entrepot = rechercheUnEntrepotDansLaVilleActuelle(location);
                        Geocoder geocoder = new Geocoder(HomeActivity.this);
                        List<Address> address = null;
                        try {
                            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        tv_valeurVilleActuelleAffichage.setText(address.get(0).getLocality());
                        if (entrepot == true) {
                            tv_presenceEntrepot.setText("Un entrepot est présent");
                            entrepotEstPresent = true;
                        } else {
                            tv_presenceEntrepot.setText("Aucun entrepot présent");
                        }
                    }
                }
            });
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }

        }

    }

    public boolean rechercheUnEntrepotDansLaVilleActuelle(Location location) {
        URL url;
        boolean entrepotDansLaVille = false;
        Geocoder geocoder = new Geocoder(HomeActivity.this);
        try {
            // Il faut changer l'url selon l'adresse IP
            List<Address> address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            url = new URL("http://192.168.92.2/all4sport-master/API/rechercheEntrepot.php/?villeActuelle="+address.get(0).getLocality());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            line = rd.readLine();
            System.out.println(line);
            if (line.equals("true")) {
                entrepotDansLaVille = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(entrepotDansLaVille);
        return entrepotDansLaVille;
    }

}
