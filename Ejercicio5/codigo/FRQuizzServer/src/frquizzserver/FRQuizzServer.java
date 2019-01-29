/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frquizzserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author alfonso
 */
public class FRQuizzServer {


    /**
     * @param args the command line arguments
     */        
    public static void main(String[] args) {
        // TODO code application logic here

        // Puerto de escucha
        int port=8999;

        ServerSocket serverSocket = null;

        Socket socket = null;
        
        FRQuizzGame game = FRQuizzGame.getInstance();
        
        PlayerHandler playerHandler;
        
        try {
            serverSocket = new ServerSocket(port);
            do {
               socket = serverSocket.accept(); 
               playerHandler = new PlayerHandler(socket);
               playerHandler.run();
               game.addHandler(playerHandler);
            } while(!game.isFull()); // aceptar jugadores hasta alcanzar el límite
            game.startGame();
        }
        catch(IOException e) {
            System.err.println("Error en la conexión con un cliente ");
        }
    
    }
    
}
