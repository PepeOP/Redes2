
package cliente;

import java.net.*;
import java.io.*;

public class cliente {
    public static void main(String[] args) {
        String host = ""; // Dirección IP del servidor proporcionada por el profesor
        int pto = 7000; // Puerto del servidor proporcionado por el profesor
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        
        try {
            Socket cl = new Socket(host, pto);
            System.out.println("Conexión establecida...");
            oos = new ObjectOutputStream(cl.getOutputStream());
            
            // Crear objeto de tipo Persona
            Persona persona = new Persona(0, 2019630103, "José ALberto Ortiz Pelaez", 742); // Los valores pueden ser modificados según sea necesario
            
            System.out.println("Enviando objeto Persona...");
            oos.writeObject(persona);
            oos.flush();
            System.out.println("Objeto Persona enviado correctamente.");
            
            // Recibir confirmación del servidor
            ois = new ObjectInputStream(cl.getInputStream());
            String confirmacion = (String) ois.readObject();
            System.out.println("Confirmación del servidor: " + confirmacion);
            
            // Cerrar conexión
            cl.close();
            
            // Esperar 10 segundos antes de intentar conectar al servidor del alumno
            Thread.sleep(10000);
            
            // Conectar al servidor del alumno
            Socket alumnoSocket = new Socket(host, persona.getPuerto());
            System.out.println("Conexión al servidor del alumno establecida en el puerto " + persona.getPuerto());
            alumnoSocket.close();
            
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}