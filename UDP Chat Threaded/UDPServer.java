/* Michael Woo
   Assignment 5
   4/14/14
*/
import java.net.*;
import java.io.*;
import java.util.*;

class MultiThreadedUDPServerProtocol implements Runnable {
    
    private static HashSet<Integer> portNum = new HashSet<Integer>();    
    private DatagramSocket socket = null;
    private DatagramPacket packet = null;
    
    //constructor
    public MultiThreadedUDPServerProtocol (DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
    }
    
    public void run() {
        try {
                if(socket.isClosed()){
                    System.exit(1);
                }
                String message = (new String(packet.getData())).trim();
                System.out.println("Client Connected to Port: " + packet.getPort());

                System.out.println("Message: "  + message );
        
                //get IP Address
                InetAddress clientIP = packet.getAddress();
                //get client port
                int clientPort = packet.getPort();
                //keep track of all port being used
                String messageTemp = "Client " + clientPort + ": " + message;
                //System.out.println("Message from "  + messageTemp + ": ");
                portNum.add(clientPort);
        
                //create buffer to send and send
                byte sendData[] = new byte[1024];
                sendData = messageTemp.getBytes();
        
                //send message to every port connected
                for(Integer port : portNum) {
                    if(port != clientPort ) {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, port);
                        socket.send(sendPacket);
                    }
                }

            }
        catch (IOException ex) {
            ex.getMessage();
        }
    }
}

public class UDPServer {
    
    public static void main (String args[]) throws IOException {
        
        
        if (args.length != 1)
            throw new IllegalArgumentException("Parameter(s): <Port>");
        int servPort = Integer.parseInt(args[0]);
        
        DatagramSocket socket = new DatagramSocket(servPort);
        DatagramPacket receivePacket = null;
        
        while(true) {
            byte[] receiveData = new byte[1024];
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            //wait for client to send something
            socket.receive(receivePacket);
            //give it to a thread
            Thread thread = new Thread( new MultiThreadedUDPServerProtocol(socket,receivePacket));
            thread.start();
            
        }
    }
}