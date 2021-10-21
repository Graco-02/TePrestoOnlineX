package com.example.teprestoonline;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ventana_alta_cliente {

    private AppCompatActivity view;

    public ventana_alta_cliente(AppCompatActivity view){
        this.view = view;
    }


    public void set_view_alta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(view);
        builder.setTitle("Alta Clientes");
        LayoutInflater inflater = view.getLayoutInflater();
        View v = inflater.inflate(R.layout.formulario_cliente_alta, null);
        builder.setView(v);
        builder.create();


        final AlertDialog ventana = builder.show();

    }

}
