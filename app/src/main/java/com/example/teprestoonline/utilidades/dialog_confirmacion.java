package com.example.teprestoonline.utilidades;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.prestamo_alta;

import androidx.appcompat.app.AppCompatActivity;

public class dialog_confirmacion {

    private void set_mensage_ingresar_prestamo(String mensaje, Prestamo p, AppCompatActivity actividad){
        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
        builder.setTitle("Notificacion ");
        builder.setMessage(mensaje);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(actividad,"Agregado",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
