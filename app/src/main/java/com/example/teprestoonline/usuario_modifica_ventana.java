package com.example.teprestoonline;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.teprestoonline.Controladores.Parametros_ctr;
import com.example.teprestoonline.Controladores.Usuario_ctr;
import com.example.teprestoonline.Modelo.Parametros;
import com.example.teprestoonline.Modelo.Usuario;
import com.example.teprestoonline.utilidades.Fecha_utiliti;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class usuario_modifica_ventana {

    private Usuario_ctr control;
    private EditText txt_usuario;
    private EditText txt_clave_ant;
    private EditText txt_clave_new;
    private EditText txt_clave_confirm;

    public void get_acceso(AppCompatActivity view,Usuario usu){

        AlertDialog.Builder builder = new AlertDialog.Builder(view);
        builder.setTitle("Mantenimiento Usuarios");
        LayoutInflater inflater = view.getLayoutInflater();
        View v = inflater.inflate(R.layout.usuario_mant, null);
        builder.setView(v);
        builder.create();


         txt_usuario = (EditText) v.findViewById(R.id.usuariomtn_usuario);
         txt_clave_ant = (EditText) v.findViewById(R.id.usuariomtn_clave_anterior);
         txt_clave_ant.setVisibility(View.VISIBLE);
         txt_clave_new = (EditText) v.findViewById(R.id.usuariomtn_clave_nueva);
         txt_clave_confirm = (EditText) v.findViewById(R.id.usuariomtn_clave_confirm);

        txt_usuario.setText(Usuario.usuario_logueado.getUsuario());

        final AlertDialog ventana = builder.show();

        Button guardar = (Button) v.findViewById(R.id.usuario_modificar_bt);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(get_validaciones(txt_usuario) ){

                    if(get_validacion_datos()) {
                        control = new Usuario_ctr(view.getContext());
                        Usuario.usuario_logueado.setClave(txt_clave_new.getText().toString());
                        Usuario.usuario_logueado.set_datos_ultima_modificaion(view.getContext());
                        control.set_usuario(Usuario.usuario_logueado); // realizo los cambios
                        ventana.dismiss();
                    }

                }
            }
        });

    }


    public void set_solcitar_nuevo_acceso(AppCompatActivity view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view);
        builder.setTitle("Mantenimiento Usuarios");
        LayoutInflater inflater = view.getLayoutInflater();
        View v = inflater.inflate(R.layout.usuario_mant, null);
        builder.setView(v);
        builder.create();

        txt_clave_ant = (EditText) v.findViewById(R.id.usuariomtn_clave_anterior);
        txt_clave_ant.setVisibility(View.GONE);

        txt_usuario = (EditText) v.findViewById(R.id.usuariomtn_usuario);
        txt_clave_new = (EditText) v.findViewById(R.id.usuariomtn_clave_nueva);
        txt_clave_confirm = (EditText) v.findViewById(R.id.usuariomtn_clave_confirm);

        final AlertDialog ventana = builder.show();
        ventana.setCanceledOnTouchOutside(false);

        Button guardar = (Button) v.findViewById(R.id.usuario_modificar_bt);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txt_clave_new.getText().toString().equals(txt_clave_confirm.getText().toString()) ){
                        control = new Usuario_ctr(view.getContext());
                        Usuario usuario = new Usuario();
                        usuario.set_datos_unicos(null);
                        usuario.set_datos_ultima_modificaion(null);

                        usuario.setUsuario(txt_usuario.getText().toString());
                        usuario.setContador_clave(0);
                        usuario.setClave(txt_clave_confirm.getText().toString());
                        usuario.setEstado(0);
                        usuario.setFecha_activacion(new Fecha_utiliti().getFechaSystemaYYMMDD());

                        Calendar calendario =  Calendar.getInstance();
                        usuario.setFecha_activacion_unix((calendario.getTimeInMillis()));

                        try {
                            control.set_usuario(usuario); // realizo los cambios
                            Parametros pr = new Parametros();
                            pr.setFecultcobro("0001-01-01");
                            pr.set_datos_unicos(null);
                            pr.set_datos_ultima_modificaion(null);
                            pr.setId_usuario(usuario.getId());
                            pr.setDispo_bt_nombre(" ");
                            pr.setDispo_bt_mac(" ");
                            pr.setPts_ganar(0);
                            pr.setPts_perder(0);
                            new Parametros_ctr(view.getContext()).set_parametros(pr);

                            ventana.dismiss();
                            Toast.makeText(view.getContext(), "Solcitud enviada debe esperar a que sea activado ",
                                    Toast.LENGTH_LONG).show();

                        }catch (Exception e){

                        }

                }else{
                    txt_clave_confirm.setError("LA clave nueva y debe ser igual");
                    txt_clave_confirm.requestFocus();
                }
            }
        });
    }


    private boolean get_validacion_datos(){
         if(txt_usuario.getText().toString().isEmpty() ){
             txt_usuario.setError("Se debe informar el usuario");
             txt_usuario.requestFocus();
             return false;
         }else if(txt_clave_ant.getText().toString().isEmpty()){
             txt_clave_ant.setError("Se debe informar la clave anterior");
             txt_clave_ant.requestFocus();
             return false;
        }else if(txt_clave_new.getText().toString().isEmpty()){
             txt_clave_new.setError("Se debe informar la nueva clave");
             txt_clave_new.requestFocus();
             return false;
         }else if(txt_clave_confirm.getText().toString().isEmpty()){
             txt_clave_confirm.setError("Se debe confirma la nueva clave");
             txt_clave_confirm.requestFocus();
             return false;
         }else if(!txt_clave_ant.getText().toString().equals(Usuario.usuario_logueado.getClave())){
             txt_clave_ant.setError("La clave anterior debe coincidir");
             txt_clave_ant.requestFocus();
             return false;
         }else if(!txt_clave_new.getText().toString().equals(txt_clave_confirm.getText().toString())){
             txt_clave_ant.setError("La clave nueva no conincide con la confirmacion");
             txt_clave_ant.requestFocus();
             return false;
         }

             return true;
    }

    private void mostar_error(EditText texto, String mensaje){
        texto.requestFocus();
        texto.setError(mensaje);
    }


    private boolean get_validaciones(EditText input){
        if(input.getText().length() < 3 || input.getText().toString().isEmpty()){
            mostar_error(input,"incorrecto");
            return false;
        }else{
            return true;
        }
    }

}
