package com.example.teprestoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Cliente_ctr;
import com.example.teprestoonline.Controladores.Prestamo_ctr;
import com.example.teprestoonline.Modelo.Cliente;
import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.Modelo.Usuario;
import com.example.teprestoonline.Modelo.amortizacion_cuota;
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
                listado.removeAllViews(); //limpio el conrtenedor
                if(dataSnapshot.exists()) {
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
        ImageButton bt_listar_amortiz = (ImageButton) v.findViewById(R.id.prestamo_view_bt_listar_amort);
        LinearLayout ln_listado_amor = (LinearLayout) v.findViewById(R.id.prestamo_view_amorizaciones);
        LinearLayout ln_datos_cuotas = (LinearLayout) v.findViewById(R.id.prestamo_view_datos_cuotas);

        switch (p.getTipo()){
            case 0:
                txt_tipo.setText("REGULAR");
                break;
            default:
                txt_tipo.setText("CUOTAS");
                bt_listar_amortiz.setVisibility(View.VISIBLE);
                ln_datos_cuotas.setVisibility(View.VISIBLE);
                break;
        }

        bt_modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   contenedor.setVisibility(View.VISIBLE);
                if(!txt_montofin.isEnabled()) {
                    txt_montofin.setEnabled(true);
                    txt_tasa.setEnabled(true);
                    txt_cantcuorest.setEnabled(true);
                    txt_cantcuototal.setEnabled(true);
                 //   txt_cuota.setEnabled(true);
                    txt_restante.setEnabled(true);
                    txt_fecultcob.setEnabled(true);
               //     txt_fecultpag.setEnabled(true);
                    selector_perido.setEnabled(true);
                }else{
                    //seteo los valores del formulario a la instancia del prestamo para modificar
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
                    if(p.getTipo()==1){
                        p.setCantida_cuotas_restantes(Integer.parseInt(txt_cantcuorest.getText().toString()));
                        p.setCantidad_cuotas(Integer.parseInt(txt_cantcuototal.getText().toString()));
                    }

                    set_mensage("esta seguro de realizar los cambios?",p,0);
                    txt_montofin.setEnabled(false);
                    txt_tasa.setEnabled(false);
                    txt_cantcuorest.setEnabled(false);
                    txt_cantcuototal.setEnabled(false);
                  //  txt_cuota.setEnabled(false);
                    txt_restante.setEnabled(false);
                    txt_fecultcob.setEnabled(false);
                  //  txt_fecultpag.setEnabled(false);
                    selector_perido.setEnabled(false);

                }

            }
        });

        bt_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_mensage("Esta seguro de eliminar el registro?",p,1);
            }
        });


        bt_listar_amortiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(ln_listado_amor.getVisibility() == View.VISIBLE){
                   ln_listado_amor.setVisibility(View.GONE);
               }else {
                   ln_listado_amor.setVisibility(View.VISIBLE);
                   set_listado_amortizaciones(p, v);
               }
            }
        });

        return v;
    }

    private void set_proceso_refinanciamiento(Prestamo p){
        new Prestamo_ctr().set_eliminar(p);//elimino el prestamo anterior y refinancio
        if(p.getTipo()==1){
            p.setTipo(1); // tipo 1 = Cuotas

            double monto = p.getMonto_financiado();
            double tasa = Double.parseDouble(""+p.getTasa()) / 100 ;
            int ganancia = Integer.parseInt(String.valueOf(Math.round((monto * tasa))));
            double restante = monto + ganancia;
            double cuota = restante  / p.getCantidad_cuotas();
            double interes_cuota = (double) (ganancia) / p.getCantidad_cuotas();
            double capital_cuota = (double) (monto) / p.getCantidad_cuotas();

            p.setInteres_cuota(interes_cuota);
            p.setCapital_cuota(capital_cuota);
            p.setCuota(cuota);
            p.setRestante(restante);
        }
        new Prestamo_ctr().set_prestamo(p);
        if(p.getTipo()==1){
            new Prestamo_ctr().set_proceso_amortizaciones(p);
        }
        set_listar_pretamos_cliente(p.getId_cliente(),p.getId_usuario());
    }

    private void set_mensage(String mensaje,Prestamo p,int opcion){
        AlertDialog.Builder builder = new AlertDialog.Builder(listado_prestamo.this);
        builder.setTitle("Notificacion ");
        builder.setMessage(mensaje);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(opcion==0) {// opcion 0 para modificar
                    set_proceso_refinanciamiento(p);
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


    private void set_listado_amortizaciones(Prestamo p,View v){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child(Prestamo_ctr.BBDD_NAME2).child(p.getId());
        final Query usuQuery = ref.orderByChild("fecha_alta_unix");
        LinearLayout listado_amorizaciones = (LinearLayout) v.findViewById(R.id.prestamo_view_lista_amortizaciones);

        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    listado_amorizaciones.removeAllViews();//limpio el contenedor
                    TableLayout tabla = new TableLayout(v.getContext());
                    tabla.setStretchAllColumns(true);
                    tabla.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    String[] titulos = {"FECHA VENCE","FECHA PAGO","ESTADO","CUOTA"};

                    TableRow ln_emcabezados = new TableRow(v.getContext());
                    ln_emcabezados.setBackgroundColor(Color.GREEN);

                    for(int i=0;i<titulos.length;i++) {

                        TextView label = new TextView(v.getContext());
                        label.setText(titulos[i]);
                        label.setPadding(5,5,5,5);
                        ln_emcabezados.addView(label);
                    }

                    tabla.addView(ln_emcabezados);
                    listado_amorizaciones.addView(tabla);

                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {

                        if(hijo.getValue(amortizacion_cuota.class).getId_prestamo().equalsIgnoreCase(p.getId())) {
                            amortizacion_cuota amortz = hijo.getValue(amortizacion_cuota.class);
                            TableRow lnX2 = new TableRow(v.getContext());
                            lnX2.setOrientation(LinearLayout.HORIZONTAL);

                            for(int i=0;i<titulos.length;i++) {
                                TextView label = new TextView(v.getContext());
                                switch (i){
                                    case 0:
                                        label.setText(amortz.getFecha_cuota());
                                        break;
                                    case 1:
                                        label.setText(amortz.getFecha_pago());
                                        break;
                                    case 2:
                                        label.setText(amortz.getEstado_descripcion());
                                        break;
                                    case 3:
                                        label.setText(""+amortz.getCuota());
                                        break;
                                }

                                lnX2.addView(label);
                            }
                            tabla.addView(lnX2);
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


}