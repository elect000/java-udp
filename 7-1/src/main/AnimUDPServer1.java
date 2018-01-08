import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.FileInputStream;

public class AnimUDPServer1 {
    public static void main (String args []){
        try {
            BufferedInputStream bitStream;
            int port = 8000;
            InetAddress clientAddress;
            int clientPort;

            int waitTime = Integer.parseInt(args[0]);

            String filename = "bane.raw";
            int width = 160;
            int height = 120;
            int framesize = 160 * 120;

            // DatagramPacket for sending
            byte buf [] = new byte [framesize];
            DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);

            // DatagramPacket for receive
            byte req [] = new byte [32];
            DatagramPacket receivePacket = new DatagramPacket(req, req.length);

            // set socket (port 8000)
            DatagramSocket socket = new DatagramSocket(port);
            System.out.println ("Running ...");
            while (true) {
                socket.receive (receivePacket); // receive request
                clientAddress = receivePacket.getAddress ();
                clientPort = receivePacket.getPort ();
                socket.send (receivePacket); // Echo back (Ack)

                // settings for IPaddress, PortNo and Data-length
                sendPacket.setAddress (clientAddress);
                sendPacket.setPort (clientPort);
                sendPacket.setLength (framesize);

                bitStream = new BufferedInputStream(new FileInputStream(filename));
                for (int i = 0; i < 200; i++) {
                    bitStream.read (buf, 0, 160*120); // read from buffer
                    socket.send (sendPacket); // send data to client
                    Thread.sleep (waitTime);      // wait some seconds
                }
                bitStream.close ();
            }
            // socket.close();
        }
        catch (Exception e) {
            System.out.println("Exception " + e);
        }

    }
}
