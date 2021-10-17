package com.example.teprestoonline.Controladores;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.teprestoonline.Modelo.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class Usuario_ctr {

    private FirebaseDatabase database = null;
    private DatabaseReference myRef;
    private static ArrayList<Usuario> lista;
    private Usuario userant;


    public Usuario_ctr(Context con){
        FirebaseApp.initializeApp(con);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        lista = new ArrayList<>();
        lista.clear();
    }

    public void set_usuario(Usuario usu){
        myRef.child(Usuario.BBDD_NAME).child(usu.getId()).setValue(usu);
    }

    public void set_agregar_atributo(Usuario usu){
        myRef.child(Usuario.BBDD_NAME).child(usu.getId()).child(Usuario.BBDD_NAME2).setValue(usu.getFecha_humana_ult_mod());
        myRef.child(Usuario.BBDD_NAME).child(usu.getId()).child(Usuario.BBDD_NAME3).setValue(usu.getFecha_unix_ult_mod());
    }

    public void set_borrar_tabla(){
        myRef.removeValue();
    }


    private void set_agregar(Usuario usu){
        if(lista.size()==0){
            lista.add(usu);
        }else{
            if(lista.indexOf(usu) < 0){
                lista.add(usu);
            }
        }
    }


    public int get_lista_sise(){
        return lista.size();
    }

    public void set_cargar(){

        myRef.child(Usuario.BBDD_NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot objeto : snapshot.getChildren()) {
                        Usuario usuario = objeto.getValue(Usuario.class);
                        set_agregar(usuario);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(contexto, error.toException().toString(),Toast.LENGTH_LONG);
            }
        });

    }


    public void set_listar(){
        for(int i = 0;i<lista.size();i++){
            System.out.println("BBDD "+ lista.get(i).getUsuario() + " - " + lista.get(i).getClave() );
        }

    }


    public Usuario get_usuario_x_nombre(String nombre ){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child(Usuario.BBDD_NAME);

        Query usuQuery = ref.orderByChild("usuario").equalTo(nombre);

        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        if(hijo.getValue(Usuario.class).getUsuario().equalsIgnoreCase(nombre)) {
                            userant = hijo.getValue(Usuario.class);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    // do sontime
            }
        });

        return userant;
    }


}
