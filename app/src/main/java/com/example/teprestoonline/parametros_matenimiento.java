package com.example.teprestoonline;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Parametros_ctr;
import com.example.teprestoonline.Modelo.Dispositivos_bluethoo;
import com.example.teprestoonline.Modelo.Parametros;
import com.example.teprestoonline.Modelo.Usuario;
import com.example.teprestoonline.utilidades.Bluethop_manager;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class parametros_matenimiento {

    private final Bluethop_manager mng_bluethop;
    private Spinner spinner;
    private List<Dispositivos_bluethoo> listado_dispositivos;
    private AppCompatActivity activiti;
    private Parametros parametros;

    public parametros_matenimiento(AppCompatActivity view, Parametros parametros){
        activiti = view;
        this.parametros = parametros;
        AlertDialog.Builder builder = new AlertDialog.Builder(view);
        LayoutInflater inflater = view.getLayoutInflater();
        View v = inflater.inflate(R.layout.parametro_formulario, null);
        builder.setView(v);
        builder.create();

        EditText txt_fecultcob = (EditText) v.findViewById(R.id.parametros_fecultcob);
        EditText txt_diasgracia = (EditText) v.findViewById(R.id.parametros_dias_gracia);
        EditText txt_ptsganar = (EditText) v.findViewById(R.id.parametros_pts_ganar);
        EditText txt_ptsperder = (EditText) v.findViewById(R.id.parametros_pts_perder);

        if(parametros!=null){
            txt_fecultcob.setText(parametros.getFecultcobro());
            txt_diasgracia.setText(""+parametros.getDias_gracia());
            txt_ptsganar.setText(""+parametros.getPts_ganar());
            txt_ptsperder.setText(""+parametros.getPts_perder());
        }
        
        mng_bluethop = new Bluethop_manager();

        spinner = (Spinner) v.findViewById(R.id.parametros_bt_selector);
        set_proceso_spinner(v);
        
        final AlertDialog ventana = builder.show();
        ventana.setCanceledOnTouchOutside(false);
        
        Button guardar = (Button) v.findViewById(R.id.parametros_bt_guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if(parametros==null) {
                     Parametros parametros = new Parametros();
                     parametros.set_datos_unicos(activiti);
                     parametros.setId_usuario(Usuario.usuario_logueado.getId());
                 }

                 parametros.set_datos_ultima_modificaion(activiti);

                 try{
                     parametros.setDias_gracia(Integer.parseInt(txt_diasgracia.getText().toString()));
                     parametros.setFecultcobro(txt_fecultcob.getText().toString());
                     parametros.setPts_ganar(Integer.parseInt(txt_ptsganar.getText().toString()));
                     parametros.setPts_perder(Integer.parseInt(txt_ptsperder.getText().toString()));

                     for(int i = 0;i<listado_dispositivos.size();i++){ //obtengo los datos del dispositivo BT
                         if(listado_dispositivos.get(i).getNombre().equals( spinner.getSelectedItem().toString())){
                             parametros.setDispo_bt_nombre(listado_dispositivos.get(i).getNombre());
                             parametros.setDispo_bt_mac(listado_dispositivos.get(i).getMac());
                         }
                     }

                     if(parametros.getDispo_bt_nombre().isEmpty()){
                         parametros.setDispo_bt_mac("");
                     }
                     if(parametros.getDispo_bt_nombre().isEmpty()){
                         parametros.setDispo_bt_nombre("");
                     }

                     new Parametros_ctr(activiti).set_parametros(parametros);//guardo o modifico los parametros
                     Toast.makeText(activiti, "Realizado", Toast.LENGTH_SHORT).show();
                 }catch (NumberFormatException numfecp){
                     Toast.makeText(activiti, "ERROR"+numfecp.getMessage().toString(), Toast.LENGTH_SHORT).show();
                 }
            }

        });
  
        Button salir = (Button) v.findViewById(R.id.parametros_bt_salir);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ventana.dismiss();
            }
        });
    }

    private void set_proceso_spinner(View v){
        List<String> dispositivos_nombre = new ArrayList<String>();
        listado_dispositivos = mng_bluethop.get_emparejados();

        for(int i = 0;i<listado_dispositivos.size();i++){
            dispositivos_nombre.add(listado_dispositivos.get(i).getNombre());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activiti,
                android.R.layout.simple_spinner_item, dispositivos_nombre);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        if(parametros!=null) {
            for (int i = 0; i < listado_dispositivos.size(); i++) {
                if (listado_dispositivos.get(i).getNombre().equals(parametros.getDispo_bt_nombre())) {
                    spinner.setSelection(i);
                }
            }
        }
    }

}
