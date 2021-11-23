package com.example.teprestoonline.Controladores;


import android.widget.Toast;

import com.example.teprestoonline.Modelo.Pago;
import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.Modelo.amortizacion_cuota;
import com.example.teprestoonline.listado_prestamo;
import com.example.teprestoonline.utilidades.Fecha_utiliti;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class Prestamo_ctr {

    private FirebaseDatabase database = null;
    private DatabaseReference myRef;
    public static final String BBDD_NAME = "prestamos";
    public static final String BBDD_NAME2 = "amortizaciones";
    public static final String BBDD_NAME3 = "pagos";

    public void set_prestamo(Prestamo p){
        myRef.child(p.getId()).setValue(p);
    }

    public void set_prestamo_amortizaciones(amortizacion_cuota amortizacion){
        myRef = database.getReference(BBDD_NAME2);
      //  myRef.child(amortizacion.getId_prestamo()).child(amortizacion.getId()).setValue(amortizacion);
        myRef.child(amortizacion.getId_prestamo()).child(amortizacion.getFecha_cuota()).setValue(amortizacion);
    }

    public void set_eliminar(Prestamo p){
        myRef.child(p.getId()).removeValue();
        set_eliminar_amortizacion(p);
        set_eliminar_pagos(p);
    }

    private void set_eliminar_amortizacion(Prestamo p){
        myRef = database.getReference(BBDD_NAME2);
        myRef.child(p.getId()).removeValue();
    }

    private void set_eliminar_pagos(Prestamo p){
        myRef = database.getReference(BBDD_NAME3);
        myRef.child(p.getId()).removeValue();
    }

    public Prestamo_ctr(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(BBDD_NAME);
    }


    public void set_pago(Pago mipago){
        myRef = database.getReference(BBDD_NAME3);
        myRef.child(mipago.getId_prestamo()).child(mipago.getId()).setValue(mipago);
    }


    public void set_proceso_amortizaciones(Prestamo p){
        amortizacion_cuota amorizacion =null;
        String fecha_prox_cobro=p.getFecha_ult_cobro();
        String nueva_fecha="0001-01-01";
        boolean fecha_informada=false;

        if (fecha_prox_cobro.equals("0001-01-01")) {
            fecha_prox_cobro = new Fecha_utiliti().getFechaSystemaYYMMDD();
        }else{
            nueva_fecha = fecha_prox_cobro;
            fecha_informada=true;
        }

        for(int cont=0;cont<p.getCantidad_cuotas();cont++){// hago un bucle por la cantidad de cuotas que se parametrizo el prestamo
            amorizacion  = new amortizacion_cuota();
            amorizacion.set_datos_unicos();
            amorizacion.set_datos_ultima_modificaion();

                if (fecha_informada && cont == 0) {
                    //   Toast.makeText(prestamo_alta.this.actividad,"FECHA ES "+nueva_fecha,Toast.LENGTH_LONG).show();
                } else {
                    if (!fecha_informada) {
                        nueva_fecha = new Fecha_utiliti().suma_dias3(
                                ((cont + 1) * p.getPeriodo()), fecha_prox_cobro
                        );
                    } else {
                        nueva_fecha = new Fecha_utiliti().suma_dias3(
                                ((cont) * p.getPeriodo()), fecha_prox_cobro
                        );
                    }
                }

            double cuota = (int) Math.round(p.getCuota());
            amorizacion.setId_prestamo(p.getId());
            amorizacion.setFecha_cuota(nueva_fecha);
            amorizacion.setEstado(3);
            amorizacion.setCapital(p.getCapital_cuota());
            amorizacion.setInteres(p.getInteres_cuota());
            amorizacion.setCuota(cuota);
            amorizacion.setFecha_pago("0001-01-01");
            set_prestamo_amortizaciones(amorizacion);
        }
    }

}
