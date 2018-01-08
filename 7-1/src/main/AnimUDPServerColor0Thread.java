import java.io.BufferedInputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.FileInputStream;
import java.text.MessageFormat;

public class AnimUDPServerColor0Thread {
    public static class UDPThread extends Thread{
        InetAddress clientAddress;
        int clientPort;
        BufferedInputStream bitStream;
        String filename = "bane.raw";
        int waitTime = 100;
        int width = 160;
        int height = 120;
        int framesize = 160 * 120;
        int headersize = 12;
        DatagramSocket socket;
        DatagramPacket receivePacket;
        // DatagramPacket for sending
        byte buf [] = new byte [framesize];
        DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);

        // DatagramPacket for sending file info ---------------
        byte ifile [] = new byte [headersize];
        DatagramPacket info = new DatagramPacket(ifile, ifile.length);
        // --------------------------------------------------

        public UDPThread (DatagramPacket receivePacket) {
            this.receivePacket = receivePacket;
            // child Thread ----------------------------------------
            this.clientAddress = receivePacket.getAddress ();
            this.clientPort = receivePacket.getPort ();
            // settings for IPaddress, PortNo and Data-length
            this.sendPacket.setAddress (clientAddress);
            this.sendPacket.setPort (clientPort);
            this.sendPacket.setLength (framesize);
            // settings for ...
            this.info.setAddress (clientAddress);
            this.info.setPort (clientPort);
            this.info.setLength (headersize);
            System.out.println("Receive:" + clientPort + " " + clientAddress + " " + framesize);
        }

        public void run (){
            try {
                this.socket = new DatagramSocket();
                this.bitStream = new BufferedInputStream(new FileInputStream(filename));
                // analyze file information ------------
                this.bitStream.read(ifile, 0, headersize);
                analyzeHeader(ifile, socket, info, filename, bitStream);
                // ----------------------------------
                for (int i = 0; i < 200; i++) {
                    this.bitStream.read (this.buf, 0, this.framesize); // read from buffer
                    this.socket.send (this.sendPacket); // send data to client
                    Thread.sleep (this.waitTime);      // wait some seconds
                }
                this.buf[0] = -1; // end
                this.socket.send(this.sendPacket);
                this.bitStream.close ();
                this.socket.close();
                System.out.println("Close:" + clientPort + " " + clientAddress);
                // --------------------------------------------------------
            }
            catch (Exception e) {
                System.out.println("Exception " + e);
            }
        }
    }

    static void analyzeHeader (byte ifile [],DatagramSocket socket, DatagramPacket info, String filename, BufferedInputStream bitStream){
        try{
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
                throw new Exception( "Invalid File:  " + filename + "  has no header!!!");
            }
            
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main (String args []){
        try {
            int port = 8001;

            // DatagramPacket for receive
            byte req [] = new byte [32];
            DatagramPacket receivePacket = new DatagramPacket(req, req.length);

            // set socket (port 8001)
            DatagramSocket socket = new DatagramSocket(port);
            System.out.println ("Running ...");

            while (true) {
                socket.receive (receivePacket); // receive request
                UDPThread thread = new UDPThread(receivePacket);
                thread.start ();
            }
            // socket.close();
        }
        catch (Exception e) {
            System.out.println("Exception " + e);
        }

    }
}
