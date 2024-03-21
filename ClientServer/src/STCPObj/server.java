package STCPObj;
import java.net.*;
import java.io.*;
import java.util.List;
import java.util.LinkedList;
public class server {
    public static void main(String[] args) {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ServerSocket s = new ServerSocket(742);
            System.out.println("Servicio iniciado... Esperando cliente");
            for (;;) {
                Socket cl = s.accept();
                System.out.println("Cliente conectado desde " + cl.getInetAddress() + ":" + cl.getPort());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                oos = new ObjectOutputStream(cl.getOutputStream());
                ois = new ObjectInputStream(cl.getInputStream());
                long fileSize = ois.readLong();
                System.out.println("Tamaño del archivo recibido: " + fileSize);
                byte[] fileData = new byte[(int) fileSize];
                String nombre = "SoyUnNuevoArchivo";
                System.out.println("\n\nRecibimos el archivo");
                long tam = dis.readLong();
                System.out.println("\n\nTamano " + tam);

                DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));
                long recibidos = 0;
                int n, porcentaje;
                while(recibidos<tam){
                    n = dis.read(fileData);//Lee bytes
                    dos.write(fileData,0,n);//Lee de 0 a n
                    dos.flush();
                    recibidos = recibidos + n;
                    porcentaje = (int)(recibidos * 100/tam);
                    System.out.println("Recibido: " + porcentaje + "%\r");
                }//while
                System.out.println("\n\nArchivo recibido correctamente");
                ByteArrayInputStream bis = new ByteArrayInputStream(fileData);
                ObjectInputStream oisFile = new ObjectInputStream(bis);
                int numeroObjetos2 = oisFile.readInt();
                System.out.println("Número de objetos Objeto2: " + numeroObjetos2);
                List<Objeto2> objetos2 = new LinkedList<>();
                for (int i = 0; i < numeroObjetos2; i++) {
                    Objeto2 objeto2 = (Objeto2) oisFile.readObject();
                    objetos2.add(objeto2);
                }
                ListaObjetos1 listaObjeto1 = (ListaObjetos1) oisFile.readObject();
                System.out.println("Objeto ListaObjeto1 recibido: " + listaObjeto1);
                boolean res = dis.readBoolean();
                if(res==true)
                    System.out.println("Resultado correcto\n");
                else
                    System.out.println("Resultado incorrecto\n");
                
                oos.flush();
                dis.close();
                bis.close();
                oisFile.close();
                cl.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
