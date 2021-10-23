package com.example.teprestoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Cliente_ctr;
import com.example.teprestoonline.Modelo.Cliente;
import com.example.teprestoonline.Modelo.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class mantenimiento_clientes extends AppCompatActivity {

    private EditText txt_identificacion;
    private FirebaseDatabase database = null;
    private DatabaseReference myRef;
    private ArrayList<Cliente> listado_clientes;
    private LinearLayout ln;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantenimiento_clientes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        txt_identificacion = (EditText) findViewById(R.id.cli_mtn_barra_busqueda_txt);
        txt_identificacion.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(txt_identificacion.getText().toString().length()==3){
                    txt_identificacion.setText(txt_identificacion.getText().toString()+"-");
                    txt_identificacion.setSelection(4);
                }else if(txt_identificacion.getText().toString().length()==11){
                    txt_identificacion.setText(txt_identificacion.getText().toString()+"-");
                    txt_identificacion.setSelection(12);
                }
                return false;
            }
        });

      //  set_listar_todos();

    }

    @Override
    protected void onResume() {
        super.onResume();
        set_listar_todos();
    }

    private void set_listar_todos(){
        listado_clientes = new ArrayList<>();
        ln = (LinearLayout) findViewById(R.id.clientes_mnt_lista);
        ln.removeAllViews();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = database.child(Cliente_ctr.BBDD_NAME);

        Query usuQuery = ref.orderByChild("id_usuario").equalTo(Usuario.usuario_logueado.getId());

        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        if(hijo.getValue(Cliente.class).getId_usuario().equalsIgnoreCase(Usuario.usuario_logueado.getId())) {
                            Cliente cli = hijo.getValue(Cliente.class);
                            listado_clientes.add(cli);

                            LayoutInflater inflater = getLayoutInflater();
                            View v = inflater.inflate(R.layout.cliente_lista, null);

                            EditText txt_nombres = (EditText) v.findViewById(R.id.cli_view_nombre);
                            EditText txt_apellido = (EditText) v.findViewById(R.id.cli_view_apellidos);
                            EditText txt_identificacion = (EditText) v.findViewById(R.id.cli_view_identificacion);
                            Spinner tipo_doc = (Spinner) v.findViewById(R.id.cli_view_tipos_identificacion);

                            txt_nombres.setEnabled(false);
                            txt_apellido.setEnabled(false);
                            txt_identificacion.setEnabled(false);
                            tipo_doc.setEnabled(false);

                            //lleno el combo tipos de documentos
                            List<String> tipos_documento = new ArrayList<String>();
                            tipos_documento.add("CED");
                            tipos_documento.add("PSS");
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mantenimiento_clientes.this,
                                   android.R.layout.simple_spinner_item, tipos_documento);
                            // Drop down layout style - list view with radio button
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            // attaching data adapter to spinner
                            tipo_doc.setAdapter(dataAdapter);

                            txt_nombres.setText(cli.getPersona().getNombres());
                            txt_apellido.setText(cli.getPersona().getApellidos());
                            txt_identificacion.setText(cli.getPersona().getIdentificacion());

                            if(cli.getPersona().getTipo_identificacion().equals("CED")){
                                tipo_doc.setSelection(0);
                            }else{
                                tipo_doc.setSelection(1);
                            }


                           v.setOnClickListener(new View.OnClickListener() {//agrego accion al clicar sobre un cliente
                                @Override
                                public void onClick(View view) {
                                    EditText txt_nombres = (EditText) view.findViewById(R.id.cli_view_nombre);
                                    Toast.makeText(getApplicationContext(),
                                            txt_nombres.getText().toString(),Toast.LENGTH_LONG).show();
                                }
                            });


                            ln.addView(v);
                        }
                    }
                }else{
                    System.out.println("No se encontro nada para " + Usuario.usuario_logueado.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // do sontime
            }
        });
    }

    public void  onSaveInstanceState(Bundle bundle){

        super.onSaveInstanceState(bundle);
    }

    public void onRestoreInstanceState(Bundle bundle){

        super.onRestoreInstanceState(bundle);
        set_listar_todos();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_clientes,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem opcion_menu){
        int id = opcion_menu.getItemId();

        switch (id){
            case R.id.cli_mtn_menu_alta:
                new ventana_alta_cliente(this).set_view_alta();
                set_listar_todos();
                break;
        }

        return super.onOptionsItemSelected(opcion_menu);
    }

}