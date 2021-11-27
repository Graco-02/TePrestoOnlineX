package com.example.teprestoonline.utilidades;

import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import androidx.annotation.RequiresApi;

public class Conversor_archivos_byte {


    @RequiresApi(api = Build.VERSION_CODES.O)
    public byte[] convertir_File_to_byte(File file){
        byte[] fileContent=null;
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileContent;
    }

    public File convertir_byte_to_file(byte[] bytes,String nombre)
    {
        File file=null;
        try {

            file = new File(nombre+".pdf");
            // Initialize a pointer
            // in file using OutputStream
            OutputStream
                    os
                    = new FileOutputStream(file);

            // Starts writing the bytes in it
            os.write(bytes);
            // Close the file
            os.close();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e);
        }

        return file;
    }

}
