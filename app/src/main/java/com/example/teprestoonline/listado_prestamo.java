package com.example.teprestoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.teprestoonline.utilidades.PDF_MAnager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class listado_prestamo extends AppCompatActivity {


    private LinearLayout listado;
    private Cliente cliente = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_prestamo);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listado = (LinearLayout) findViewById(R.id.listado_prestamos_lista);

      //  Bundle datos = getIntent().getExtras();
        Intent datos;
        datos =  getIntent();

        if(datos!=null){
            cliente = (Cliente) datos.getSerializableExtra("cliente");
         //  set_listar_pretamos_cliente(datos.getString("id_cliente"),datos.getString("id_usuario"));
           set_listar_pretamos_cliente(cliente.getId(),cliente.getId_usuario());
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
                    listado.removeAllViews(); //limpio el conrtenedor
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
        EditText txt_por_atrz = (EditText) v.findViewById(R.id.prestamo_view_porct_atrazo);
        EditText txt_mont_atrz = (EditText) v.findViewById(R.id.prestamo_view_mnt_fijo_atraz);

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
        txt_cuota.setText(""+Math.round(p.getCuota()));
        txt_restante.setText(""+p.getRestante());
        txt_fecultcob.setText(""+p.getFecha_ult_cobro());
        txt_fecultpag.setText(""+p.getFecha_ult_pago());
        txt_mont_atrz.setText(""+p.getMonto_fijo());
        txt_por_atrz.setText(""+p.getPorcentage_atrazo());


        ImageButton bt_modificar = (ImageButton) v.findViewById(R.id.prestamo_view_bt_modficar);
        ImageButton bt_pagar = (ImageButton) v.findViewById(R.id.prestamo_view_bt_pagar);
        ImageButton bt_eliminar = (ImageButton) v.findViewById(R.id.prestamo_view_bt_eliminar);
        ImageButton bt_listar_amortiz = (ImageButton) v.findViewById(R.id.prestamo_view_bt_listar_amort);
        ImageButton bt_ver_contrato = (ImageButton) v.findViewById(R.id.prestamo_vie_bt_contrato);
        Button bt_hist_pagos = (Button) v.findViewById(R.id.prestamo_view_hist_pagos);

        if(!p.getContrato_ruta().isEmpty()){
            bt_ver_contrato.setVisibility(View.VISIBLE);
        }

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
                if(!txt_montofin.isEnabled()) {
                    txt_montofin.setEnabled(true);
                    txt_tasa.setEnabled(true);
                    txt_cantcuorest.setEnabled(true);
                    txt_cantcuototal.setEnabled(true);
                    txt_restante.setEnabled(true);
                    txt_fecultcob.setEnabled(true);
                    selector_perido.setEnabled(true);
                    txt_mont_atrz.setEnabled(true);
                    txt_por_atrz.setEnabled(true);
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
                        if(!txt_por_atrz.getText().toString().isEmpty()) {
                            p.setPorcentage_atrazo(Integer.parseInt(txt_por_atrz.getText().toString()));
                        }else{
                            p.setPorcentage_atrazo(0);
                        }

                        if(!txt_mont_atrz.getText().toString().isEmpty()) {
                            p.setMonto_fijo(Integer.parseInt(txt_mont_atrz.getText().toString()));
                        }else{
                            p.setMonto_fijo(0);
                        }
                    }

                    set_mensage("esta seguro de realizar los cambios?",p,0);
                    txt_montofin.setEnabled(false);
                    txt_tasa.setEnabled(false);
                    txt_cantcuorest.setEnabled(false);
                    txt_cantcuototal.setEnabled(false);
                    txt_restante.setEnabled(false);
                    txt_fecultcob.setEnabled(false);
                    selector_perido.setEnabled(false);
                    txt_mont_atrz.setEnabled(false);
                    txt_por_atrz.setEnabled(false);

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
                   set_listado_amortizaciones(p, v);
                   ln_listado_amor.setVisibility(View.VISIBLE);
               }
            }
        });

        bt_pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // set_mensage("Desea realizar el pago?",p,9);
                Intent lanzadera = new Intent(listado_prestamo.this,Pago.class);
                lanzadera.putExtra("prestamo",p);
                lanzadera.putExtra("cliente",cliente);
                startActivity(lanzadera);
            }
        });

        bt_hist_pagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ln_listado_amor.getVisibility() == View.VISIBLE){
                    ln_listado_amor.setVisibility(View.GONE);
                }else {
                    set_listado_pagos(p,v);
                    ln_listado_amor.setVisibility(View.VISIBLE);
                }
            }
        });


        bt_ver_contrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PDF_MAnager pdf_controler = new PDF_MAnager(listado_prestamo.this);
                pdf_controler.set_abrir_documento(p.getContrato_ruta());

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
            double cuota = Math.round( restante  / p.getCantidad_cuotas() );
            double interes_cuota = (double) (ganancia) / p.getCantidad_cuotas() ;
            double capital_cuota =  (double) (monto) / p.getCantidad_cuotas();

            p.setInteres_cuota(interes_cuota);
            p.setCapital_cuota(capital_cuota);
            p.setCuota(cuota);
            p.setRestante(restante);
        }
        new Prestamo_ctr().set_prestamo(p);
        if(p.getTipo()==1){
            new Prestamo_ctr().set_proceso_amortizaciones(p);
        }
      //  set_listar_pretamos_cliente(p.getId_cliente(),p.getId_usuario());
    }

    private void set_mensage(String mensaje,Prestamo p,int opcion){
        AlertDialog.Builder builder = new AlertDialog.Builder(listado_prestamo.this);
        builder.setTitle("Notificacion ");
        builder.setMessage(mensaje);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(opcion==0) {// opcion 0 para modificar
                    p.setContrato_ruta("");//limpio el ultimo contrato
                    set_proceso_refinanciamiento(p);
                    Toast.makeText(listado_prestamo.this,"Correcto",Toast.LENGTH_LONG).show();
                }else if(opcion==1) {// lo elimino
                    new Prestamo_ctr().set_eliminar(p);
                    Toast.makeText(listado_prestamo.this,"Correcto",Toast.LENGTH_LONG).show();
                }else if(opcion==9) {// Mando el pago
                    Intent lanzadera = new Intent(listado_prestamo.this,Pago.class);
                    lanzadera.putExtra("prestamo",p);
                    startActivity(lanzadera);
                }

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
        TextView encabezado = (TextView) v.findViewById(R.id.prestamo_view_amortizaciones_encabezado);
        encabezado.setText("AMORTIZACIONES");

        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    listado_amorizaciones.removeAllViews();//limpio el contenedor
                    TableLayout tabla = new TableLayout(v.getContext());
                    tabla.setStretchAllColumns(true);
                    tabla.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));


                    String[] titulos = {"FECVEN","FECPAGO","ESTADO","CUOTA","ATRAZO"};

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
                                        label.setText(""+Math.round(amortz.getCuota()));
                                        break;
                                    case 4:
                                        double atrazo =  Math.ceil(
                                                amortz.getCapital()+amortz.getInteres()
                                                ) - amortz.getCuota();

                                        if(atrazo < 0){
                                            atrazo=0;
                                        }
                                        label.setText(""+atrazo);
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


    private void set_listado_pagos(Prestamo p,View v){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child(Prestamo_ctr.BBDD_NAME3).child(p.getId());
        final Query usuQuery = ref.orderByChild("fecha_alta_unix");
        LinearLayout listado_amorizaciones = (LinearLayout) v.findViewById(R.id.prestamo_view_lista_amortizaciones);
        TextView encabezado = (TextView) v.findViewById(R.id.prestamo_view_amortizaciones_encabezado);
        encabezado.setText("HISTORICO PAGOS");

        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    listado_amorizaciones.removeAllViews();//limpio el contenedor
                    TableLayout tabla = new TableLayout(v.getContext());
                    tabla.setStretchAllColumns(true);
                    tabla.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    String[] titulos = {"FECPAGO","MONTO","CAPAMORT","INTAMORT","RECIBO"};

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

                        if(hijo.getValue(com.example.teprestoonline.Modelo.Pago.class).getId_prestamo().equalsIgnoreCase(p.getId())) {
                            com.example.teprestoonline.Modelo.Pago pago
                                    = hijo.getValue(com.example.teprestoonline.Modelo.Pago.class);

                            TableRow lnX2 = new TableRow(v.getContext());
                            lnX2.setOrientation(LinearLayout.HORIZONTAL);

                            for(int i=0;i<titulos.length;i++) {
                                TextView label = new TextView(v.getContext());
                                switch (i){
                                    case 0:
                                        label.setText(pago.getFecha_pago());
                                        break;
                                    case 1:
                                        label.setText(""+pago.getMonto_pagado());
                                        break;
                                    case 2:
                                        label.setText(""+Math.round(pago.getCapital_amortizado()));
                                        break;
                                    case 3:
                                        label.setText(""+Math.round(pago.getInteres_amortizado()));
                                        break;
                                    case 4:
                                        if(!pago.getRecibo_rutta().isEmpty()){
                                            Button bt = new Button(v.getContext());
                                            bt.setText("*");
                                            bt.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    PDF_MAnager manejador = new PDF_MAnager(listado_prestamo.this);
                                                    manejador.set_abrir_documento(pago.getRecibo_rutta());
                                                }
                                            });

                                            lnX2.addView(bt);
                                        }

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