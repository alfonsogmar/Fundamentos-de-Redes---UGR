/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frquizzclient;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 *
 * @author alfonso
 */
public class FRQuizzClient {

    /**
     * @param args the command line arguments
     * Simplemente se encarga de establecer la conexión con el host ya sea local o remoto,
     * del resto de transmisiones se encarga el ClientConnection
     */
    public static void main(String[] args) {
        // Nombre del host donde se ejecuta el servidor:
        String host;
        // Puerto en el que espera el servidor:
        int port=8999;

        // Socket para la conexión TCP
        Socket socketServicio=null;
        
        ClientConnection clientConnection;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduzca host:");
        host = scanner.nextLine();
        
        
        
        try {
            socketServicio = new Socket(host,port);
            
            clientConnection = new ClientConnection(socketServicio);
            clientConnection.setIOStreamsFromSocket();
            clientConnection.setUserName();
            clientConnection.sendUserName();
            
            clientConnection.receiveServerResponse();
            
            
            clientConnection.sendQuestionPetition();
            System.out.println("Esperando al servidor...");

            clientConnection.receiveQuestion();
            
            while (!clientConnection.getEnd()){

                clientConnection.printQuestion();
                int answer;
                System.out.println("Introduzca el número de la respuesta:");
                answer = scanner.nextInt();
                while(answer < -1 || answer > 4) {
                    System.out.println("Número de pregunta fuera del rango, vuelva a intentarlo");
                    answer = scanner.nextInt();
                }
                clientConnection.setAnswer(answer);
                clientConnection.sendAnswer();
                clientConnection.receiveResult();
                
                clientConnection.sendQuestionPetition();
                System.out.println("Esperando al servidor...");
                
                clientConnection.receiveQuestion();
            }
            socketServicio.close();

        } catch (UnknownHostException e) {
            System.err.println("Error: Nombre de host no encontrado.");
         } catch (IOException e) {
            System.err.println("Error de entrada/salida al conectar con servidor.");
        }
    }
}
