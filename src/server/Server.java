package server;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable {

    private DatagramSocket socket;
    private boolean running = false;
    private int port;
    private Thread run, manage, send, receive;

    public Server(int port){
        this.port=port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        run = new Thread(this, "Server");
    }

    @Override
    public void run(){
        running = true;
        manageClients();
        receive();
    }

    private void manageClients() {
        manage = new Thread("Manage"){
            public void run(){
                while(running){

                    //********

                }
            }
        };
        manage.start();
    }

    private void receive() {
        receive = new Thread("Receive"){
            public void run(){

                //********

            }
        };
        receive.start();
    }

}
