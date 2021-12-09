package com.example.teprestoonline.Controladores;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.teprestoonline.Inicio;
import com.example.teprestoonline.Modelo.Pago;
import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.R;
import com.example.teprestoonline.utilidades.Fecha_utiliti;
import com.example.teprestoonline.utilidades.Proceso_cobro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class estadisticas_firebase_ctr {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private List<Pago> lista_pagos;
    private Fecha_utiliti fecha_ctr = new Fecha_utiliti();

    public estadisticas_firebase_ctr(){

    }

    public void get_listado_pagos(int mes,int year){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Prestamo_ctr.BBDD_NAME3);
        lista_pagos = new ArrayList<>();

        final Query usuQuery = myRef.orderByChild("id_prestamo");
        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                       Pago pago = hijo.getValue(Pago.class);
                       System.out.println("FECHA DEL PAGO MES..:" +pago.getFecha_pago().substring(6,2));
                       System.out.println("FECHA DEL PAGO YEAR..:"+pago.getFecha_pago().substring(0,4));
                       int mes_pago = Integer.parseInt(pago.getFecha_pago().substring(6,2));
                       int year_pago = Integer.parseInt(pago.getFecha_pago().substring(0,4));
                      // if() {
                           lista_pagos.add(pago);
                    //   }
                    }
                }else{
                    System.out.println("SIN DATOS PARA MOSTRAR TOODOS LOS PAGOS..:" );
                  //  Toast.makeText(estadisticas_firebase_ctr.this,"No se encontraron datos",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public void setDatabase(FirebaseDatabase database) {
        this.database = database;
    }

    public DatabaseReference getMyRef() {
        return myRef;
    }

    public void setMyRef(DatabaseReference myRef) {
        this.myRef = myRef;
    }

    public List<Pago> getLista_pagos() {
        return lista_pagos;
    }

    public void setLista_pagos(List<Pago> lista_pagos) {
        this.lista_pagos = lista_pagos;
    }

    public Fecha_utiliti getFecha_ctr() {
        return fecha_ctr;
    }

    public void setFecha_ctr(Fecha_utiliti fecha_ctr) {
        this.fecha_ctr = fecha_ctr;
    }
}
