/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frquizzserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * @author alfonso
 */
public class PlayerHandler extends Thread {
    private byte[] outputBuffer;
    private byte[] inputBuffer = new byte[512];
    private byte[] codeBytes;
    private byte[] msgBytes;
    private Player player;
    private Socket playerSocket;
    private ServerState state;
    private boolean haveAnswered;
    private int currentAnswer;
    private boolean endGame;
    private boolean waiting;
    // Número de bytes leídos
    private int receivedBytes;
    // stream de lectura para leer datos enviados por cliente
    private InputStream inputStream;
    // stream de escritura (por aquí se envía los datos al cliente)
    private OutputStream outputStream;
    // Código de la última transmisión realiza
    private int code;
    
    private int correctAnswer;
    
    public PlayerHandler(Socket socket) {
        playerSocket = socket;
        player = new Player();
        state = ServerState.NOT_LOGGED;
        endGame = false;
        waiting = false;
    }
    
    
    @Override
    public void run() {
        //DEBUG
        System.out.println("Handler operativo");
        try {
            inputStream=playerSocket.getInputStream();
            outputStream=playerSocket.getOutputStream();
            
            while(!endGame && !waiting) {
                switch(state) {
                    case NOT_LOGGED:
                        // Log In
                        // Recibe nombre para loggear al jugador
                        String name;
                        
                        receivedBytes = inputStream.read(inputBuffer);
                        codeBytes = Arrays.copyOfRange(inputBuffer, 0, 4);
                        msgBytes = Arrays.copyOfRange(inputBuffer, 4, receivedBytes);
                        name = new String(msgBytes);
                        ByteBuffer wrapped = ByteBuffer.wrap(codeBytes); // big-endian by default

                        code = wrapped.getInt();
                        // DEBUG
                        System.out.println(name+" ha entrado al juego");
                        System.out.println("Código de la transmisión: "+code);
                        player.logInPlayer(name);
                        // Enviar confirmacion
                        sendOk();
                        state = ServerState.LOGGED;
                        break;
                    case LOGGED:
                        // Esperar hasta que se pueda contestar
                        // DEBUG
                        System.out.println("Jugador "
                                + player.getUserName() + " listo para jugar!");

                        waiting=true;
                        break;

                    case LISTENING_ANSWER:
                        // Handler manda la pregunta al cliente
                        // y espera a que cliente mande una respuesta
                        haveAnswered = false;
                        receiveAnswer();
                        waiting=true;
                        break;
                        
                    case CORRECT_ANSWER:
                        // Handler notifica al cliente de que su respuesta 
                        // era correcta y suma puntos
                        int score = 1; // Se puede modificar
                        player.addScore(score);
                        notifyCorrectAnswer();
                        waiting=true;

                        //DEBUG
                        System.out.println(player.getUserName()+" ha respondido bien");

                        break;
                        
                    case WRONG_ANSWER:
                        // Handler notifica al cliente de que su respuesta era incorrecta
                        notifyWrongAnswer();
                        waiting=true;

                        //DEBUG
                        System.out.println(player.getUserName()+" ha respondido mal");

                        break;
                    case WIN:
                        // Notifica a cliente de que ha ganado, juego termina
                        endGame = true;
                        
                    case LOSE:
                        // Notifica a cliente de que ha perdido, juego termina
                        endGame = true;

                }
            }
        } catch(IOException e) {
            System.err.println("Error al comunicar con cliente: " + e.toString());
        }

    }
    
    
    
    public Player getPlayer()
    {
        return this.player;
    }
    
    
    
    public boolean haveAnswered()
    {
        return haveAnswered;
    }
    
    
    
