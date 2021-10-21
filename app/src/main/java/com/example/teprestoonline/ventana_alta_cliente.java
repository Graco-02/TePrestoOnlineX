package com.example.teprestoonline;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ventana_alta_cliente {

    private AppCompatActivity view;
    private Spinner spinner_tipdoc;

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

        spinner_tipdoc = (Spinner) v.findViewById(R.id.cli_form_tipos_identificacion);
        set_proceso_spinner_tipos_documento();
        final AlertDialog ventana = builder.show();

        Button bt_cancelar = v.findViewById(R.id.cli_form_cancelar);
        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ventana.dismiss();
            }
        });
    }

    private void set_proceso_spinner_tipos_documento(){
        List<String> tipos_documento = new ArrayList<String>();
        tipos_documento.add("CED");
        tipos_documento.add("PSS");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.view,
                android.R.layout.simple_spinner_item, tipos_documento);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_tipdoc.setAdapter(dataAdapter);

    }

}
