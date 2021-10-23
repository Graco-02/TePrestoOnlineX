package com.example.teprestoonline;

import android.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Cliente_ctr;
import com.example.teprestoonline.Modelo.Cliente;
import com.example.teprestoonline.Modelo.Contacto;
import com.example.teprestoonline.Modelo.Direccion;
import com.example.teprestoonline.Modelo.Persona;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ventana_alta_cliente {

    private AppCompatActivity view;
    private Spinner spinner_tipdoc;
    private EditText txt_nombres;
    private EditText txt_apellidos;
    private EditText txt_identificacion;
    private EditText txt_municipio;
    private EditText txt_sector;
    private EditText txt_calle;
    private EditText txt_vivienda;
    private EditText txt_telefono;
    private EditText txt_correo;

    public ventana_alta_cliente(AppCompatActivity view){
        this.view = view;
    }

    public void set_view_alta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(view);
        builder.setTitle("Alta Clientes");
        LayoutInflater inflater = view.getLayoutInflater();
        View v = inflater.inflate(R.layout.formulario_cliente_alta, null);
        builder.setView(v);
        builder.create();

        spinner_tipdoc = (Spinner) v.findViewById(R.id.cli_form_tipos_identificacion);
        set_proceso_spinner_tipos_documento();

        txt_identificacion = (EditText) v.findViewById(R.id.cli_form_identificacion);
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

        txt_telefono = (EditText) v.findViewById(R.id.cli_form_telefono);
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


        final AlertDialog ventana = builder.show();
        ventana.setCanceledOnTouchOutside(false); // para que el dialogo no se cierre si se toca afuera


        Button bt_guardar = v.findViewById(R.id.cli_form_guardar);
        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                set_tratamiento(v,ventana);
            }
        });

        Button bt_cancelar = v.findViewById(R.id.cli_form_cancelar);
        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ventana.dismiss();
            }
        });
    }

    private void set_proceso_spinner_tipos_documento(){
        List<String> tipos_documento = new ArrayList<String>();
        tipos_documento.add("CED");
        tipos_documento.add("PSS");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.view,
                android.R.layout.simple_spinner_item, tipos_documento);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_tipdoc.setAdapter(dataAdapter);

    }

    private void set_tratamiento(View v,AlertDialog ventana){

        // hago el tratamiento del formulario
         txt_nombres = (EditText) v.findViewById(R.id.cli_form_nombre);
         txt_apellidos = (EditText) v.findViewById(R.id.cli_form_apellidos);
         Spinner tipo_identificacion = (Spinner) v.findViewById(R.id.cli_form_tipos_identificacion);

        //datos de la direccion
         txt_municipio = (EditText) v.findViewById(R.id.cli_form_municipio);
         txt_sector = (EditText) v.findViewById(R.id.cli_form_sector);
         txt_calle = (EditText) v.findViewById(R.id.cli_form_calle);
         txt_vivienda = (EditText) v.findViewById(R.id.cli_form_vivienda);

        //datos del contacto

         txt_correo = (EditText) v.findViewById(R.id.cli_form_correo);

         Persona p = new Persona();
         p.set_datos_unicos(v.getContext());
         p.setNombres(txt_nombres.getText().toString());
         p.setApellidos(txt_apellidos.getText().toString());
         p.setIdentificacion(txt_identificacion.getText().toString());
         p.setTipo_identificacion(String.valueOf(tipo_identificacion.getSelectedItem()));

        Direccion d = new Direccion();
        d.set_datos_unicos(v.getContext());
        d.setMunicipio(txt_municipio.getText().toString());
        d.setSector(txt_sector.getText().toString());
        d.setCalle(txt_calle.getText().toString());
        d.setVivienda(txt_vivienda.getText().toString());

        Contacto c = new Contacto();
        c.set_datos_unicos(v.getContext());
        c.setTelefono(txt_telefono.getText().toString());
        c.setCorreo(txt_correo.getText().toString());

        p.setDireccion(d);
        p.setContacto(c);

         if(get_valida_datos(p)){//valido los datos obligatorios
             Cliente cli = new Cliente();
             cli.set_datos_unicos(this.view.getApplicationContext());
             cli.setPersona(p);

             new Cliente_ctr(this.view.getApplicationContext()).set_cliente(cli);

             ventana.dismiss();
             Toast.makeText(this.view,"Correcto",Toast.LENGTH_LONG).show();
         }

    }

    private boolean get_valida_datos(Persona p){
        if(p.getNombres().isEmpty() || p.getNombres().length() < 3){
            txt_nombres.requestFocus();
            txt_nombres.setError("El Nombre no esta informado minimo 3 caracteres");
            return false;
        }else if(p.getApellidos().isEmpty() || p.getApellidos().length() < 3){
            txt_apellidos.requestFocus();
            txt_apellidos.setError("El Apelido no esta informado minimo 3 caracteres");
            return false;
        }else if(p.getIdentificacion().isEmpty() || p.getIdentificacion().length() < 11){
            txt_identificacion.requestFocus();
            txt_identificacion.setError("La Identificacion no esta informado minimo 11 caracteres");
            return false;
        }

        return true;
    }



}
