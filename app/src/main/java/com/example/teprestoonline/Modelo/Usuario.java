package com.example.teprestoonline.Modelo;

import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class Usuario {

    private  String id;
    private  String clave;
    private  String usuario;
    private  String fecha_alta_humana;
    private  long fecha_unix;
    private  String fecha_humana_ult_mod;
    private  long fecha_unix_ult_mod;
    public final static String BBDD_NAME = "usuarios";
    public final static String BBDD_NAME2 = "fecha_ultima_mod";
    public final static String BBDD_NAME3 = "fecha_ultima_mod_unix";
    private int contador_clave;


    public static Usuario usuario_logueado;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha_alta_humana() {
        return fecha_alta_humana;
    }

    public void setFecha_alta_humana(String fecha_alta_humana) {
        this.fecha_alta_humana = fecha_alta_humana;
    }

    public long getFecha_unix() {
        return fecha_unix;
    }

    public void setFecha_unix(long fecha_unix) {
        this.fecha_unix = fecha_unix;
    }

    public void set_datos_unicos(Context contesto){
        setId(UUID.randomUUID().toString());
        Calendar calendario =  Calendar.getInstance();
        setFecha_unix((calendario.getTimeInMillis() * -1));
        setFecha_unix_ult_mod((calendario.getTimeInMillis() * -1));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(calendario.getTime());
        setFecha_alta_humana(formattedDate);
        setFecha_humana_ult_mod(formattedDate);

        Toast.makeText(contesto,"Datos Genericos",Toast.LENGTH_LONG);
    }

     public void set_datos_ultima_modificaion(Context contesto){
         Calendar calendario =  Calendar.getInstance();
         setFecha_unix_ult_mod((calendario.getTimeInMillis() * -1));

         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         String formattedDate = df.format(calendario.getTime());
         setFecha_humana_ult_mod(formattedDate); // seteo la fecha de ultima modificacion legible
     }

    public  String getFecha_humana_ult_mod() {
        return fecha_humana_ult_mod;
    }

    public  void setFecha_humana_ult_mod(String fecha_humana_ult_mod) {
        this.fecha_humana_ult_mod = fecha_humana_ult_mod;
    }

    public  long getFecha_unix_ult_mod() {
        return fecha_unix_ult_mod;
    }

    public  void setFecha_unix_ult_mod(long fecha_unix_ult_mod) {
       this.fecha_unix_ult_mod = fecha_unix_ult_mod;
    }

    public int getContador_clave() {
        return contador_clave;
    }

    public void setContador_clave(int contador_clave) {
        this.contador_clave = contador_clave;
    }

}
