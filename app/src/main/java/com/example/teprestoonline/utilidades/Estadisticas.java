package com.example.teprestoonline.utilidades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Prestamo_ctr;
import com.example.teprestoonline.Inicio;
import com.example.teprestoonline.Modelo.Pago;
import com.example.teprestoonline.Modelo.Prestamo;
import com.example.teprestoonline.Modelo.Usuario;
import com.example.teprestoonline.R;
import com.example.teprestoonline.mantenimiento_clientes;
import com.example.teprestoonline.parametros_matenimiento;
import com.example.teprestoonline.usuario_modifica_ventana;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Estadisticas extends AppCompatActivity {

    private BarChart grafico_barras;
    private EditText txt_year;
    private Spinner selector_mes;
    private CheckBox op_pagos;
    private CheckBox op_financiamientos;
    private Fecha_utiliti fecha_controler = new Fecha_utiliti();
    private double total_recibido;
    private double num_total_pagos;
    private double num_total_financiamientos;
    private double total_financiado;
    private BarData data;
    private ArrayList<BarEntry> financiamientos_lista;
    private ArrayList<BarEntry> pagos_lista;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private EditText txt_tot_financiado;
    private EditText txt_num_recibido;
    private EditText txt_tot_recibido;
    private EditText txt_num_fin;
    private LinearLayout ln_datos_pagos;
    private LinearLayout ln_datos_finan;
    private List<Prestamo> listado_prestamos;
    private List<Pago> listado_pagos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        grafico_barras = (BarChart) findViewById(R.id.barra_graficas);
        selector_mes = (Spinner) findViewById(R.id.estadisticas_meses);
        selector_mes.setAdapter(proceso_spiner());

        //coloco en el texto inicial el anio en curso
        txt_year = (EditText) findViewById(R.id.estadisticas_year);
        txt_year.setText(fecha_controler.getFechaSystemaYYMMDD().substring(0,4));

        //dejo seleccionado el mes en curso
        int mes = fecha_controler.getMes();
        selector_mes.setSelection(mes-1);

        op_financiamientos= (CheckBox) findViewById(R.id.estadisticas_opc_salidas);
        op_pagos= (CheckBox) findViewById(R.id.estadisticas_opc_entradas);

        financiamientos_lista = new ArrayList<>();
        pagos_lista = new ArrayList<>();

        ln_datos_pagos = (LinearLayout) findViewById(R.id.estadisticas_pagos1);
        ln_datos_finan = (LinearLayout) findViewById(R.id.estadisticas_financiamientos);
        txt_tot_recibido = (EditText) findViewById(R.id.estadisticas_pagos_tot_recibido_txt);
        txt_tot_financiado = (EditText) findViewById(R.id.estadisticas_pagos_tot_fin_txt);
        txt_num_recibido = (EditText) findViewById(R.id.estadisticas_pagos_num_pagos_txt);
        txt_num_fin = (EditText) findViewById(R.id.estadisticas_pagos_num_fin_txt);

        listado_prestamos=new ArrayList<>();
        listado_pagos=new ArrayList<>();
        set_listar_prestamos();//listo todos los prestamos y pagos a prestamos en working para optimizar las busquedas
    }


    private ArrayAdapter<String> proceso_spiner(){
        //lleno el combo tipos de documentos
        List<String> meses = new ArrayList<String>();
        for(int i= 1;i<=12;i++){
            meses.add(new Fecha_utiliti().get_nombre_del_mes(i));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, meses);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return dataAdapter;
    }

    public void set_lanzar(View v){
        int mes = selector_mes.getSelectedItemPosition()+1;
        int year=0;
        num_total_financiamientos=0;
        total_financiado=0;
        total_recibido = 0;
        num_total_pagos = 0;

        try {
            for(int i = 0;i<listado_prestamos.size();i++){//bucle para validar los prestamos por mes seleccionado
               Prestamo p = listado_prestamos.get(i);
               year= Integer.parseInt(txt_year.getText().toString());
               financiamientos_lista.clear();
               pagos_lista.clear();
               grafico_barras.clear();
               grafico_barras.removeAllViews();
               String fecha_split[] = p.getFecha_alta_humana().substring(0, 10).split("-");
               int year_f = Integer.parseInt(fecha_split[0]);
               int mes_f = Integer.parseInt(fecha_split[1]);

               if (mes_f == mes && year_f == year && op_financiamientos.isChecked()) {
                   num_total_financiamientos++;
                   total_financiado += p.getMonto_financiado();
               }

          }

          for(int o=0;o<listado_pagos.size();o++){//blucle para seleccionar los pagos por mes seleccionado
              Pago pago = listado_pagos.get(o);
              if(op_pagos.isChecked()){
                  String fecha_split[] = pago.getFecha_pago().split("-");
                  int mes_pago = Integer.parseInt(fecha_split[1]);
                  int year_pago = Integer.parseInt(fecha_split[0]);

                  if(mes_pago == mes && year_pago == year ) {
                      total_recibido += pago.getMonto_pagado();
                      num_total_pagos++;
                  }
              }
          }
          set_graficar();
        }catch (NumberFormatException e){
            Toast.makeText(getApplicationContext(),
                    "Se debe informar el dato ANIO como numerico 9999"
                    , Toast.LENGTH_LONG).show();
        }

    }



    private void set_graficar(){

        financiamientos_lista.add(new BarEntry( 5 , (float) total_financiado ));
        BarDataSet barDataSet_f = new BarDataSet(financiamientos_lista,"FINANCIAMIENTO REALIZADOS");
        barDataSet_f.setColor(Color.RED);
        barDataSet_f.setDrawValues(true);

        pagos_lista.add(new BarEntry( 1 , (float) total_recibido ));
        BarDataSet barDataSet_p = new BarDataSet(pagos_lista,"PAGOS REALIZADOS");
        barDataSet_p.setColor(Color.BLUE);
        barDataSet_p.setDrawValues(true);

        //data = new BarData(barDataSet_p);
        data = new BarData(barDataSet_f);
        data.addDataSet(barDataSet_p);
        data.groupBars(0,0,2);
        grafico_barras.removeAllViews();
        grafico_barras.setData(data);

        ln_datos_pagos.setVisibility(View.VISIBLE);
        ln_datos_finan.setVisibility(View.VISIBLE);

        txt_num_fin.setText(""+num_total_financiamientos);
        txt_num_recibido.setText(""+num_total_pagos);

        txt_tot_financiado.setText(""+total_financiado);
        txt_tot_recibido.setText(""+total_recibido);
    }


    private void set_listar_prestamos(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child(Prestamo_ctr.BBDD_NAME);
        final Query usuQuery = ref.orderByChild("id");

        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot hijo : dataSnapshot.getChildren()){
                    Prestamo p = hijo.getValue(Prestamo.class);
                    if(Usuario.usuario_logueado.getId().equalsIgnoreCase(p.getId_usuario())){//valdiacion de prestamos por usuario
                         listado_prestamos.add(p);
                         get_listado_pagos(p);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void get_listado_pagos(Prestamo p){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Prestamo_ctr.BBDD_NAME3).child(p.getId());

        final Query usuQuery = myRef.orderByChild("fecha_alta_unix:");
        usuQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot hijo: dataSnapshot.getChildren()) {
                        Pago pago = hijo.getValue(Pago.class);
                        if(pago.getFecha_pago()!=null
                                && pago.getId_usuario().equalsIgnoreCase(Usuario.usuario_logueado.getId())) {
                            listado_pagos.add(pago);
                        }
                    }//fin bucle

                }else{
                    System.out.println("SIN DATOS PARA MOSTRAR TOODOS LOS PAGOS..:" );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_reportes_estadisticos,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem opcion_menu){
        int id = opcion_menu.getItemId();

        switch (id){
            case R.id.est_menu_cli_atrz:
                break;
            case R.id.est_menu_cli_atrzt:
                break;
            case R.id.est_menu_detalle_operaciones:
                break;
        }
        return super.onOptionsItemSelected(opcion_menu);
    }

}