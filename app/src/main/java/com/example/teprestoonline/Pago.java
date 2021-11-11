package com.example.teprestoonline;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Prestamo_ctr;
import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.utilidades.Fecha_utiliti;

public class Pago extends AppCompatActivity {

    private String id_pago;
    private int tipo;
    private double monto_restante;
    private String fecha_ult_pago;
    private String id_usuario;
    private String id_cliente;
    private EditText txt_monto_pago;
    private com.example.teprestoonline.Modelo.Pago mipago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        Bundle datos;
        datos =  getIntent().getExtras();

        txt_monto_pago = (EditText) findViewById(R.id.pago_monto_pago);
        EditText txt_fecha =  (EditText) findViewById(R.id.pago_fecha);
        EditText txt_fecha_ult_pago =  (EditText) findViewById(R.id.pago_fecha_ult_pago);
        EditText txt_monto_rest =  (EditText) findViewById(R.id.pago_monto_restante);
        EditText txt_tipo =  (EditText) findViewById(R.id.pago_tipo_prestamo);

        if(datos!=null){
            id_pago = datos.getString("id_prestamo");
            tipo = datos.getInt("tipo");
            monto_restante = datos.getDouble("monto_restante");
            fecha_ult_pago =  datos.getString("fec_ult_pago");
            id_usuario =  datos.getString("id_usuario");
            id_cliente =  datos.getString("id_cliente");

            switch (tipo){
                case 0:
                    txt_tipo.setText("REGULAR");
                    break;
                default:
                    txt_tipo.setText("CUOTAS");
                    break;
            }

            mipago.setId_cliente(id_cliente);
            mipago.setId_usuario(id_usuario);
            mipago.setId_prestamo(id_pago);
            mipago.setMonto_restante(monto_restante);
            mipago.setTipo(txt_tipo.getText().toString());

            txt_monto_rest.setText(""+monto_restante);
            txt_fecha.setText(new Fecha_utiliti().getFechaSystemaYYMMDD());
            txt_fecha_ult_pago.setText(fecha_ult_pago);
        }

        Button bt_pagar = (Button) findViewById(R.id.pago_bt_pagar);
        bt_pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_mensage("Desea realizar el pago?");
            }
        });

    }

    private void set_mensage(String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(Pago.this);
        builder.setTitle("Notificacion ");
        builder.setMessage(mensaje);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(!txt_monto_pago.getText().toString().isEmpty()
                   && Double.parseDouble(txt_monto_pago.getText().toString()) > 0
                ) {
                    mipago.setMonto_pagado(Double.parseDouble(txt_monto_pago.getText().toString()));
                    set_proceso_amortiza_pago();
                    Toast.makeText(Pago.this,"Correcto",Toast.LENGTH_LONG).show();
                }else{
                   txt_monto_pago.setError("Se debe informar mayor a 0");
                   txt_monto_pago.requestFocus();
                }
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

    
    protected void  set_proceso_amortiza_pago(){
        new Prestamo_ctr().set_pago(mipago);
    }

}