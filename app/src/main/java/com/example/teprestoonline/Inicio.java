package com.example.teprestoonline;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.teprestoonline.Modelo.Usuario;

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

                break;
            case R.id.inicio_prestamo:

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

}