package com.example.teprestoonline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Prestamo_ctr;
import com.example.teprestoonline.Modelo.Cliente;
import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.Modelo.Prestamo_cuota;
import com.example.teprestoonline.Modelo.amortizacion_cuota;
import com.example.teprestoonline.utilidades.Fecha_utiliti;

import java.sql.Date;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class prestamo_alta {

    private AppCompatActivity actividad;
    private ImageButton bt_cli_buscar;


    private Prestamo_ctr controlador_prestamo;
    private CheckBox opc_imprimir;
    private CheckBox opc_generar_contrato;
    private ImageButton bt_crear;
    private ImageButton bt_eliminar;
    private ImageButton bt_limpiar;
    private RadioButton opcion_perido_d;
    private RadioButton opcion_perido_s;
    private RadioButton opcion_perido_q;
    private RadioButton opcion_perido_m;
    private EditText txt_cant_cuotas;
    private EditText txt_cuota;
    private EditText txt_cliente;
    private EditText txt_monto_finaciado;
    private EditText txt_tasa;
    private EditText txt_restante;
    private EditText txt_fecha_inicio;
    private EditText txt_porct_atrz;
    private EditText txt_mont_fijo;
    private CheckBox opc_regular;
    private CheckBox opc_cuota;
    private Cliente cliente;
    private  AlertDialog ventana = null;

    public prestamo_alta(AppCompatActivity view){
        this.actividad = view;
        controlador_prestamo = new Prestamo_ctr();
    }

    public void set_view_alta(Cliente cl){
        this.cliente = cl;

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this.actividad);
        builder.setTitle("Alta Prestamo");
        LayoutInflater inflater = this.actividad.getLayoutInflater();
        View v = inflater.inflate(R.layout.prestamo_alta, null);
        builder.setView(v);
        builder.create();

        txt_cliente = (EditText) v.findViewById(R.id.prestamo_alta_cliente);
        bt_cli_buscar = (ImageButton) v.findViewById(R.id.prestamo_alta_cliente_bt_search);

        if(cl!=null){
             bt_cli_buscar.setVisibility(View.GONE);
             txt_cliente.setText(cl.getPersona().getNombres()+" , "+cl.getPersona().getApellidos());
        }else{
            bt_cli_buscar.setVisibility(View.VISIBLE);
        }

        set_campos_formulario(v);
        ventana = builder.show();
        ventana.setCanceledOnTouchOutside(false);
    }

    private void set_campos_formulario( View v){

         txt_monto_finaciado = (EditText) v.findViewById(R.id.prestamo_alta_monto_financiado);
         txt_tasa = (EditText) v.findViewById(R.id.prestamo_alta_tasa);
         txt_restante = (EditText) v.findViewById(R.id.prestamo_alta_restante);
         opc_regular = (CheckBox) v.findViewById(R.id.prestamo_alta_opcion_regular);
         opc_cuota = (CheckBox) v.findViewById(R.id.prestamo_alta_opcion_cuota);
         txt_fecha_inicio = (EditText) v.findViewById(R.id.prestamo_alta_fecha_incio);

         txt_porct_atrz =  (EditText) v.findViewById(R.id.prestamo_alta_porct_atrazo);
         txt_mont_fijo =  (EditText) v.findViewById(R.id.prestamo_alta_mnt_fijo_atraz);

         txt_cant_cuotas = (EditText) v.findViewById(R.id.prestamo_alta_cantidad_cuotas);
         txt_cuota = (EditText) v.findViewById(R.id.prestamo_alta_cuota);

         opcion_perido_d = (RadioButton) v.findViewById(R.id.prestamo_alta_opcion_diario);
         opcion_perido_s = (RadioButton) v.findViewById(R.id.prestamo_alta_opcion_semanal);
         opcion_perido_q = (RadioButton) v.findViewById(R.id.prestamo_alta_opcion_quincenal);
         opcion_perido_m = (RadioButton) v.findViewById(R.id.prestamo_alta_opcion_mensual);

         bt_crear = (ImageButton) v.findViewById(R.id.prestamo_alta_bt_crear);
         bt_limpiar = (ImageButton) v.findViewById(R.id.prestamo_alta_bt_limpiar);

         opc_imprimir = (CheckBox) v.findViewById(R.id.prestamo_alta_imprimir);
         opc_generar_contrato = (CheckBox) v.findViewById(R.id.prestamo_alta_generar_contrato);

         bt_crear.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(get_validar()) {
                     Prestamo p = new Prestamo();
                     p.set_datos_unicos();
                     p.setId_cliente(prestamo_alta.this.cliente.getId());
                     p.setId_usuario(prestamo_alta.this.cliente.getId_usuario());
                     set_procesamiento_prestamo_alta(p);
                 }
             }
         });


        opc_regular.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    txt_cant_cuotas.setEnabled(false);
                    txt_porct_atrz.setEnabled(false);
                    txt_mont_fijo.setEnabled(false);
                    opc_cuota.setChecked(false);
                }
            }
        });


        opc_cuota.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    txt_cant_cuotas.setEnabled(true);
                    txt_porct_atrz.setEnabled(true);
                    txt_mont_fijo.setEnabled(true);
                    opc_regular.setChecked(false);

                }
            }
        });

        opcion_perido_d.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    opcion_perido_d.setChecked(true);
                    opcion_perido_s.setChecked(false);
                    opcion_perido_q.setChecked(false);
                    opcion_perido_m.setChecked(false);
                }
            }
        });


        opcion_perido_s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    opcion_perido_d.setChecked(false);
                    opcion_perido_s.setChecked(true);
                    opcion_perido_q.setChecked(false);
                    opcion_perido_m.setChecked(false);
                }
            }
        });


        opcion_perido_q.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    opcion_perido_d.setChecked(false);
                    opcion_perido_s.setChecked(false);
                    opcion_perido_q.setChecked(true);
                    opcion_perido_m.setChecked(false);
                }
            }
        });


        opcion_perido_m.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    opcion_perido_d.setChecked(false);
                    opcion_perido_s.setChecked(false);
                    opcion_perido_q.setChecked(false);
                    opcion_perido_m.setChecked(true);
                }
            }
        });

    }


    private void set_procesamiento_prestamo_alta(Prestamo p){
        if(!txt_fecha_inicio.getText().toString().isEmpty()){
            p.setFecha_ult_cobro(txt_fecha_inicio.getText().toString());
        }else{
            p.setFecha_ult_cobro("0001-01-01");
        }
        p.setFecha_ult_pago("0001-01-01");
        p.setMonto_financiado(Double.parseDouble(txt_monto_finaciado.getText().toString()));
        p.setTasa(Integer.parseInt(txt_tasa.getText().toString()));
        p.setRestante(p.getMonto_financiado());

        if(opcion_perido_d.isChecked()){
            p.setPeriodo(1);
        }else if(opcion_perido_s.isChecked()){
            p.setPeriodo(7);
        }else if(opcion_perido_q.isChecked()){
            p.setPeriodo(15);
        }else if(opcion_perido_m.isChecked()){
            p.setPeriodo(30);
        }

        if(opc_cuota.isChecked()){
            p.setTipo(1); // tipo 1 = Cuotas

            p.setCantida_cuotas_restantes(Integer.parseInt(txt_cant_cuotas.getText().toString()));
            p.setCantidad_cuotas(Integer.parseInt(txt_cant_cuotas.getText().toString()));

            double monto = p.getMonto_financiado();
            double tasa = Double.parseDouble(txt_tasa.getText().toString()) / 100 ;
            int ganancia = Integer.parseInt(String.valueOf(Math.round((monto * tasa))));
            double restante = monto + ganancia;
            double cuota = restante  / p.getCantidad_cuotas();
            double interes_cuota = (double) (ganancia) / p.getCantidad_cuotas();
            double capital_cuota = (double) (monto) / p.getCantidad_cuotas();
            double cuotare_dondeada = (int) Math.round(cuota);

            p.setInteres_cuota(interes_cuota);
            p.setCapital_cuota(capital_cuota);
            p.setCuota(cuotare_dondeada);
            p.setRestante(restante);

            if(!txt_porct_atrz.getText().toString().isEmpty()) {
                p.setPorcentage_atrazo(Integer.parseInt(txt_porct_atrz.getText().toString()));
            }else{
                p.setPorcentage_atrazo(0);
            }

            if(!txt_mont_fijo.getText().toString().isEmpty()) {
                p.setMonto_fijo(Integer.parseInt(txt_mont_fijo.getText().toString()));
            }else{
                p.setMonto_fijo(0);
            }

        }

        txt_restante.setText(""+p.getRestante());
        txt_cuota.setText(""+p.getCuota());
        ventana.dismiss();

        set_mensage("Esta seguro de guardar los cambios?",p);
    }

    private boolean get_validar(){
        if(this.cliente == null){
            txt_cliente.setError("Se debe Seleccionar un cliente");
            txt_cliente.requestFocus();
            return  false;
        }else if(txt_monto_finaciado.getText().toString().isEmpty()
                 || Double.parseDouble(txt_monto_finaciado.getText().toString()) <= 0
        ){
            txt_monto_finaciado.setError("Se debe informar un monto a financiar");
            txt_monto_finaciado.requestFocus();
            return false;
        }else if(txt_tasa.getText().toString().isEmpty()
                || Double.parseDouble(txt_monto_finaciado.getText().toString()) <= 0
        ){
            txt_tasa.setError("Se debe informar una tasa de ganancia");
            txt_tasa.requestFocus();
            return false;
        }else if(!opc_cuota.isChecked() && !opc_regular.isChecked()){
            Toast.makeText(this.actividad,"Se debe seleccionar una opcion de prestamo",Toast.LENGTH_LONG).show();
            return false;
        }else if(!opcion_perido_d.isChecked() && !opcion_perido_m.isChecked() && !opcion_perido_q.isChecked() && !opcion_perido_s.isChecked()){
            Toast.makeText(this.actividad,"Se debe seleccionar una opcion de periodo",Toast.LENGTH_LONG).show();
            return false;
        }else if(opc_cuota.isChecked()){
            if(txt_cant_cuotas.getText().toString().isEmpty()
                    || Integer.parseInt(txt_cant_cuotas.getText().toString()) <= 0
            ){
                txt_cant_cuotas.setError("se debe informar una cantidad de cuotas");
                txt_cant_cuotas.requestFocus();
                return false;
            }
        }
        return true;
    }


    private void set_mensage(String mensaje,Prestamo p){
        AlertDialog.Builder builder = new AlertDialog.Builder(prestamo_alta.this.actividad);
        builder.setTitle("Notificacion ");
        builder.setMessage(mensaje);
        builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                controlador_prestamo.set_prestamo(p);
                if(p.getTipo()==1){
                    controlador_prestamo.set_proceso_amortizaciones(p);
                }
                Toast.makeText(prestamo_alta.this.actividad,"Agregado",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                prestamo_alta.this.ventana.show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }





}
