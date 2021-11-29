package com.example.teprestoonline.utilidades;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.example.teprestoonline.Modelo.Dispositivos_bluethoo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Bluethop_manager {
    private BluetoothAdapter mBluetoothAdapter ;
    private Dispositivos_bluethoo tb_dispo;

    public Bluethop_manager(){
        mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter();

    }

    public List<Dispositivos_bluethoo> get_emparejados(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<Dispositivos_bluethoo> listDevices = new ArrayList<Dispositivos_bluethoo>();

        for(BluetoothDevice btd : pairedDevices){
            tb_dispo =  new Dispositivos_bluethoo();
            tb_dispo.setNombre(btd.getName());
            tb_dispo.setMac(btd.getAddress());
            listDevices.add(tb_dispo);
        }

        return listDevices;
    }
}
