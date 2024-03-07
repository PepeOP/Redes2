/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiarchivo;

import java.net.*;
import java.io.*;

public class SE_Multiarchivo {
    public static void main(String[] args){
        try{
            ServerSocket s = new ServerSocket(7000);
            System.out.println("Servidor preparado.");
            for(;;){
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde "+cl.getInetAddress()+":"+cl.getPort());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                int numArchivos = dis.readInt(); // Lee el número de archivos a recibir
                for (int i = 0; i < numArchivos; i++) {
                    String nombre = dis.readUTF(); // Lee el nombre del archivo
                    System.out.println("Recibimos el archivo: " + nombre);
                    long tam = dis.readLong(); // Lee el tamaño del archivo
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));
                    long recibidos = 0;
                    int n;
                    byte[] b = new byte[1024];
                    while (recibidos < tam) {
                        n = dis.read(b); // Lee el contenido del archivo
                        dos.write(b, 0, n);
                        dos.flush();
                        recibidos += n;
                    }
                    System.out.println("\nArchivo recibido.");
                    dos.close();
                }
                dis.close();
                cl.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
