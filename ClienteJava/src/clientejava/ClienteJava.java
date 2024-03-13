
package clientejava;

import java.net.*;
import java.io.*;

public class ClienteJava {

    public static void main(String[] args) {
        try {
            BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Escribe la dirección del servidor:");
            String host = br1.readLine();
            System.out.println("Escribe el puerto:");
            int pto = Integer.parseInt(br1.readLine());
            Socket cl = new Socket(host, pto);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            DataInputStream dis = new DataInputStream(cl.getInputStream());

            System.out.println("Escribe la boleta (Long):");
            long boleta = Long.parseLong(br1.readLine());
            System.out.println("Escribe el nombre (String):");
            String nombre = br1.readLine();
            System.out.println("Escribe la edad (Int):");
            int edad = Integer.parseInt(br1.readLine());

            dos.writeLong(boleta);
            dos.writeUTF(nombre);
            dos.writeInt(edad);
            dos.flush();

            long tamaño = dis.readLong();
            byte[] datos = new byte[(int) tamaño];
            dis.readFully(datos);
            double valor = dis.readDouble();

            dos.writeBoolean(true);
            dos.flush();

            System.out.println("Respuesta recibida del servidor:");
            System.out.println("Tamaño: " + tamaño);
            System.out.println("Datos: " + new String(datos));
            System.out.println("Valor: " + valor);

            System.out.println("Por favor, reenvía el valor al servidor:");
            double valorReenviado = Double.parseDouble(br1.readLine());
            dos.writeDouble(valorReenviado);
            dos.flush();

            boolean confirmacion = dis.readBoolean();
            if (confirmacion) {
                System.out.println("El servidor ha confirmado que el valor ha sido entregado correctamente.");
            } else {
                System.out.println("El servidor no pudo confirmar la entrega del valor correctamente.");
            }

            cl.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
