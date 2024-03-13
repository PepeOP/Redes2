
package multiarchivo;

import javax.swing.JFileChooser;
import java.net.*;
import java.io.*;

public class CE_Multiarchivo {
    public static void main(String[] args){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.printf("Escriba la dirección del servidor:");
            String host = br.readLine();
            System.out.printf("\n\nEscriba el puerto:");
            int pto = Integer.parseInt(br.readLine());
            Socket cl = new Socket(host, pto);
            JFileChooser jf = new JFileChooser(".");
            jf.setMultiSelectionEnabled(true); // Permitir selección múltiple de archivos
            int r = jf.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION){
                File[] files = jf.getSelectedFiles(); // Obtiene la lista de archivos seleccionados
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                dos.writeInt(files.length); // Envía el número de archivos a enviar
                for (File f : files) {
                    String archivo = f.getAbsolutePath(); // Dirección del archivo
                    String nombre = f.getName(); // Nombre del archivo
                    long tam = f.length(); // Tamaño del archivo
                    dos.writeUTF(nombre); // Envía el nombre del archivo
                    dos.flush();
                    dos.writeLong(tam); // Envía el tamaño del archivo
                    dos.flush();
                    FileInputStream fis = new FileInputStream(archivo);
                    byte[] buffer = new byte[1024];
                    int n;
                    long enviados = 0;
                    while ((n = fis.read(buffer)) != -1) {
                        dos.write(buffer, 0, n); // Envía el contenido del archivo
                        dos.flush();
                        enviados += n;
                        int porcentaje = (int)((enviados * 100) / tam);
                        System.out.print("\rEnviado: " + porcentaje + "%");
                    }
                    fis.close();
                    System.out.println("\nArchivo enviado: " + nombre);
                }
                dos.close();
                cl.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}