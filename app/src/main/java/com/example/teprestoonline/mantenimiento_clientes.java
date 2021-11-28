package com.example.teprestoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Cliente_ctr;
import com.example.teprestoonline.Controladores.Prestamo_ctr;
import com.example.teprestoonline.Modelo.Cliente;
import com.example.teprestoonline.Modelo.Prestamo;
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
        ln = (LinearLayout) findViewById(R.id.clientes_mnt_lista);

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


        ImageButton bt_limpiar = (ImageButton) findViewById(R.id.cli_mtn_barra_limpiar_bt);
        bt_limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_listar_todos();
                bt_limpiar.setVisibility(View.GONE);
                txt_identificacion.setText("");
            }
        });

        ImageButton bt_buscar = (ImageButton) findViewById(R.id.cli_mtn_barra_busqueda_bt);
        bt_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_buscar_cliente(txt_identificacion.getText().toString());
                bt_limpiar.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        set_listar_todos();
    }


    private void get_buscar_cliente(String identificacion){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child(Cliente_ctr.BBDD_NAME);
        final Query usuQuery = ref.orderByChild("id_usuario").equalTo(Usuario.usuario_logueado.getId());
        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    ln.removeAllViews();
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        if(
                                hijo.getValue(Cliente.class).getId_usuario().equalsIgnoreCase(Usuario.usuario_logueado.getId())
                                &&
                                hijo.getValue(Cliente.class).getPersona().getIdentificacion().equalsIgnoreCase(identificacion)
                        ) {
                            Cliente cli = hijo.getValue(Cliente.class);
                            ln.addView(set_agregar_cliente(cli));
                        }
                    }
                }else{
                   Toast.makeText(mantenimiento_clientes.this,"No se encontraron datos",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void set_listar_todos(){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child(Cliente_ctr.BBDD_NAME);
        final Query usuQuery = ref.orderByChild("id_usuario").equalTo(Usuario.usuario_logueado.getId());


        usuQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    ln.removeAllViews();
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        if(hijo.getValue(Cliente.class).getId_usuario().equalsIgnoreCase(Usuario.usuario_logueado.getId())) {
                            Cliente cli = hijo.getValue(Cliente.class);
                            ln.addView(set_agregar_cliente(cli));
                        }
                    }
                }else{
                    System.out.println("No se encontro nada para " + Usuario.usuario_logueado.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private View set_agregar_cliente(Cliente cli){
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.cliente_lista, null);
        Cliente cl = cli; // creo una instacia final para poder utilizarla en los botones

        EditText txt_nombres = (EditText) v.findViewById(R.id.cli_view_nombre);
        EditText txt_apellido = (EditText) v.findViewById(R.id.cli_view_apellidos);
        EditText txt_identificacion = (EditText) v.findViewById(R.id.cli_view_identificacion);
        Spinner tipo_doc = (Spinner) v.findViewById(R.id.cli_view_tipos_identificacion);

        txt_nombres.setEnabled(false);
        txt_apellido.setEnabled(false);
        txt_identificacion.setEnabled(false);
        tipo_doc.setEnabled(false);


        tipo_doc.setAdapter(proceso_spiner());

        txt_nombres.setText(cl.getPersona().getNombres());
        txt_apellido.setText(cl.getPersona().getApellidos());
        txt_identificacion.setText(cl.getPersona().getIdentificacion());

        if(cl.getPersona().getTipo_identificacion().equals("CED")){
            tipo_doc.setSelection(0);
        }else{
            tipo_doc.setSelection(1);
        }


        ImageButton bt_modificar = (ImageButton) v.findViewById(R.id.cli_view_bt_modificar);
        bt_modificar.setOnClickListener(new View.OnClickListener() {//agrego accion al clicar sobre un cliente
            @Override
            public void onClick(View view) {
                set_visualizar_cliente(cl);
            }
        });


        ImageButton bt_eliminar = (ImageButton) v.findViewById(R.id.cli_view_bt_eliminar);
        bt_eliminar.setOnClickListener(new View.OnClickListener() {//agrego accion al clicar sobre un cliente
            @Override
            public void onClick(View view) {
              set_mensage("Esta seguro de eliminar el registro?",cl,1);
            }
        });


        ImageButton bt_crear_prestamo = (ImageButton) v.findViewById(R.id.cli_view_bt_alta_prestamo);
        bt_crear_prestamo.setOnClickListener(new View.OnClickListener() {//agrego accion al clicar sobre un cliente
            @Override
            public void onClick(View view) {
                new prestamo_alta(mantenimiento_clientes.this).set_view_alta(cli);
            }
        });

        ImageButton bt_listar_prestamo = (ImageButton) v.findViewById(R.id.cli_view_bt_listar_prestamo);
        bt_listar_prestamo.setOnClickListener(new View.OnClickListener() {//agrego accion al clicar sobre un cliente
            @Override
            public void onClick(View view) {
                //codigo para listar los prestamos por cliente
            /*    Intent lanzadera = new Intent(mantenimiento_clientes.this,listado_prestamo.class);
                lanzadera.putExtra("id_cliente",cl.getId());
                lanzadera.putExtra("id_usuario",cl.getId_usuario());
                startActivity(lanzadera);*/

                Intent lanzadera = new Intent(mantenimiento_clientes.this,listado_prestamo.class);
                lanzadera.putExtra("cliente",cl);
                startActivity(lanzadera);
            }
        });

        return v;
    }

    private void set_mensage(String mensaje, Cliente c, int opcion){
        AlertDialog.Builder builder = new AlertDialog.Builder(mantenimiento_clientes.this);
        builder.setTitle("Notificacion ");
        builder.setMessage(mensaje);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(opcion==0) {// opcion 0 para modificar

                }else{// de lo contrario elimino
                    new Cliente_ctr(mantenimiento_clientes.this).set_eliminar(c);
                }
                Toast.makeText(mantenimiento_clientes.this,"Correcto",Toast.LENGTH_LONG).show();
                dialog.dismiss();
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


    private ArrayAdapter<String> proceso_spiner(){
        //lleno el combo tipos de documentos
        List<String> tipos_documento = new ArrayList<String>();
        tipos_documento.add("CED");
        tipos_documento.add("PSS");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mantenimiento_clientes.this,
                android.R.layout.simple_spinner_item, tipos_documento);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return dataAdapter;
    }


    private void set_visualizar_cliente(Cliente cli){
        LayoutInflater inflater = getLayoutInflater();
        View v_datos_cli = inflater.inflate(R.layout.formulario_cliente_alta, null);

        LinearLayout view_cliente = (LinearLayout) this.findViewById(R.id.clientes_modifica);
        view_cliente.removeAllViews();
        view_cliente.addView(v_datos_cli);
        view_cliente.setVisibility(View.VISIBLE);

        EditText txt_nombres = (EditText)  v_datos_cli.findViewById(R.id.cli_form_nombre);
        EditText txt_apellidos = (EditText)  v_datos_cli.findViewById(R.id.cli_form_apellidos);
        EditText txt_identificacion = (EditText)  v_datos_cli.findViewById(R.id.cli_form_identificacion);

        Spinner tipo_doc = (Spinner) v_datos_cli.findViewById(R.id.cli_form_tipos_identificacion);
        tipo_doc.setAdapter(proceso_spiner());
        if(cli.getPersona().getTipo_identificacion().equals("CED")){
            tipo_doc.setSelection(0);
        }else{
            tipo_doc.setSelection(1);
        }


        EditText txt_municipio = (EditText)  v_datos_cli.findViewById(R.id.cli_form_municipio);
        EditText txt_sector = (EditText)  v_datos_cli.findViewById(R.id.cli_form_sector);
        EditText txt_calle = (EditText)  v_datos_cli.findViewById(R.id.cli_form_calle);
        EditText txt_vivienda = (EditText)  v_datos_cli.findViewById(R.id.cli_form_vivienda);

        EditText txt_telefono = (EditText)  v_datos_cli.findViewById(R.id.cli_form_telefono);
        EditText txt_correo = (EditText)  v_datos_cli.findViewById(R.id.cli_form_correo);

        txt_nombres.setText(cli.getPersona().getNombres());
        txt_apellidos.setText(cli.getPersona().getApellidos());
        txt_identificacion.setText(cli.getPersona().getIdentificacion());
        txt_identificacion.setEnabled(false);

        txt_municipio.setText(cli.getPersona().getDireccion().getMunicipio());
        txt_sector.setText(cli.getPersona().getDireccion().getSector());
        txt_calle.setText(cli.getPersona().getDireccion().getCalle());
        txt_vivienda.setText(cli.getPersona().getDireccion().getVivienda());

        txt_telefono.setText(cli.getPersona().getContacto().getTelefono());
        txt_correo.setText(cli.getPersona().getContacto().getCorreo());

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

        txt_telefono.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(txt_telefono.getText().toString().length()==3){
                    txt_telefono.setText(txt_telefono.getText().toString()+"-");
                    txt_telefono.setSelection(4);
                }else if(txt_telefono.getText().toString().length()==7){
                    txt_telefono.setText(txt_telefono.getText().toString()+"-");
                    txt_telefono.setSelection(8);
                }
                return false;
            }
        });

        Button bt_cancelar = (Button)  v_datos_cli.findViewById(R.id.cli_form_cancelar);
        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_cliente.setVisibility(View.GONE);
            }
        });


        Button bt_guardar = (Button)  v_datos_cli.findViewById(R.id.cli_form_guardar);
        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_cliente.setVisibility(View.GONE);

                cli.set_datos_ultima_modificaion(mantenimiento_clientes.this);
                cli.getPersona().set_datos_ultima_modificaion(mantenimiento_clientes.this);
                cli.getPersona().getDireccion().set_datos_ultima_modificaion(mantenimiento_clientes.this);
                cli.getPersona().getContacto().set_datos_ultima_modificaion(mantenimiento_clientes.this);

                cli.getPersona().setNombres(txt_nombres.getText().toString());
                cli.getPersona().setApellidos(txt_apellidos.getText().toString());
                cli.getPersona().setIdentificacion(txt_identificacion.getText().toString());
                cli.getPersona().setTipo_identificacion(String.valueOf(tipo_doc.getSelectedItem()));

                cli.getPersona().getDireccion().setMunicipio(txt_municipio.getText().toString());
                cli.getPersona().getDireccion().setSector(txt_sector.getText().toString());
                cli.getPersona().getDireccion().setCalle(txt_calle.getText().toString());
                cli.getPersona().getDireccion().setVivienda(txt_vivienda.getText().toString());

                cli.getPersona().getContacto().setTelefono(txt_telefono.getText().toString());
                cli.getPersona().getContacto().setCorreo(txt_correo.getText().toString());

                new Cliente_ctr(mantenimiento_clientes.this).set_cliente(cli);
            }
        });


    }

    public void  onSaveInstanceState(Bundle bundle){

        super.onSaveInstanceState(bundle);
    }

    public void onRestoreInstanceState(Bundle bundle){

        super.onRestoreInstanceState(bundle);
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
                break;
        }

        return super.onOptionsItemSelected(opcion_menu);
    }

}