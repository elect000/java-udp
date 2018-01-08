import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.FileInputStream;
import java.text.MessageFormat;

public class AnimUDPServerColor0 {
    public static void main (String args []){
        try {
            BufferedInputStream bitStream;
            int port = 8001;
            InetAddress clientAddress;
            int clientPort;

            int waitTime = 100;

            String filename = "bane.raw";
            int width = 160;
            int height = 120;
            int framesize = 160 * 120;
            int headersize = 12;

            if (args.length >= 1) {
                filename = args[0];
            }

            // DatagramPacket for sending
            byte buf [] = new byte [framesize];
            DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);

            // DatagramPacket for receive
            byte req [] = new byte [32];
            DatagramPacket receivePacket = new DatagramPacket(req, req.length);

            // DatagramPacket for sending file info ---------------
            byte ifile [] = new byte [headersize];
            DatagramPacket info = new DatagramPacket(ifile, ifile.length);
            // --------------------------------------------------

            // set socket (port 8001)
            DatagramSocket socket = new DatagramSocket(port);
            System.out.println ("Running ...");
            while (true) {
                // main Thread ---------------------------------------

                socket.receive (receivePacket); // receive request
                clientAddress = receivePacket.getAddress ();
                clientPort = receivePacket.getPort ();
                // socket.send (receivePacket); // Echo back (Ack)
                // -----------------------------------------------------

                // child Thread ----------------------------------------
                // settings for IPaddress, PortNo and Data-length
                sendPacket.setAddress (clientAddress);
                sendPacket.setPort (clientPort);
                sendPacket.setLength (framesize);

                // settings for ...
                info.setAddress (clientAddress);
                info.setPort (clientPort);
                info.setLength (headersize);

                System.out.println("Receive:" + clientPort + " " + clientAddress + " " + framesize);

                bitStream = new BufferedInputStream(new FileInputStream(filename));
                // analyze file information ------------
                bitStream.read(ifile, 0, headersize);

                if((((ifile[0] & 0xff) << 8) + (ifile[1] & 0xff))
                   == 0xfffe) { // This file have a header

                    System.out.println(filename + " has a header");
                    System.out.print("Info: ");

                    switch (((ifile[2] & 0xff) << 8) + (ifile[3] & 0xff)) {
                    case 1 :
                        System.out.print("Gray Scale ");
                        break;
                    case 3 :
                        System.out.print("Color(3) ");
                        break;
                    default:
                        throw new Exception("File: " + filename + " had a invalid header");
                    }
                    System.out.print("Max Value: " + (((ifile[4] & 0xff) << 8) + (ifile[5] & 0xff)) +  " ");
                    System.out.print("Width: "     + (((ifile[6] & 0xff) << 8) + (ifile[7] & 0xff)) +  " ");
                    System.out.print("Height: "    + (((ifile[8] & 0xff) << 8) + (ifile[9] & 0xff)) +  " ");
                    System.out.print("Frame #: "   + (((ifile[10] & 0xff) << 8) + (ifile[11] & 0xff)) +  "\n");

                    socket.send (info); // send infomation // Echo back (Ack)
                } else {
                    bitStream.close ();
                    throw new Exception( "Invalid File:" + filename + "  has no header!!!");
                }
                // ----------------------------------

                for (int i = 0; i < 200; i++) {
                    bitStream.read (buf, 0, framesize); // read from buffer
                    socket.send (sendPacket); // send data to client
                    Thread.sleep (waitTime);      // wait some seconds
                }
                bitStream.close ();
                // --------------------------------------------------------
            }
            // socket.close();
        }
        catch (Exception e) {
            System.out.println("Exception " + e);
        }

    }
}
