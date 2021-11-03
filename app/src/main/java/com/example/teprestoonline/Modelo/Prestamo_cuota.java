package com.example.teprestoonline.Modelo;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class Prestamo_cuota {

    private int cantidad_cuotas;
    private int cantida_cuotas_restantes;
    private double cuota;
    private ArrayList<String> fechas;

    public Prestamo_cuota(){
        fechas = new ArrayList<>();
    }

    public int getCantidad_cuotas() {
        return cantidad_cuotas;
    }

    public void setCantidad_cuotas(int cantidad_cuotas) {
        this.cantidad_cuotas = cantidad_cuotas;
    }

    public int getCantida_cuotas_restantes() {
        return cantida_cuotas_restantes;
    }

    public void setCantida_cuotas_restantes(int cantida_cuotas_restantes) {
        this.cantida_cuotas_restantes = cantida_cuotas_restantes;
    }

    public double getCuota() {
        return cuota;
    }

    public void setCuota(double cuota) {
        this.cuota = cuota;
    }

    public ArrayList<String> getFechas() {
        return fechas;
    }

    public void setFechas(ArrayList<String> fechas) {
        this.fechas = fechas;
    }

    public void set_add_fecha(String fecha){
        fechas.add(fecha);
    }

    public void set_eliminar_fecha(String fecha){
        fechas.remove(fecha);
    }

}
