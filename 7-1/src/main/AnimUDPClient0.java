import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


// class AnimUDPClient0{
//     public static void main(String[] args){
//         AppFrame3 f=new AppFrame3(args[0]);
//         f.setSize(640,480);
//         f.addWindowListener(new WindowAdapter(){
//                 @Override public void windowClosing(WindowEvent e){
//                     System.exit(0);
//                 }});
//         f.setVisible(true);
//     }
// }

// class AppFrame3 extends Frame{
//     String hostname;
//     ImageSocket2 imgsock = null;
//     AppFrame3(String hostname){
//         this.hostname = hostname;
//     }
//     @Override public void update(Graphics g){
//         paint(g);
//     }
//     @Override public void paint(Graphics g){
//         if(imgsock != null){
//             Image img=imgsock.loadNextFrame();
//             if(img!=null)
//                 // drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
//                 g.drawImage(img,10,50,480,360,this);
//         }else{
//             imgsock = new ImageSocket2(hostname);
//         }
//         repaint(1);
//     }
// }

// class ImageSocket2{
//     DatagramSocket socket;
//     InetAddress serverAddress;
//     BufferedImage bImage;
//     byte buf[];
//     int port=8001;
//     byte ack[];
//     DatagramPacket receivePacket1;
//     DatagramPacket receivePacket2;
//     DatagramPacket receivePacket3;
//     DatagramPacket ackPacket;
//     boolean fin = false;
//     ImageSocket2(String hostname){
//         buf=new byte[160*120*3];
//         bImage=new BufferedImage(160, 120, BufferedImage.TYPE_3BYTE_BGR);
//         byte request[] = "REQUEST".getBytes();
//         ack = "Ack".getBytes();	
		
//         try{
//             socket = new DatagramSocket();

//             serverAddress = InetAddress.getByName(hostname);
//             DatagramPacket sendPacket =
//                 new DatagramPacket(request, request.length, serverAddress, port);
//             ackPacket =
//                 new DatagramPacket(ack,ack.length,serverAddress,port);
//             receivePacket1 = new DatagramPacket(buf,160*120*0,160*120);
//             receivePacket2 = new DatagramPacket(buf,160*120*1,160*120);
//             receivePacket3 = new DatagramPacket(buf,160*120*2,160*120);
			
//             socket.setSoTimeout(3000);
//             socket.send(sendPacket);
//             socket.receive(receivePacket1);
//             receivePacket1.setLength(160*120);
//         }
//         catch(IOException e){
//             System.out.println("Exception:"+e);
//         }
//     }
	
//     Image loadNextFrame(){
//         if(fin) return null;
//         try{
//             int x,y,pixel,r,g,b;

//             socket.receive(receivePacket1);
//             socket.send(ackPacket);
//             if(buf[0]<0){
//                 socket.close();
//                 System.out.println("Done");
//                 fin = true;
//                 return null;
//             }
//             socket.receive(receivePacket2);
//             socket.send(ackPacket);
//             socket.receive(receivePacket3);

//             for(y=0;y<120;y++){
//                 for(x=0;x<160;x++){
//                     r = (int)buf[y*160*3+x*3+0]*2;
//                     g = (int)buf[y*160*3+x*3+1]*2;
//                     b = (int)buf[y*160*3+x*3+2]*2;
//                     pixel=new Color(r,g,b).getRGB();
//                     bImage.setRGB(x,y,pixel);
//                 }
//             }
//             socket.send(ackPacket);
//         }
//         catch(Exception e){
//             System.err.println("Exception2: "+e);
//         }
//         return bImage;
//     }
// }
