/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multicastreceiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Лена
 */
    public class Proxy extends Thread {
        private int groupPort;
        private InetAddress groupAddr;
        private InetAddress senderAddr;
        private int senderPort;
        
        public Proxy(String url, String ipaddr, int port) {            
            if(url.length()>0) {
                try {
                    URI uri = URI.create(url);
                    groupPort = uri.getPort();
                    groupAddr = InetAddress.getByName(uri.getHost());
                    senderAddr = InetAddress.getByName(ipaddr);
                    senderPort = port;
                } catch (UnknownHostException ex) {
                    Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
                    groupPort=0;
                    groupAddr=null;
                    senderAddr=null;
                    senderPort=0;
                }
                System.out.println("Group:"+groupAddr.getHostAddress()+" gport:"+groupPort+
                        " to:"+senderAddr.getHostAddress()+" sport:"+senderPort);
            }
        }
        
        @Override
        public void run() {
            Thread t;
            MulticastSocket socket;
            DatagramPacket packet;
            DatagramSocket txSocket=null;
            DatagramPacket txPacket=null;
            //byte[] buff = new byte[1324];
            byte[] buff = new byte[4096];
                //super.start(); //To change body of generated methods, choose Tools | Templates.
            if(groupAddr==null || groupPort==0 || senderAddr==null || senderPort==0) return;
                try {
                    
                    packet = new DatagramPacket(buff, buff.length);
                    socket = new MulticastSocket(groupPort);
                    socket.setInterface(InetAddress.getByName("192.168.1.99"));
                    socket.joinGroup(groupAddr);
                    while(true){
                        socket.receive(packet);
                        //System.out.println("size "+packet.getLength());
                        if(txSocket==null) txSocket = new DatagramSocket(senderPort);
                        txPacket = new DatagramPacket(buff, packet.getLength(), senderAddr, groupPort);
                        txSocket.send(txPacket);
                        System.out.print('.');

                        t = Thread.currentThread();
                        if(t!=null) {
                            if(t.isInterrupted()) {
                                System.out.println("Breaking thread ...");
                                socket.leaveGroup(groupAddr);
                                socket.close(); socket=null; packet=null;
                                txSocket.close(); txSocket=null; txPacket=null;
                                break;
                            }
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
