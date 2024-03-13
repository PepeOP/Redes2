
package eco_udp;

import java.net.*;
import java.io.*;
public class CE_ECO {
    public static void main(String[] args) {
        try {
            DatagramSocket cl = new DatagramSocket();
            System.out.println("Cliente iniciado. Escribe 'adios' para terminar la conversación.");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("Cliente: ");
                String mensaje = br.readLine();
                byte[] b = mensaje.getBytes();
                String dst = "127.0.0.1";
                int pto = 2000;
                DatagramPacket p = new DatagramPacket(b, b.length, InetAddress.getByName(dst), pto);
                cl.send(p);
                if (mensaje.equals("adios")) {
                    System.out.println("Conversación terminada.");
                    break;
                }
                DatagramPacket pEco = new DatagramPacket(new byte[2000], 2000);
                cl.receive(pEco);
                String eco = new String(pEco.getData(), 0, pEco.getLength());
                System.out.println("Servidor: " + eco);
            }
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
