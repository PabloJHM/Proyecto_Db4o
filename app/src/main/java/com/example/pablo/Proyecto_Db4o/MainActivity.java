package com.example.pablo.Proyecto_Db4o;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pablo.Proyecto_Db4o.mapa.Posicion;
import com.example.pablo.Proyecto_Db4o.servicio.Servicio;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    private GoogleApiClient cliente;
    private LocationRequest peticionLocalizaciones;
    private Db4O bd;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LanzadorServicio lanzadorServicio =new LanzadorServicio();
        lanzadorServicio.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        bd=new Db4O(this);
        mMap = googleMap;
        PolylineOptions localizaciones = new PolylineOptions();
        localizaciones.color(Color.parseColor("#52d053"));
        localizaciones.width(4);
        localizaciones.visible(true);
        List<Posicion> posicionList = bd.getConsulta();
        if(!posicionList.isEmpty()) {
            Posicion puntoInicial = posicionList.get(0);
            LatLng punto = new LatLng(puntoInicial.getLatitud(), puntoInicial.getLongitud());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(punto));
            for (Posicion p : posicionList) {
                localizaciones.add(new LatLng(p.getLatitud(), p.getLongitud()));
            }
            mMap.addPolyline(localizaciones);
        }
        bd.close();
    }


    private class LanzadorServicio extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            Intent serviceIntent = new Intent(getBaseContext(),Servicio.class);
            getBaseContext().startService(serviceIntent);
            return true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        peticionLocalizaciones = new LocationRequest();
        peticionLocalizaciones.setFastestInterval(5000);
        peticionLocalizaciones.setSmallestDisplacement(1);
        peticionLocalizaciones.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(cliente, peticionLocalizaciones, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            cliente = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            cliente.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
       mMap.clear();
        bd=new Db4O(this);
        PolylineOptions rectOptions = new PolylineOptions();
        rectOptions.color(Color.parseColor("#0040FF"));
        rectOptions.width(4);
        rectOptions.visible(true);
        List<Posicion> posicionList = bd.getConsulta();
        if(!posicionList.isEmpty()) {
            Posicion puntoInicial = posicionList.get(0);
            LatLng punto = new LatLng(puntoInicial.getLatitud(), puntoInicial.getLongitud());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(punto));
            for (Posicion p : posicionList) {
                rectOptions.add(new LatLng(p.getLatitud(), p.getLongitud()));
            }

            mMap.addPolyline(rectOptions);
        }
        bd.close();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

