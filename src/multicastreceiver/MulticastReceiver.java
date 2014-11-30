/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multicastreceiver;

/**
 *
 * @author Лена
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.media.Player;
import javax.media.Manager;

/**
 *
 * @author Admin
 */
public class MulticastReceiver {
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Thread thread;
        Runnable mythread;
        BufferedInputStream stream;
        JFrame video;
        Player player;
        Component component;
        
        video = new JFrame("My iptv player");
        video.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        video.setSize(300, 300);
        player = Manager.createPlayer(null);
        //player.addControllerListener(this);
        component = player.getVisualComponent();
        video.add(component);

        video.setVisible(true);
        player.start();
                        
        mythread = new Runnable() {

            @Override
            public void run() {
                try {
                    MulticastSocket socket;
                    DatagramPacket packet;
                    byte[] buff = new byte[512];
                    int cnt = 0;
                    
                    packet = new DatagramPacket(buff, buff.length);
                    socket = new MulticastSocket(1234);
                    socket.setInterface(InetAddress.getByName("192.168.1.200"));
                    socket.joinGroup(InetAddress.getByName("238.0.0.38"));
                    while(cnt<20){
                        socket.receive(packet);
                        int i=0; int x=0;
                        while(i<buff.length) {
                            if(x==10) { System.out.print("\n"); x=0; }
                            StringBuilder sb = new StringBuilder("");
                            if(Integer.toHexString(Byte.toUnsignedInt(buff[i])).length()==1) {
                                sb.append("0");
                                sb.append(Integer.toHexString(Byte.toUnsignedInt(buff[i])));
                            } else {
                                sb.append(Integer.toHexString(Byte.toUnsignedInt(buff[i])));
                            }
                            System.out.print(" "+sb.toString());
                            i++;
                            x++;
                        }
                        cnt++;
                    }
                    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                } catch (IOException ex) {
                    Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        thread = new Thread(mythread);
        thread.start();
        thread.join();
        stream = new BufferedInputStream(null);
        video.dispose();
    }
    
    
}
