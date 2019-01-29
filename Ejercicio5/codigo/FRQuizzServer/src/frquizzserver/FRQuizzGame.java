/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frquizzserver;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author alfonso
 */
public class FRQuizzGame {
    private static FRQuizzGame instance = null; // singleton
    private ArrayList<PlayerHandler> handlers;
    private QuestionDeck deck;
    private boolean end;
    private boolean isFull;
    private int howManyHaveAnswered;
    private static final int MAX_PLAYERS=2;
    private Question currentQuestion;
    
    
    
    
    public FRQuizzGame() {
        end = false;
        isFull = false;
        handlers = new ArrayList();
        deck = new QuestionDeck();
    }
    
    
    
    
    // Patrón singleton
    public static FRQuizzGame getInstance()
    {
        if(instance == null)
            instance = new FRQuizzGame();
        return instance;
    }
    
    
    
    
    public static int getMaxPlayers()
    {
        return FRQuizzGame.MAX_PLAYERS;
    }
    
    
    
    
    public void addHandler(PlayerHandler newHandler)
    {
        handlers.add(newHandler);
        if (handlers.size() == FRQuizzGame.getMaxPlayers()) {
            isFull = true;
        }
    }
    
    
    
    
    public int getNumPlayers()
    {
        return handlers.size();
    }
    
    
    
    
    boolean isFull() {
        return isFull;
    }
    

    
    
    void startGame() throws IOException
    {
        deck.initDeck();
        
        // DEBUG
        System.out.println("Servidor inicia el juego");
        
        
        while(!deck.isEmpty()) {
            currentQuestion = deck.nextQuestion();
            howManyHaveAnswered = 0;
            
            // Enviar pregunta a todos los jugadores
            for (PlayerHandler handler : handlers) {
                handler.receiveQuestionPetition();
                handler.sendQuestion(currentQuestion);
            }
            for (PlayerHandler handler : handlers) {
                handler.run();
            }
            
            
            
            // Check if everybody has answered
            while(howManyHaveAnswered < getNumPlayers()) {
                for (PlayerHandler handler : handlers) {
                    if (handler.haveAnswered()) {
                        howManyHaveAnswered++;
                    }
                }
            }
            
            //DEBUG
            System.out.println("Todos han respondido");
            
            // Check answers
            for (PlayerHandler handler : handlers) {
                handler.checkAnswer(currentQuestion);
            }
            
            // DEBUG
            System.out.println("Respuestas comprobadas, mandando notificaciones");  
            
            for (PlayerHandler handler : handlers) {
                handler.run();
            }
            
            //DEBUG vaciar
            /*
            while(!deck.isEmpty()) {
                currentQuestion = deck.nextQuestion();
            }
            */
        }

        
        // Comprobar quién ha ganado
        int bestScore = 0;
        int playerScore;
        
        for(PlayerHandler handler : handlers) {
            playerScore = handler.getPlayer().getScore();
            if(playerScore > bestScore) {
                bestScore = playerScore;
            }
        }
        
        for(PlayerHandler handler : handlers) {
            playerScore = handler.getPlayer().getScore();
            handler.receiveQuestionPetition();
            if(playerScore == bestScore) {
                handler.notifyWin();
            }
            else {
                handler.notifyLoss();
            }
        }

    }
}
