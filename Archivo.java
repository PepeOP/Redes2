package STCPObj;

import java.net.*;
import java.io.*;


public class Servidor_Serializado{
    public static void main(String[] args){
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try{
            int port = 9000;
            ServerSocket s = new ServerSocket(port);
            System.out.println("Servidor iniciado...");
            for(;;){
                Socket cl = s.accept();
                System.out.println("\n\nConexion establecida desde" + cl.getInetAddress()+ ":" + cl.getPort());

                DataInputStream dis = new DataInputStream(cl.getInputStream());
                oos = new ObjectOutputStream(cl.getOutputStream());

                byte[] b = new byte[20000];
                //**************Leer archivo**************
                String nombre = "SoyUnNuevoArchivo";
                System.out.println("\n\nRecibimos el archivo");
                long tam = dis.readLong();
                System.out.println("\n\nTamano " + tam);

                DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));//Flujo de salida
                long recibidos = 0;
                int n, porcentaje;
                while(recibidos<tam){
                    n = dis.read(b);//Lee bytes
                    dos.write(b,0,n);//Lee de 0 a n
                    dos.flush();
                    recibidos = recibidos + n;
                    porcentaje = (int)(recibidos * 100/tam);
                    System.out.println("Recibido: " + porcentaje + "%\r");
                }//while
                System.out.println("\n\nArchivo recibido correctamente");

                
                ois = new ObjectInputStream(new FileInputStream(nombre));

                //**************Leer Objetos2**************
                Objeto2 o2;
                int objectNumber2 = ois.readInt();
                System.out.println("Numero de objetos tipo Objeto2: "+objectNumber2);
                oos.writeInt(objectNumber2);     oos.flush();
                for (int i = 0; i < objectNumber2; i++){
                    o2 = (Objeto2)ois.readObject();
                    o2.imprimeObjeto2();
                    oos.writeObject(o2);  oos.flush();
                    System.out.println("Objeto2 #"+i+" enviado\n");
                }
                System.out.println("\n\nTodos los Objetos2 se enviaron bien");

                //**************Leer Objetos1**************
                ListaObjetos1 o1;
                o1 = (ListaObjetos1)ois.readObject();
                int objectNumber1 = o1.getTamano();
                System.out.println("Numero de objetos tipo Objeto1: "+objectNumber1);
                oos.writeInt(objectNumber1);     oos.flush();
                for (int i = 0; i < objectNumber1; i++){
                    o1.getObjeto(i);
                    o1.muestraObjeto1();
                    oos.writeObject(o1.getObjeto(i));  oos.flush();
                    System.out.println("Objeto1 #"+i+" enviado\n");
                }
                System.out.println("\n\nTodos los Objetos1 se enviaron bien");

                //**************Leer resultado**************
                boolean res = dis.readBoolean();
                if(res==true)
                    System.out.println("Resultado correcto\n");
                else
                    System.out.println("Resultado incorrecto\n");

                System.out.println("\n\n...................................");
                dis.close(); dos.close(); cl.close();
                
            }
        }catch(Exception e){    //Manejo de excepciones
            e.printStackTrace();
        }
    }
}