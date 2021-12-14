package com.example.teprestoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Cliente_ctr;
import com.example.teprestoonline.Controladores.Usuario_ctr;
import com.example.teprestoonline.Modelo.Cliente;
import com.example.teprestoonline.Modelo.Usuario;
import com.example.teprestoonline.utilidades.PDF_MAnager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Usuario_ctr usu;
    public static Usuario usuario_logueado;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usu =new Usuario_ctr(this);
        mAuth = FirebaseAuth.getInstance();
        get_validar_usuario_logueado();

    }


    public void onRestoreInstanceState(Bundle bundle){
           get_validar_usuario_logueado();

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
       // updateUI(currentUser);
    }

    public void get_nuevo_acceso(View view){
        new usuario_modifica_ventana().set_solcitar_nuevo_acceso(this);
    }

    public void get_acceso(View view) {
        EditText txt_nombre = (EditText) findViewById(R.id.main_usuario_txt);
        EditText txt_clave = (EditText) findViewById(R.id.main_clave_txt);

        String usuario = txt_nombre.getText().toString();
        String clave = txt_clave.getText().toString();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child(Usuario.BBDD_NAME);

        Query usuQuery = ref.orderByChild("usuario").equalTo(usuario);

        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        if(hijo.getValue(Usuario.class).getUsuario().equalsIgnoreCase(usuario)) {
                            usuario_logueado = hijo.getValue(Usuario.class);
                            if(usuario_logueado.getEstado()==1) { // valido la activacion del usuario
                                if (usuario_logueado.getContador_clave() < 5) {//valido la cantidad de oportunidades de la clave
                                    if (usuario_logueado.getUsuario().equals(usuario)) {//valido el nombre de usuario
                                        if (usuario_logueado.getClave().equals(clave)) {//valido la clave
                                            usuario_logueado.setContador_clave(0);
                                            usu.set_usuario(usuario_logueado);
                                            Intent lanzadera = new Intent(MainActivity.this, Inicio.class);
                                            Usuario.usuario_logueado = usuario_logueado;
                                            startActivity(lanzadera);
                                        } else {
                                            txt_clave.requestFocus();
                                            txt_clave.setError("Clave Inexistente");
                                            //sumo uno a la cantidad de intentos
                                            usuario_logueado.setContador_clave(usuario_logueado.getContador_clave() + 1);
                                            usu.set_usuario(usuario_logueado);
                                        }
                                    } else {
                                        txt_nombre.requestFocus();
                                        txt_nombre.setError("Usuario Inexistente");
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "Cantidad de intentos agotada favor contactar al administrador",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(MainActivity.this,
                                        "Este usuario aun no ha sido activado",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // do sontime
            }
        });

    }



    private void get_validar_usuario_logueado(){
        if(Usuario.usuario_logueado!=null){
            Intent lanzadera = new Intent(MainActivity.this, Inicio.class);
            startActivity(lanzadera);
        }
    }

}