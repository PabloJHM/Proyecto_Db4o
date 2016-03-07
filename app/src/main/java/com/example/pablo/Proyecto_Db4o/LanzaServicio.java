package com.example.pablo.Proyecto_Db4o;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.pablo.Proyecto_Db4o.servicio.Servicio;

public class LanzaServicio extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Context c = context;
        Intent serviceIntent = new Intent(context,Servicio.class);
        context.startService(serviceIntent);
    }
}