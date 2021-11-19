package com.example.teprestoonline.Modelo;

import android.content.Context;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Prestamo_cuota implements Serializable {

    private int cantidad_cuotas;
    private int cantida_cuotas_restantes;
    private double cuota;
    private double capital_cuota;
    private double interes_cuota;
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

    public double getCapital_cuota() {
        return capital_cuota;
    }

    public void setCapital_cuota(double capital_cuota) {
        this.capital_cuota = capital_cuota;
    }

    public double getInteres_cuota() {
        return interes_cuota;
    }

    public void setInteres_cuota(double interes_cuota) {
        this.interes_cuota = interes_cuota;
    }

}
