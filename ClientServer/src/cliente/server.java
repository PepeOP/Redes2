
package cliente;

import java.net.*;
import java.io.*;
import java.util.LinkedList;
import java.util.ListIterator;

public class server {
    public static void main(String[] args) {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ServerSocket s = new ServerSocket(9999);
            System.out.println("Servicio iniciado... Esperando cliente");
            for (;;) {
                Socket cl = s.accept();
                System.out.println("Cliente conectado desde " + cl.getInetAddress() + ":" + cl.getPort());
                oos = new ObjectOutputStream(cl.getOutputStream());
                ois = new ObjectInputStream(cl.getInputStream());
                
                // Leer objeto Persona enviado por el cliente
                Persona persona = (Persona) ois.readObject();
                System.out.println("Objeto Persona recibido.. Extrayendo información");
                System.out.println("Boleta: " + persona.getBoleta());
                System.out.println("Nombre: " + persona.getNombre());
                System.out.println("Puerto: " + persona.getPuerto());
                
                // Conectar al servidor del alumno en el puerto 0742
                ServerSocket alumnoServerSocket = new ServerSocket(742);
                System.out.println("Servidor del alumno esperando conexiones en el puerto 0742");
                Socket alumnoSocket = alumnoServerSocket.accept();
                
                // Recibir tamaño del archivo
                long fileSize = ois.readLong();
                System.out.println("Tamaño del archivo recibido: " + fileSize);
                
                // Recibir el archivo
                byte[] fileData = new byte[(int) fileSize];
                ois.readFully(fileData);
                System.out.println("Archivo recibido correctamente.");
                
                // Procesar el archivo recibido
                ObjectInputStream fileInput = new ObjectInputStream(new ByteArrayInputStream(fileData));
                
                // Leer la lista de objetos Objeto1
                ListaObjetos1 listaObjetos1 = (ListaObjetos1) fileInput.readObject();
                System.out.println("Lista de objetos Objeto1 recibida:");
                listaObjetos1.muestraObjeto1();
                
                // Leer el número de elementos de Objeto2
                int numObjeto2 = fileInput.readInt();
                System.out.println("Número de objetos Objeto2: " + numObjeto2);
                
                // Leer y mostrar los objetos Objeto2
                for (int i = 0; i < numObjeto2; i++) {
                    int OID = fileInput.readInt();
                    String nomObjeto = fileInput.readUTF();
                    long cualidad21 = fileInput.readLong();
                    short cualidad22 = fileInput.readShort();
                    Objeto2 objeto2 = new Objeto2(OID, nomObjeto, cualidad21, cualidad22);
                    System.out.println("Objeto2 recibido: OID=" + OID + ", Nombre=" + nomObjeto + ", Cualidad21=" + cualidad21 + ", Cualidad22=" + cualidad22);
                }
                
                // Simplemente para demostración, enviaremos verdadero al cliente
                boolean resultado = true;
                
                // Enviar el resultado al cliente
                oos.writeBoolean(resultado);
                oos.flush();
                
                // Cerrar conexiones
                fileInput.close();
                alumnoSocket.close();
                alumnoServerSocket.close();
                cl.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
