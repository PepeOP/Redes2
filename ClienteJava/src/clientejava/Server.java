
package clientejava;

import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(1234);
            System.out.println("Esperando cliente...");
            
            while (true) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde "+cl.getInetAddress()+":"+cl.getPort());

                DataInputStream dis = new DataInputStream(cl.getInputStream());
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

                long boleta = dis.readLong();
                String nombre = dis.readUTF();
                int edad = dis.readInt();

                System.out.println("Boleta: " + boleta);
                System.out.println("Nombre: " + nombre);
                System.out.println("Edad: " + edad);

                long tamaño = 5; 
                byte[] datos = "Datos".getBytes(); 
                double valor = 3.14; 

                dos.writeLong(tamaño);
                dos.write(datos);
                dos.writeDouble(valor);
                dos.flush();

                boolean confirmacion = dis.readBoolean();
                if (confirmacion) {
                    System.out.println("Datos confirmados por el cliente.");
                }

                double valorReenviado = dis.readDouble();
                boolean entregaCorrecta = valorReenviado == valor;
                dos.writeBoolean(entregaCorrecta);
                dos.flush();
                cl.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
