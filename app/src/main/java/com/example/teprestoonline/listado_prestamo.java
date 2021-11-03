package com.example.teprestoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class listado_prestamo extends AppCompatActivity {


    private LinearLayout listado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_prestamo);

        listado = (LinearLayout) findViewById(R.id.listado_prestamos_lista);

        Bundle datos = getIntent().getExtras();
        if(datos!=null){
           set_listar_pretamos_cliente(datos.getString("id_cliente"),datos.getString("id_usuario"));
        }

    }

    private void set_listar_pretamos_cliente(String id_cliente,String id_usuario){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child(Prestamo_ctr.BBDD_NAME);
        final Query usuQuery = ref.orderByChild("id_cliente").equalTo(id_cliente);

        usuQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    listado.removeAllViews(); //limpio el conrtenedor
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        if(
                                hijo.getValue(Prestamo.class).getId_cliente().equalsIgnoreCase(id_cliente)
                                        &&
                                        hijo.getValue(Prestamo.class).getId_usuario().equalsIgnoreCase(id_usuario)
                        ) {
                            Prestamo p = hijo.getValue(Prestamo.class);
                            listado.addView(get_prestamo_view(p));
                        }
                    }
                }else{
                    Toast.makeText(listado_prestamo.this,"No se encontraron datos",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void set_listar_pretamos_cliente(String id_usuario){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child(Prestamo_ctr.BBDD_NAME);
        final Query usuQuery = ref.orderByChild("id_usuario").equalTo(id_usuario);

        usuQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    listado.removeAllViews(); //limpio el conrtenedor
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        if(hijo.getValue(Prestamo.class).getId_usuario().equalsIgnoreCase(id_usuario)) {
                            Prestamo p = hijo.getValue(Prestamo.class);
                            listado.addView(get_prestamo_view(p));
                        }
                    }
                }else{
                    Toast.makeText(listado_prestamo.this,"No se encontraron datos",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    protected View get_prestamo_view(Prestamo p){
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.prestamo_view, null);

        EditText txt_tipo = (EditText) v.findViewById(R.id.prestamo_view_tipo);
        EditText txt_fecalta = (EditText) v.findViewById(R.id.prestamo_view_fecalta);
        EditText txt_montofin = (EditText) v.findViewById(R.id.prestamo_view_monto_financiado);
        EditText txt_tasa = (EditText) v.findViewById(R.id.prestamo_view_tasa);
        EditText txt_cantcuorest = (EditText) v.findViewById(R.id.prestamo_view_cantcuorest);
        EditText txt_cantcuototal = (EditText) v.findViewById(R.id.prestamo_view_cantcuo);
        EditText txt_cuota = (EditText) v.findViewById(R.id.prestamo_view_cuota);
        EditText txt_restante = (EditText) v.findViewById(R.id.prestamo_view_restante);
        EditText txt_fecultcob = (EditText) v.findViewById(R.id.prestamo_view_fecultcobro);
        EditText txt_fecultpag = (EditText) v.findViewById(R.id.prestamo_view_fecultpago);
        Spinner selector_perido = (Spinner) v.findViewById(R.id.prestamo_view_periodo);
        selector_perido.setAdapter(proceso_spiner());
        selector_perido.setEnabled(false);

        switch (p.getPeriodo()){
            case 1:
                selector_perido.setSelection(0);
                break;
            case 7:
                selector_perido.setSelection(1);
                break;
            case 15:
                selector_perido.setSelection(2);
                break;
            default:
                selector_perido.setSelection(3);
                break;
        }


        switch (p.getTipo()){
            case 0:
                txt_tipo.setText("REGULAR");
                break;
            default:
                txt_tipo.setText("CUOTAS");
                break;
        }

        txt_fecalta.setText(p.getFecha_alta_humana());
        txt_montofin.setText(""+p.getMonto_financiado());
        txt_tasa.setText(""+p.getTasa());
        txt_cantcuorest.setText(""+p.getCantida_cuotas_restantes());
        txt_cantcuototal.setText(""+p.getCantidad_cuotas());
        txt_cuota.setText(""+p.getCuota());
        txt_restante.setText(""+p.getRestante());
        txt_fecultcob.setText(""+p.getFecha_ult_cobro());
        txt_fecultpag.setText(""+p.getFecha_ult_pago());


        ImageButton bt_modificar = (ImageButton) v.findViewById(R.id.prestamo_view_bt_modficar);
        ImageButton bt_eliminar = (ImageButton) v.findViewById(R.id.prestamo_view_bt_eliminar);

        bt_modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   contenedor.setVisibility(View.VISIBLE);
                if(!txt_montofin.isEnabled()) {
                    txt_montofin.setEnabled(true);
                    txt_tasa.setEnabled(true);
                    txt_cantcuorest.setEnabled(true);
                    txt_cantcuototal.setEnabled(true);
                    txt_cuota.setEnabled(true);
                    txt_restante.setEnabled(true);
                    txt_fecultcob.setEnabled(true);
                    txt_fecultpag.setEnabled(true);
                    selector_perido.setEnabled(true);
                }else{
                    //seteo los valores del formulario a la instancia del prestamo apra modificar
                    p.setTasa(Integer.parseInt(txt_tasa.getText().toString()));
                    p.setRestante(Double.parseDouble(txt_restante.getText().toString()));
                    p.setMonto_financiado(Double.parseDouble(txt_montofin.getText().toString()));
                    switch (selector_perido.getSelectedItemPosition()){
                        case 0:
                            p.setPeriodo(1);
                            break;
                        case 1:
                            p.setPeriodo(7);
                            break;
                        case 2:
                            p.setPeriodo(15);
                            break;
                        case 3:
                            p.setPeriodo(30);
                            break;
                    }
                    p.setFecha_ult_pago(txt_fecultpag.getText().toString());
                    p.setFecha_ult_cobro(txt_fecultcob.getText().toString());

                    set_mensage("esta seguro de realizar los cambios?",p,0);
                    txt_montofin.setEnabled(false);
                    txt_tasa.setEnabled(false);
                    txt_cantcuorest.setEnabled(false);
                    txt_cantcuototal.setEnabled(false);
                    txt_cuota.setEnabled(false);
                    txt_restante.setEnabled(false);
                    txt_fecultcob.setEnabled(false);
                    txt_fecultpag.setEnabled(false);
                    selector_perido.setEnabled(false);
                    set_listar_pretamos_cliente(p.getId_cliente(),p.getId_usuario());
                }

            }
        });

        bt_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_mensage("Esta seguro de eliminar el registro?",p,1);
            }
        });

        return v;
    }

    private void set_mensage(String mensaje,Prestamo p,int opcion){
        AlertDialog.Builder builder = new AlertDialog.Builder(listado_prestamo.this);
        builder.setTitle("Notificacion ");
        builder.setMessage(mensaje);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(opcion==0) {// opcion 0 para modificar
                    new Prestamo_ctr().set_prestamo(p);
                }else{// de lo contrario elimino
                    new Prestamo_ctr().set_eliminar(p);
                }
                Toast.makeText(listado_prestamo.this,"Correcto",Toast.LENGTH_LONG).show();
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
        tipos_documento.add("DIARIO");
        tipos_documento.add("SEMANAL");
        tipos_documento.add("QUINCENAL");
        tipos_documento.add("MENSUAL");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(listado_prestamo.this,
                android.R.layout.simple_spinner_item, tipos_documento);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return dataAdapter;
    }

}