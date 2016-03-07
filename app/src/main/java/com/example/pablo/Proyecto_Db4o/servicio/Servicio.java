package com.example.pablo.Proyecto_Db4o.servicio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.pablo.Proyecto_Db4o.Db4O;
import com.example.pablo.Proyecto_Db4o.R;
import com.example.pablo.Proyecto_Db4o.mapa.Posicion;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;

public class Servicio extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    private GoogleApiClient cliente;
    private LocationRequest peticionLocalizaciones;
    private Posicion position;
    private Date d;
    private Db4O bd;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        position=new Posicion();
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Servicio iniciado");
        Intent i=new Intent(Servicio.this, Servicio.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Notification.Builder constructorNotificacion = new Notification.Builder(Servicio.this).setSmallIcon(R.drawable.common_plus_signin_btn_icon_light_normal).setContentTitle("GPS Servicio Activo").setContentText("Servicio Activado").setContentIntent(
                PendingIntent.getActivity(Servicio.this, 0, i, 0));
        NotificationManager gestorNotificacion = (NotificationManager)getSystemService(Servicio.this.NOTIFICATION_SERVICE);
        startForeground(1, constructorNotificacion.build());
        d=new Date();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onConnected(Bundle bundle) {
        peticionLocalizaciones = new LocationRequest();
        peticionLocalizaciones.setFastestInterval(5000);
        peticionLocalizaciones.setSmallestDisplacement(1);
        peticionLocalizaciones.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(cliente, peticionLocalizaciones, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //Cuando cambie la localización guardaremos el nuevo punto en la db4o
    @Override
    public void onLocationChanged(Location location) {
        bd = new Db4O(this);
        Posicion pos=new Posicion();
        pos.setLatitud((float) location.getLatitude());
        pos.setLongitud((float) location.getLongitude());
        d.getTime();
        pos.setFecha(d);
        bd.insertar(pos);
        Log.v("XXX","Insertado en la bdd: "+pos.toString());
        bd.close();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        cliente = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener
                (this).addApi(LocationServices.API).build();
        cliente.connect();
    }

    @Override
    public void onDestroy() {}
}