    public int getAnswer()
    {
        return currentAnswer;
    }
    

    
    public void sendQuestion(Question question) throws IOException
    {
        // Manda enunciado de la pregunta y sus posibles respuestas
        // a su respectivo cliente
        
        //DEBUG
        System.out.println("Mandando pregunta a " + player.getUserName());
        
        playerSocket.setTcpNoDelay(true);
        waiting = false;
        
        
        code = 500;
        int size;
        byte[] sizeBytes;
        
        byte[] statementBytes;
        byte[] categoryBytes;
        byte[] answersBytes;
        

        // Envía enunciado de pregunta
        msgBytes = question.getSentence().getBytes();
        size = msgBytes.length;
        sizeBytes = ByteBuffer.allocate(4).putInt(size).array();
                
        statementBytes = new byte[sizeBytes.length + msgBytes.length];
        System.arraycopy(sizeBytes, 0, statementBytes, 0, sizeBytes.length);
        System.arraycopy(msgBytes, 0, statementBytes, sizeBytes.length, size);

        
        // Categoría
        msgBytes = question.getCategoryString().getBytes();
        size = msgBytes.length;
        sizeBytes = ByteBuffer.allocate(4).putInt(size).array();

        categoryBytes = new byte[sizeBytes.length + msgBytes.length];
        System.arraycopy(sizeBytes, 0, categoryBytes, 0, sizeBytes.length);
        System.arraycopy(msgBytes, 0, categoryBytes, sizeBytes.length, size);

        
        // Respuestas posibles
        byte[] tempAnswersBytes;
        byte[] singleAnswerBytes;
        
        answersBytes = new byte[0];
        
        for(int i=0; i<Question.getNumPossibleAnswers();i++) {

            msgBytes = question.getPossibleAnswer(i).getBytes();    
            size = msgBytes.length;
            sizeBytes = ByteBuffer.allocate(4).putInt(size).array();
            
            // Tamaño de la respuesta + string de la respuesta
            singleAnswerBytes = new byte[sizeBytes.length + msgBytes.length];
            System.arraycopy(sizeBytes, 0, singleAnswerBytes, 0, sizeBytes.length);
            System.arraycopy(msgBytes, 0, singleAnswerBytes, sizeBytes.length, msgBytes.length);

            
            tempAnswersBytes = new byte[answersBytes.length + singleAnswerBytes.length];
            System.arraycopy(answersBytes , 0, tempAnswersBytes, 0, answersBytes.length);
            System.arraycopy(singleAnswerBytes, 0, tempAnswersBytes, answersBytes.length, singleAnswerBytes.length);            
            
            // Copiamos el contenido del array temporal
            answersBytes = new byte[tempAnswersBytes.length];
            System.arraycopy(tempAnswersBytes, 0, answersBytes, 0, tempAnswersBytes.length);
        }


        // Código para el mensaje con pregunta y posibles respuestas
        code = 500;
        codeBytes = ByteBuffer.allocate(4).putInt(code).array();

        
        // Se concatena todo en un mismo array
        // y se manda como paquete individual
        outputBuffer = new byte[codeBytes.length + statementBytes.length + categoryBytes.length + answersBytes.length];
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);
        System.arraycopy(statementBytes, 0, outputBuffer, codeBytes.length, statementBytes.length);
        System.arraycopy(categoryBytes, 0, outputBuffer, codeBytes.length+statementBytes.length, categoryBytes.length);
        System.arraycopy(answersBytes, 0, outputBuffer, codeBytes.length+statementBytes.length+categoryBytes.length, answersBytes.length);
        
        
        outputStream.write(outputBuffer,0,outputBuffer.length);
        outputStream.flush();
        
        
        waiting = false;
        state = ServerState.LISTENING_ANSWER;
    }
    
    
    
    
    public void receiveAnswer() throws IOException
    {
        receivedBytes = -1;
        do{
            receivedBytes = inputStream.read(inputBuffer);
        } while (receivedBytes == -1);
        
        
        // Tratamiento del código
        codeBytes = Arrays.copyOfRange(inputBuffer, 0, 4);
        ByteBuffer wrapped = ByteBuffer.wrap(codeBytes);
        code = wrapped.getInt();
        
        msgBytes = Arrays.copyOfRange(inputBuffer, 4, 8);
        wrapped = ByteBuffer.wrap(msgBytes);
        currentAnswer = wrapped.getInt() -1;
        
        //DEBUG
        System.out.println("Respuesta del jugador " + player.getUserName() + " recibida: " + currentAnswer);
        
        haveAnswered = true;
    }
    
    
    
    
    public void sendOkMessage() throws IOException
    {
        String msg = "OK";
        code = 200;
        
        codeBytes = ByteBuffer.allocate(4).putInt(code).array();
        msgBytes = msg.getBytes();

        // Concatenar bytes de código + bytes de mensaje
        outputBuffer = new byte[codeBytes.length + msgBytes.length];
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);
        System.arraycopy(msgBytes, 0, outputBuffer, codeBytes.length, msgBytes.length);

        outputStream.write(outputBuffer,0,outputBuffer.length);
        outputStream.flush();
        
        receivedBytes = inputStream.read(inputBuffer);
    }
    
    
    

    
    
    public void checkAnswer(Question question) {
        if(question.isCorrectAnswer(currentAnswer)) {
            state = ServerState.CORRECT_ANSWER;
        }
        else {
            state = ServerState.WRONG_ANSWER;
        }
        
        correctAnswer = question.getCorrectAnswerIndex();
        waiting=false;
    }
    
    
    
      
    public void notifyCorrectAnswer() throws IOException
    {
        code = 800;
        String msg = "CORRECT";
        codeBytes = ByteBuffer.allocate(4).putInt(code).array();
        msgBytes = msg.getBytes();
        
        // DEBUG
        System.out.println("Tamaño: "+codeBytes.length + msgBytes.length);
        
        // Concatenar bytes de código + bytes de mensaje
        outputBuffer = new byte[codeBytes.length + msgBytes.length];
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);
        System.arraycopy(msgBytes, 0, outputBuffer, codeBytes.length, msgBytes.length);

        outputStream.write(outputBuffer,0,outputBuffer.length);
        outputStream.flush();
        
        //receivedBytes = inputStream.read(inputBuffer);
    }
        
      
    
    
    public void notifyWrongAnswer() throws IOException
    {
        code = 808;
        
        codeBytes = ByteBuffer.allocate(4).putInt(code).array();
        msgBytes = ByteBuffer.allocate(4).putInt(correctAnswer).array();

        // Concatenar bytes de código + bytes de mensaje
        outputBuffer = new byte[codeBytes.length + msgBytes.length];
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);
        System.arraycopy(msgBytes, 0, outputBuffer, codeBytes.length, msgBytes.length);

        outputStream.write(outputBuffer,0,outputBuffer.length);
        outputStream.flush();
        
        //receivedBytes = inputStream.read(inputBuffer);
    }
    
    
    
    
    public void notifyWin() throws IOException
    {
        code = 1000;
        String msg = "YOU WIN";
        byte[] scoreBytes;
        
        codeBytes = ByteBuffer.allocate(4).putInt(code).array();
        msgBytes = msg.getBytes();
        scoreBytes = ByteBuffer.allocate(4).putInt(player.getScore()).array();
        
        // Concatenar bytes de código + bytes de mensaje + bytes de puntuación
        outputBuffer = new byte[codeBytes.length + msgBytes.length + scoreBytes.length];
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);
        System.arraycopy(msgBytes, 0, outputBuffer, codeBytes.length, msgBytes.length);
        System.arraycopy(scoreBytes, 0, outputBuffer, codeBytes.length+msgBytes.length, scoreBytes.length);

        outputStream.write(outputBuffer,0,outputBuffer.length);
        outputStream.flush();
        
        //receivedBytes = inputStream.read(inputBuffer);
    }
    
    
    
    public void notifyLoss() throws IOException
    {
        code = 2000;
        String msg = "YOU LOSE";
        byte[] scoreBytes;
        
        codeBytes = ByteBuffer.allocate(4).putInt(code).array();
        msgBytes = msg.getBytes();
        scoreBytes = ByteBuffer.allocate(4).putInt(player.getScore()).array();
        
        // Concatenar bytes de código + bytes de mensaje + bytes de puntuación
        outputBuffer = new byte[codeBytes.length + msgBytes.length + scoreBytes.length];
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);
        System.arraycopy(msgBytes, 0, outputBuffer, codeBytes.length, msgBytes.length);
        System.arraycopy(scoreBytes, 0, outputBuffer, codeBytes.length+msgBytes.length, scoreBytes.length);

        outputStream.write(outputBuffer,0,outputBuffer.length);
        outputStream.flush();
    }
    
    
    public void sendOk() throws IOException
    {
        code = 200;
        String msg = "OK";
        
        codeBytes = ByteBuffer.allocate(4).putInt(code).array();
        msgBytes = msg.getBytes();
        
        // Concatenar bytes de código + bytes de mensaje
        outputBuffer = new byte[codeBytes.length + msgBytes.length];
        System.arraycopy(codeBytes, 0, outputBuffer, 0, codeBytes.length);
        System.arraycopy(msgBytes, 0, outputBuffer, codeBytes.length, msgBytes.length);

        outputStream.write(outputBuffer,0,outputBuffer.length);
        outputStream.flush();
    }
    
    
    
    public void receiveQuestionPetition() throws IOException {
        receivedBytes = -1;
        do{
            receivedBytes = inputStream.read(inputBuffer);
        } while (receivedBytes == -1);
        
        
        // Tratamiento del código
        codeBytes = Arrays.copyOfRange(inputBuffer, 0, 4);
        ByteBuffer wrapped = ByteBuffer.wrap(codeBytes);
        code = wrapped.getInt();
        
        String msg;
        msgBytes = Arrays.copyOfRange(inputBuffer, 4, receivedBytes);
        msg = new String(msgBytes);
        
        if(code == 400) {
            System.out.println("Recibido " + msg);
        }
        else {
            System.err.println("Código no esperado");
            System.exit(-1);
        }
    }
    
}
