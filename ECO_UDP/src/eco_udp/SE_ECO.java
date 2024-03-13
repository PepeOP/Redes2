
package eco_udp;

import java.net.*;

public class SE_ECO {
    public static void main(String[] args) {
        try {
            DatagramSocket s = new DatagramSocket(2000);
            System.out.println("Servidor iniciado, esperando cliente");
            while (true) {
                DatagramPacket p = new DatagramPacket(new byte[2000], 2000);
                s.receive(p);
                
                InetAddress clientAddress = p.getAddress();
                int clientPort = p.getPort();
                
                String mensaje = new String(p.getData(), 0, p.getLength());
                System.out.println("Cliente (" + clientAddress + ":" + clientPort + "): " + mensaje);
                
                if (mensaje.equals("adios")) {
                    System.out.println("Cliente (" + clientAddress + ":" + clientPort + ") se desconectó.");
                    continue;
                }
                
                DatagramPacket pEco = new DatagramPacket(p.getData(), p.getLength(), clientAddress, clientPort);
                s.send(pEco);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
