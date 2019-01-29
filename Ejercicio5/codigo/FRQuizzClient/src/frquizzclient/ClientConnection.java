/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frquizzclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author alfonso
 */
public class ClientConnection {
        
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    
    private int answer;
    private String userName;

    private byte [] outputBuffer;
    private byte [] inputBuffer=new byte[256];
    private int bytes=0;
    private int code;
    private byte [] codeBytes;
    private byte [] msgBytes;
    private int receivedBytes;
    private int size;
    private byte [] sizeBytes;
    private boolean end;
    
    // Datos de pregunta
    private String statement, category;
    private ArrayList<String> possibleAnswers ;


    public ClientConnection(Socket s) {
        socket = s;
        possibleAnswers = new ArrayList(4);
        end = false;
    }
        
    public void setIOStreamsFromSocket() throws IOException{
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }
                
    
    
    // Por ahora por consola, pero debe hacerse por la GUI
    public void setUserName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduzca el nombre de usuario:");
        userName = scanner.nextLine();
    }
    
    
    
    public void sendUserName() throws IOException{

        code = 100;
        codeBytes = ByteBuffer.allocate(4).putInt(code).array();
        msgBytes = userName.getBytes();

        // Concatenar bytes de código + bytes de mensaje
        outputBuffer = new byte[codeBytes.length + msgBytes.length];
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);
        System.arraycopy(msgBytes, 0, outputBuffer, codeBytes.length, msgBytes.length);

        outputStream.write(outputBuffer,0,outputBuffer.length);
        outputStream.flush();
    }
    
    

    
    public void sendAnswer() throws IOException{
        code = 600;
        codeBytes = ByteBuffer.allocate(4).putInt(code).array();
        
        msgBytes = ByteBuffer.allocate(4).putInt(answer).array();

        outputBuffer = new byte[codeBytes.length + msgBytes.length];
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);
        System.arraycopy(msgBytes, 0, outputBuffer, codeBytes.length, msgBytes.length);

        outputStream.write(outputBuffer,0,outputBuffer.length);
        outputStream.flush();
    }
    
    
    
    public void setAnswer(int answer) {
        this.answer = answer;
    }
    
    
    // Por ahora por consola, pero debe hacerse por la GUI
    public void setAnswerFromConsole() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduzca el número de la respuesta:");
        answer = scanner.nextInt();
    }
    
    /*
    public void setAnswerFromGUI() {
    
    }
    */

    
    
    public void receiveQuestion() throws IOException
    {
        int offset=0;
        
        receivedBytes = -1;
        do{
            receivedBytes = inputStream.read(inputBuffer);
        } while (receivedBytes == -1);
        
        
        
        
        // Tratamiento del código
        codeBytes = Arrays.copyOfRange(inputBuffer, 0, 4);
        ByteBuffer wrapped = ByteBuffer.wrap(codeBytes);
        code = wrapped.getInt();
        
        
        if(code == 1000){
            int score;
            byte[] scoreBytes;
            msgBytes = Arrays.copyOfRange(inputBuffer, 4, receivedBytes-4);
            scoreBytes = Arrays.copyOfRange(inputBuffer, receivedBytes-4, receivedBytes);
            wrapped = ByteBuffer.wrap(scoreBytes);
            score = wrapped.getInt();
            String msg = new String(msgBytes);
            System.out.println("¡Has ganado la partida! " + msg);
            System.out.println("Puntiación final: " + score);
            end=true;
        }
        else if(code == 2000) {
            int score;
            byte[] scoreBytes;
            msgBytes = Arrays.copyOfRange(inputBuffer, 4, receivedBytes-4);
            scoreBytes = Arrays.copyOfRange(inputBuffer, receivedBytes-4, receivedBytes);
            wrapped = ByteBuffer.wrap(scoreBytes);
            score = wrapped.getInt();
            String msg = new String(msgBytes);
            System.out.println("Lo siento, has perdido " + msg);
            System.out.println("Puntuación final: " + score);
            end=true;
        }
        else {
            offset += 4;
            
            // Tratamiento de enunciado
            sizeBytes = Arrays.copyOfRange(inputBuffer, offset, offset+4);
            wrapped = ByteBuffer.wrap(sizeBytes);
            size = wrapped.getInt();
            offset += 4;
            msgBytes = Arrays.copyOfRange(inputBuffer, offset, offset+size);
            statement = new String(msgBytes);
            offset += size;


            // Tratamiento de categoría
            sizeBytes = Arrays.copyOfRange(inputBuffer, offset, offset+4);
            wrapped = ByteBuffer.wrap(sizeBytes);
            size = wrapped.getInt();
            offset += 4;
            msgBytes = Arrays.copyOfRange(inputBuffer, offset, offset+size);
            category = new String(msgBytes);
            offset += size;


            // Tratamiento de respuestas posibles
            String answerString;
            possibleAnswers.clear();
            for(int i=0; i<4; i++) {
                sizeBytes = Arrays.copyOfRange(inputBuffer, offset, offset+4);
                wrapped = ByteBuffer.wrap(sizeBytes);
                size = wrapped.getInt();
                offset += 4;
                msgBytes = Arrays.copyOfRange(inputBuffer, offset, offset+size);
                answerString = new String(msgBytes);
                possibleAnswers.add(answerString);
                offset += size;
            }
        }
    }
    
    
    
    public void receiveResult() throws IOException {
        do {
            receivedBytes = inputStream.read(inputBuffer);
        } while (receivedBytes == -1);
        codeBytes = Arrays.copyOfRange(inputBuffer, 0, 4);
        msgBytes = Arrays.copyOfRange(inputBuffer, 4, receivedBytes);
        ByteBuffer wrapped = ByteBuffer.wrap(codeBytes); // big-endian by default
        code = wrapped.getInt();
        
        if(code == 800) {
            // Respuesta correcta
            String msg = new String(msgBytes);
            if(!msg.equals("CORRECT")) {
                System.err.println("ERROR: código no se corresponde con mensaje");
                System.exit(-1);
            }
            this.printCorrect();
        }
        else if(code == 808) {
            // Respuesta incorrecta
            int actualAnswer;
            wrapped = ByteBuffer.wrap(msgBytes);
            actualAnswer = wrapped.getInt();
            
            this.printWrong(actualAnswer);
        }
        else {
            System.err.println("ERROR: mensaje" + code + "del servidor desconocido");
            System.exit(-1);
        }
        
        //msgBytes = Arrays.copyOfRange(inputBuffer, 4, receivedBytes);
    }
    
    
    
    
    public void receiveServerResponse() throws IOException {
        // 1er paquete: code + pregunta
        String msg;

        receivedBytes = inputStream.read(inputBuffer);
        codeBytes = Arrays.copyOfRange(inputBuffer, 0, 4);
        msgBytes = Arrays.copyOfRange(inputBuffer, 4, receivedBytes);
        msg = new String(msgBytes);
        ByteBuffer wrapped = ByteBuffer.wrap(codeBytes); // big-endian by default
        code = wrapped.getInt();
        
        if (code != 200) {
            System.err.println("ERROR no admitido...");
            System.exit(-1);
        }
        else {
            System.out.println(msg + " " + userName + " loggeado con éxito");
        }

    }
    
    
    public void sendQuestionPetition() throws IOException
    {
        code = 400;
        codeBytes = ByteBuffer.allocate(4).putInt(code).array();
        String msg = "NEXT";
        msgBytes = msg.getBytes();
        
        outputBuffer = new byte[codeBytes.length + msgBytes.length];
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);

        outputStream.write(outputBuffer,0,outputBuffer.length);
        outputStream.flush();
    }
    
    
    
    public void printQuestion() {
        System.out.println("Categoría: "+category);
        System.out.println(statement);
        
        for(int i=0; i<possibleAnswers.size(); ++i) {
            System.out.println((i+1)+": "+possibleAnswers.get(i));
        }
    }
    

    
    
    public void printCorrect() {
        System.out.println("¡Respuesta correcta!");
    }
    
    
    
    
    public void printWrong(int actualAnswer) {
        System.out.println("¡Respuesta incorrecta! La respuesta correcta era: " + possibleAnswers.get(actualAnswer));
    }
    
    
    public boolean getEnd() {
        return end;
    }
}
