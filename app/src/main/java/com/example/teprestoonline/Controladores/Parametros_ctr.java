package com.example.teprestoonline.Controladores;

import android.content.Context;

import com.example.teprestoonline.Inicio;
import com.example.teprestoonline.Modelo.Parametros;
import com.example.teprestoonline.Modelo.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Parametros_ctr {
    private FirebaseDatabase database = null;
    private DatabaseReference myRef;
    public final String RUTA_PARAMETROS = "parametros";


    public Parametros_ctr(Context con){
        FirebaseApp.initializeApp(con);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    public void set_parametros(Parametros parametro){
        myRef.child(RUTA_PARAMETROS).child(parametro.getId_usuario()).child(parametro.getId()).setValue(parametro);
        Inicio.parametros = parametro;
    }
}
