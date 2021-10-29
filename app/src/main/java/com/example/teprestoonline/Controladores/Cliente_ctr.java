package com.example.teprestoonline.Controladores;

import android.content.Context;

import com.example.teprestoonline.Modelo.Cliente;
import com.example.teprestoonline.Modelo.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Cliente_ctr {

    private FirebaseDatabase database = null;
    private DatabaseReference myRef;
    public static final String BBDD_NAME = "clientes";

    public void set_cliente(Cliente cli){
        myRef.child(cli.getPersona().getIdentificacion()).setValue(cli);
    }

    public void set_eliminar(Cliente cli){
       // System.out.println(cli.getPersona().getIdentificacion());
        myRef.child(cli.getPersona().getIdentificacion()).removeValue();
    }

    public Cliente_ctr(Context con){
       // FirebaseApp.initializeApp(con);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(BBDD_NAME);
    }



}
