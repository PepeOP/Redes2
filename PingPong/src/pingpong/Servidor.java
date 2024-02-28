/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pingpong;

import java.net.*;
import java.io.*;

public class Servidor {
    public static void main(String[] args){
        try{
            ServerSocket s = new ServerSocket(1234);
            System.out.println("Esperando cliente...");
            Socket cl = s.accept();
            System.out.println("Conexión establecida desde "+cl.getInetAddress()+":"+cl.getPort());

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()));
            BufferedReader br2 = new BufferedReader(new InputStreamReader(cl.getInputStream()));

            // Hilo para leer los mensajes del cliente y mostrarlos en la consola
            Thread thread = new Thread(() -> {
                try {
                    String mensaje;
                    while ((mensaje = br2.readLine()) != null) {
                        System.out.println("Cliente dice: " + mensaje);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();

            String mensaje = br.readLine();
            while (mensaje != null && !mensaje.equals("adios")) {
                pw.println(mensaje);
                pw.flush();
                mensaje = br.readLine();
                if (mensaje != null && mensaje.equals("adios")) {
                    pw.println("adios");
                    pw.flush();
                    System.out.println("Cerrando conexión con el cliente.");
                    cl.close();
                    break; // Salir del bucle infinito
                }
            }
            if (mensaje != null && mensaje.equals("adios")) {
                cl.close();
            }

        }catch(Exception e){
            e.printStackTrace();
        }//catch
    }//main
}