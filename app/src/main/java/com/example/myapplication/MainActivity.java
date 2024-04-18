package com.example.myapplication;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {


    private LocationCallback locationCallback;
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 123;


    FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }


    @Override
    protected void onStart() {

        checkPermission();

        statusCheck();
        // Crear una instancia de FusedLocationProviderClient
        //  FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Crear una instancia de LocationRequest
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Intervalo de actualización en milisegundos
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Crear una instancia de LocationCallback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    // Actualizar la ubicación
                }
            }

        };

        // Solicitar actualizaciones de ubicación
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // La ubicación puede ser nula si no se ha obtenido ninguna ubicación recientemente
                        if (location != null) {
                            // Usa la ubicación
                            String latitude = String.valueOf(location.getLatitude());
                            String longitude = String.valueOf(location.getLongitude());

                            // Muestra la latitud y longitud
                            Log.i("Location", "Latitud: " + latitude + ", Longitud: " + longitude);
                            TextView locationTextView = (TextView) findViewById(R.id.main_activity_location_textView);
                            locationTextView.setText("Location" + "Latitud: " + latitude + ", Longitud: " + longitude);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar la excepción
                    }
                });

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        super.onStart();
    }


    @Override
    protected void onStop() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }


    private void checkPermission() {
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Si no se ha concedido el permiso, lo solicitamos
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);

        }
    }



    //aun no se me ocurre como suar este metodo
       //se ejecuta aumaticamente tnato si se acepto el permiso como si se denego
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso fue concedido, puedes continuar con la operación que requiere el permiso
            } else {
                if(shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Acceder a  la ubicacion del telefono");
                    builder.setMessage("Debes aceptar este permiso para poder utiliar la app Trovami");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
                        }
                    });
                    builder.show();
                }
                // El permiso fue denegado. Debes manejar adecuadamente esta situación.
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


        public void statusCheck() {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }
        }

        private void buildAlertMessageNoGps() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }


}