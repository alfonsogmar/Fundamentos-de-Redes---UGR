/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frquizzserver;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author alfonso
 */
public class QuestionDeck {
    private ArrayList<Question> unusedQuestions;
    private boolean emptyDeck;
    
    public QuestionDeck() {
        emptyDeck = false;
    }
    
    
    public void initDeck() {
        unusedQuestions = new ArrayList();
     
        
        Question question;
        ArrayList<String> possibleAnswers;
        
        
        possibleAnswers= new ArrayList();
        possibleAnswers.add("Enlace");
        possibleAnswers.add("Red");
        possibleAnswers.add("Aplicación");
        possibleAnswers.add("Transporte");
        question = new Question("¿A qué capa pertenece el protocolo IMAP?",
                possibleAnswers, 2, QuestionCategory.COMPUTER_NETWORKS);
        unusedQuestions.add(question);
        
        
        possibleAnswers= new ArrayList();
        possibleAnswers.add("Manila");
        possibleAnswers.add("Kuala Lumpur");
        possibleAnswers.add("Hanoi");
        possibleAnswers.add("Macao");
        question = new Question("¿Cuál es la capital de Malasia?",
                possibleAnswers, 1, QuestionCategory.GEOGRAFY);
        unusedQuestions.add(question);
        
        
        possibleAnswers= new ArrayList();
        possibleAnswers.add("O(n log n)");
        possibleAnswers.add("O(n²)");
        possibleAnswers.add("O(n)");
        possibleAnswers.add("O(log n)");
        question = new Question("¿Qué rendimiento tienen los mejores algoritmos de ordenación?",
                possibleAnswers, 0, QuestionCategory.COMPUTER_SCIENCE);
        unusedQuestions.add(question);
        
        
        possibleAnswers= new ArrayList();
        possibleAnswers.add("Sudáfrica");
        possibleAnswers.add("Inglaterra");
        possibleAnswers.add("Australia");
        possibleAnswers.add("Nueva Zelanda");
        question = new Question("¿Para qué selección nacional jugó la leyenda viva del rugby Richie McCaw?",
                possibleAnswers, 3, QuestionCategory.SPORTS);
        unusedQuestions.add(question);
        
        
        possibleAnswers= new ArrayList();
        possibleAnswers.add("1989");
        possibleAnswers.add("1975");
        possibleAnswers.add("2000");
        possibleAnswers.add("1997");
        question = new Question("¿En qué año Tim Berners-Lee consiguió establecer la primera conexión basada en HTTP?",
                possibleAnswers, 0, QuestionCategory.HISTORY);
        unusedQuestions.add(question);
        
        
        Collections.shuffle(unusedQuestions);
    }
    
    Question nextQuestion()
    {
        Question question;
        
        question = unusedQuestions.get(0);
        unusedQuestions.remove(0);
        if(unusedQuestions.isEmpty()) {
            emptyDeck = true;
        }
        
        return question;
    }
    
    
    boolean isEmpty() {
        return emptyDeck;
    }
}
