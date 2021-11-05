package com.example.teprestoonline.Controladores;

import com.example.teprestoonline.Modelo.Cliente;
import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.Modelo.amortizacion_cuota;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Prestamo_ctr {
    private FirebaseDatabase database = null;
    private DatabaseReference myRef;
    public static final String BBDD_NAME = "prestamos";
    public static final String BBDD_NAME2 = "amortizaciones";

    public void set_prestamo(Prestamo p){
        myRef.child(p.getId()).setValue(p);
    }

    public void set_prestamo_amortizaciones(amortizacion_cuota amortizacion){
        myRef = database.getReference(BBDD_NAME2);
        myRef.child(amortizacion.getId_prestamo()).child(amortizacion.getId()).setValue(amortizacion);
    }

    public void set_eliminar(Prestamo p){
        myRef.child(p.getId()).removeValue();
        set_eliminar_amortizacion(p);
    }

    private void set_eliminar_amortizacion(Prestamo p){
        myRef = database.getReference(BBDD_NAME2);
        myRef.child(p.getId()).removeValue();
    }

    public Prestamo_ctr(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(BBDD_NAME);
    }
}
