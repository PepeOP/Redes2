
package pingpong;
import java.net.*;
import java.io.*;

public class Cliente {

    public static void main(String[] args) {
        try {
            BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Escribe la dirección del servidor");
            String host = br1.readLine();
            System.out.println("\n\n Escribe el puerto:");
            int pto = Integer.parseInt(br1.readLine());
            Socket cl = new Socket(host, pto);
            BufferedReader br2 = new BufferedReader(new InputStreamReader(cl.getInputStream()));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()));

            // Hilo para leer los mensajes del servidor y mostrarlos en la consola
            Thread thread = new Thread(() -> {
                try {
                    String mensaje;
                    while ((mensaje = br2.readLine()) != null) {
                        System.out.println("Servidor dice: " + mensaje);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();

            String mensaje = br1.readLine();
            while (mensaje != null && !mensaje.equals("adios")) {
                pw.println(mensaje);
                pw.flush();
                mensaje = br1.readLine();
                if (mensaje != null && mensaje.equals("adios")) {
                    pw.println("adios");
                    pw.flush();
                    System.out.println("Cerrando conexión con el servidor.");
                    cl.close();
                    break; // Salir del bucle infinito
                }
            }
            if (mensaje != null && mensaje.equals("adios")) {
                cl.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}