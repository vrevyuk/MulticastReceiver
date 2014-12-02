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
import com.sun.jndi.toolkit.url.Uri;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 *
 * @author Admin
 */
public class MulticastReceiver {
       private static Thread threadListener=null;
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedInputStream stream;
        JFrame jframe;
        JTextArea textArea;
        Component component;
        
        jframe = new JFrame("My iptv player");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(300, 300);
        jframe.addWindowListener(new WindowListener() {

            @Override
            public void windowClosing(WindowEvent e) {
                if(threadListener!=null) threadListener.interrupt();
                System.out.println("Closing app");
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void windowOpened(WindowEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void windowClosed(WindowEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void windowIconified(WindowEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void windowActivated(WindowEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        textArea = new JTextArea();
        textArea.setAutoscrolls(true);
        textArea.setLineWrap(false);
        textArea.setColumns(20);
        textArea.setRows(20);
        textArea.setEditable(false);
        //textArea.append("jhvsdcjsdbvncbksdn");
        
        jframe.setVisible(true);
        jframe.add(textArea);
        
                        
        
        threadListener = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socketL;
                DatagramPacket packetL;
                byte[] buff = new byte[1024];
                Proxy proxy=null;
              
                try {
                    socketL = new DatagramSocket(1971);
                    packetL = new DatagramPacket(buff, buff.length);
                    while(true) {
                        socketL.receive(packetL);
                        String str = new String(packetL.getData(), 0, packetL.getLength());
                        //System.out.println("Received from "+packetL.getAddress().getHostName()+
                        //        ":"+packetL.getPort()+" >>"+str);
                        textArea.append("Received request from "+packetL.getAddress().getHostName()+
                                ":"+packetL.getPort()+" >> "+str+"\n");
                        if(proxy==null) {
                            proxy = new Proxy(str, packetL.getAddress().getHostName(), packetL.getPort());
                            proxy.start();
                        } else {
                            if(proxy.isInterrupted()) break;
                        }
                    }
                } catch (SocketException ex) {
                    Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(proxy!=null) { 
                    proxy.interrupt();
                }
            }
        });
        threadListener.run();
        threadListener.join();
        jframe.dispose();
    }
    
}
