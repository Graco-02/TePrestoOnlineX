package com.example.teprestoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Prestamo_ctr;
import com.example.teprestoonline.Controladores.Usuario_ctr;
import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.Modelo.Usuario;
import com.example.teprestoonline.Modelo.amortizacion_cuota;
import com.example.teprestoonline.utilidades.Proceso_cobro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Inicio extends AppCompatActivity {

    private Usuario usu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // indico que esta pantalla solo puede visializarse conla pantalla vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_inicio);

        if(Usuario.usuario_logueado!=null) {
            TextView txt_pie;
            txt_pie = (TextView) findViewById(R.id.inicio_pie);
            txt_pie.setText("Saludos Sr. " + Usuario.usuario_logueado.getUsuario());
        }

    }

    public void  onSaveInstanceState(Bundle bundle){

        bundle.putString("nombre",Usuario.usuario_logueado.getUsuario());
        bundle.putString("clave",Usuario.usuario_logueado.getClave());
        bundle.putString("id",Usuario.usuario_logueado.getId());
        bundle.putString("fecha_humana",Usuario.usuario_logueado.getFecha_alta_humana());
        bundle.putLong("fecha_unix",Usuario.usuario_logueado.getFecha_unix());

        super.onSaveInstanceState(bundle);
    }

    public void onRestoreInstanceState(Bundle bundle){

        super.onRestoreInstanceState(bundle);

        usu = new Usuario();
        usu.setId(bundle.getString("id"));
        usu.setUsuario(bundle.getString("nombre"));
        usu.setClave(bundle.getString("clave"));
        usu.setFecha_alta_humana(bundle.getString("fecha_humana"));
        usu.setFecha_unix(bundle.getLong("fecha_unix"));

        Usuario.usuario_logueado = usu;
        TextView txt_pie;
        txt_pie = (TextView) findViewById(R.id.inicio_pie);
        txt_pie.setText("Saludos Sr. " + Usuario.usuario_logueado.getUsuario());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_inicio,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem opcion_menu){
        int id = opcion_menu.getItemId();

        switch (id){
            case R.id.inicio_clientes:
                Intent lanzadera = new Intent(this,mantenimiento_clientes.class);
                startActivity(lanzadera);
                break;
            case R.id.inicio_reporte:

                break;
            case R.id.inicio_parametro:
                break;

            case R.id.inicio_usuario:
                new usuario_modifica_ventana().get_acceso(this,Usuario.usuario_logueado);
                break;
            case R.id.salir:
                Usuario.usuario_logueado = null;
                finish();
                break;
        }


        return super.onOptionsItemSelected(opcion_menu);
    }

    public void set_mensage(View v ){
        AlertDialog.Builder builder = new AlertDialog.Builder(Inicio.this);
        builder.setTitle("Notificacion ");
        builder.setMessage("Desea lanzar el proceso de actualizacion de saldos?");
        LinearLayout ln = (LinearLayout) findViewById(R.id.inicio_proceso_view);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ln.setVisibility(View.VISIBLE);
                set_proceso_cobro();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ln.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void set_proceso_cobro(){
        FirebaseDatabase database = null;
        final DatabaseReference myRef;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Prestamo_ctr.BBDD_NAME);
        ArrayList<Prestamo> lista_prestamos =  new ArrayList<>();
        LinearLayout ln = (LinearLayout) findViewById(R.id.inicio_pantalla_proceso);
        Proceso_cobro procesamiento = new Proceso_cobro(Inicio.this);

        final Query usuQuery = myRef.orderByChild("id");
        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        Prestamo p = hijo.getValue(Prestamo.class);
                        switch (p.getTipo()){
                            case 0:
                                procesamiento.set_validaciones_prestamos_regulares(p);
                                break;
                            case 1:
                                procesamiento.set_validaciones_prestamos_cuotas(p);
                                break;
                        }
                    }
                }else{
                    Toast.makeText(Inicio.this,"No se encontraron datos",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void set_amortizaciones(Prestamo p){
        ArrayList<amortizacion_cuota> lista_amortizaciones =  new ArrayList<>();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child(Prestamo_ctr.BBDD_NAME2).child(p.getId());
        final Query usuQuery = ref.orderByChild("fecha_alta_unix");
        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        if(hijo.getValue(amortizacion_cuota.class).getId_prestamo().equalsIgnoreCase(p.getId())) {
                            amortizacion_cuota amortz = hijo.getValue(amortizacion_cuota.class);
                            lista_amortizaciones.add(amortz);
                        }
                    }
                }else{
                   // Toast.makeText(Inicio.this,"No se encontraron datos",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}