package com.example.teprestoonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Prestamo_ctr;
import com.example.teprestoonline.Modelo.Cliente;
import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.Modelo.amortizacion_cuota;
import com.example.teprestoonline.utilidades.Fecha_utiliti;
import com.example.teprestoonline.utilidades.PDF_MAnager;
import com.example.teprestoonline.utilidades.Proceso_cobro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Pago extends AppCompatActivity {

    private String id_pago;
    private int tipo;
    private double monto_restante;
    private String fecha_ult_pago;
    private String id_usuario;
    private String id_cliente;
    private EditText txt_monto_pago;
    private com.example.teprestoonline.Modelo.Pago mipago;
    private Prestamo prestamo;
    private EditText txt_fecha;
    private double monto_amortizar;
    private double capital_amortizado;
    private double interes_amortizado;
    private CheckBox opcion_imp;
    private CheckBox opcion_pdf;
    private Cliente cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        Intent datos;
        datos =  getIntent();

        txt_monto_pago = (EditText) findViewById(R.id.pago_monto_pago);
        txt_fecha =  (EditText) findViewById(R.id.pago_fecha);
        EditText txt_fecha_ult_pago =  (EditText) findViewById(R.id.pago_fecha_ult_pago);
        EditText txt_monto_rest =  (EditText) findViewById(R.id.pago_monto_restante);
        EditText txt_tipo =  (EditText) findViewById(R.id.pago_tipo_prestamo);
        EditText txt_cantcuores =  (EditText) findViewById(R.id.pago_cantcuorest);
        EditText txt_cantcuotot =  (EditText) findViewById(R.id.pago_cantcuo);
        EditText txt_cuota =  (EditText) findViewById(R.id.pago_cuota);
        opcion_imp = (CheckBox) findViewById(R.id.pago_cheq_imp_recibo);
        opcion_pdf = (CheckBox) findViewById(R.id.pago_cheq_pdf);

        if(datos!=null){
            prestamo = (Prestamo) datos.getSerializableExtra("prestamo");
            cliente = (Cliente) datos.getSerializableExtra("cliente");

            if(prestamo.getTipo()==1){
                LinearLayout lycuotas = (LinearLayout) findViewById(R.id.pago_datos_cuotas);
                lycuotas.setVisibility(View.VISIBLE);
            }


            monto_restante = prestamo.getRestante();

            double interes = new Proceso_cobro().get_interes(prestamo);
            String fecha = "";

            if(!prestamo.getFecha_ult_cobro().equals("0001-01-01")) {
                fecha = prestamo.getFecha_ult_cobro();
            }else{
               fecha = new Fecha_utiliti().getFechaSystemaYYMMDD();
            }

            int tiempo_dias = new Fecha_utiliti().getTiempo_Transcurido_DIAS_YYYYMMDD(fecha);
            if(tiempo_dias>=prestamo.getPeriodo()) {
                interes *= tiempo_dias;
            }else{
                interes *= prestamo.getPeriodo();
            }

            monto_restante+=interes;

            id_pago = prestamo.getId();
            tipo = prestamo.getTipo();
            fecha_ult_pago =  prestamo.getFecha_ult_pago();
            id_usuario =  prestamo.getId_usuario();
            id_cliente =  prestamo.getId_cliente();

            mipago = new com.example.teprestoonline.Modelo.Pago();
            mipago.set_datos_unicos();
            mipago.set_datos_ultima_modificaion();
            mipago.setId_cliente(id_cliente);
            mipago.setId_usuario(id_usuario);
            mipago.setId_prestamo(id_pago);
            mipago.setTipo(txt_tipo.getText().toString());

            switch (tipo){
                case 0:
                    txt_tipo.setText("REGULAR");
                    mipago.setMonto_restante(Math.round(monto_restante));
                    prestamo.setRestante(Math.round(monto_restante));
                    break;
                default:
                    txt_tipo.setText("CUOTAS");
                    txt_cantcuores.setText(""+prestamo.getCantida_cuotas_restantes());
                    txt_cantcuotot.setText(""+prestamo.getCantidad_cuotas());
                    txt_cuota.setText(""+Math.round(prestamo.getCuota()));
                    mipago.setMonto_restante(Math.round(prestamo.getRestante()));
                    break;
            }

            txt_monto_rest.setText(""+mipago.getMonto_restante());
            txt_fecha.setText(new Fecha_utiliti().getFechaSystemaYYMMDD());
            txt_fecha_ult_pago.setText(fecha_ult_pago);
        }

        Button bt_pagar = (Button) findViewById(R.id.pago_bt_pagar);
        bt_pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_mensage("Desea realizar el pago?");
            }
        });

    }

    private void set_mensage(String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(Pago.this);
        builder.setTitle("Notificacion ");
        builder.setMessage(mensaje);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(!txt_monto_pago.getText().toString().isEmpty()
                   && Double.parseDouble(txt_monto_pago.getText().toString()) > 0
                ) {
                    mipago.setMonto_pagado(Double.parseDouble(txt_monto_pago.getText().toString()));
                    mipago.setFecha_pago(txt_fecha.getText().toString());

                    set_proceso_amortiza_pago();

                    Toast.makeText(Pago.this,"Prestamo  Realizado  ",Toast.LENGTH_LONG).show();

                }else{
                   txt_monto_pago.setError("Se debe informar mayor a 0");
                   txt_monto_pago.requestFocus();
                }

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

    
    protected void  set_proceso_amortiza_pago(){
        prestamo.set_datos_ultima_modificaion();
        prestamo.setFecha_ult_pago(txt_fecha.getText().toString());
        prestamo.setRestante(
                prestamo.getRestante() - mipago.getMonto_pagado()
        );

        //proceso creacion del recibo enPDF si el cheq esta seleccionado
        if(opcion_pdf.isChecked()){
            PDF_MAnager manejador_pdf = new PDF_MAnager(this);
            String nombre_recibo = mipago.getId()+mipago.getFecha_pago()+".pdf";
            manejador_pdf.set_proceso_generar_recibo(nombre_recibo,prestamo,cliente,mipago);
            mipago.setRecibo_rutta(manejador_pdf.getArchivo_pdf().getAbsolutePath().toString());
        }

        if(prestamo.getTipo()==1){
            monto_amortizar = mipago.getMonto_pagado();
            set_amortizaciones_cuotas(prestamo);
        }else{
            mipago.setTipo("R");
            new Prestamo_ctr().set_pago(mipago);
            new Prestamo_ctr().set_prestamo(prestamo); //actualizo los datos del prestamo
        }

        finish();
    }

    private void set_amortizaciones_cuotas(Prestamo p){
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
                            //validar si la cuota no ha sido pagada ya
                            if(amortz.getEstado()!=2 && monto_amortizar > 0) {

                                monto_amortizar=Math.round(monto_amortizar);
                                double capital = Math.round(amortz.getCapital());
                                double interes = Math.round(amortz.getInteres());
                                double cuota= amortz.getCuota();

                                double total = capital + interes;
                                if(total < cuota){
                                    capital = Math.ceil(amortz.getCapital());
                                    total = capital + interes;
                                    if(total < cuota){
                                        interes = Math.ceil(amortz.getInteres());
                                    }
                                }

                                interes_amortizado = 0;
                                capital_amortizado = 0;

                                System.out.println("********** AMORTIZANDO PAGO *************");
                                System.out.println("Fecha cuota  . :" + amortz.getFecha_cuota());
                                System.out.println("Monto . :" + monto_amortizar);
                                System.out.println("interes . :" + interes);
                                System.out.println("capital . :" + capital);
                                System.out.println("cuota . :" + cuota);
                                System.out.println("**********************");

                                if(interes <= monto_amortizar){
                                    interes_amortizado =  interes;
                                    monto_amortizar = monto_amortizar - interes;
                                    interes = 0;
                                    cuota = cuota - interes_amortizado;
                                }else{
                                    if(monto_amortizar > 0) {
                                        interes_amortizado = monto_amortizar;
                                        monto_amortizar = 0;
                                        interes = interes - interes_amortizado;
                                        cuota = cuota - interes_amortizado;
                                    }
                                }

                                if(capital<=monto_amortizar){
                                    capital_amortizado = capital;
                                    monto_amortizar = monto_amortizar - capital_amortizado;
                                    capital = 0;
                                    cuota = cuota - capital_amortizado;
                                }else{
                                    if(monto_amortizar > 0) {
                                        capital_amortizado = monto_amortizar;
                                        monto_amortizar = 0;
                                        capital = capital - capital_amortizado;
                                        cuota = cuota - capital_amortizado;
                                    }
                                }

                                if(capital < 1){
                                    capital=0;
                                }

                                if(interes < 1){
                                    interes = 0;
                                }

                                if(cuota < 1){
                                    cuota = 0;
                                }

                                amortz.setCapital(capital);
                                amortz.setInteres(interes);
                                amortz.setCuota(cuota);

                                System.out.println("mipago.getCapital_amortizado() . :" + mipago.getCapital_amortizado());
                                System.out.println("getInteres_amortizado() . :" + mipago.getInteres_amortizado());
                                System.out.println("capital_amortizado . :" + capital_amortizado);
                                System.out.println("interes_amortizado . :" + interes_amortizado);
                                mipago.setCapital_amortizado(mipago.getCapital_amortizado()+capital_amortizado);
                                mipago.setInteres_amortizado(mipago.getInteres_amortizado()+interes_amortizado);
                                mipago.setTipo("C");
                                new Prestamo_ctr().set_pago(mipago);

                                if(cuota <= 0){
                                    amortz.setEstado(2); // si la cuota esta totalmente paga la seteo como pagada
                                    amortz.setFecha_pago(new Fecha_utiliti().getFechaSystemaYYMMDD());
                                    p.setCantida_cuotas_restantes(p.getCantida_cuotas_restantes() - 1);
                                    new Prestamo_ctr().set_prestamo(prestamo); //actualizo los datos del prestamo
                                }

                            }

                            new Prestamo_ctr().set_prestamo_amortizaciones(amortz);
                        }
                    }
                }else{
                  //  Toast.makeText(Pa.this,"No se encontraron datos",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}