/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frquizzserver;

import java.util.ArrayList;

/**
 *
 * @author alfonso
 */
public class Question {
    private String sentence;
    private ArrayList<String> possibleAnswers;
    private int correctAnswerIndex;
    private QuestionCategory questionCategory;
    private static final int NUM_POSSIBLE_ANSWERS = 4;
    
    public Question(String sentence, ArrayList<String> possible, int correct,
            QuestionCategory category)
    {
        this.sentence       = sentence;
        possibleAnswers     = possible;
        correctAnswerIndex  = correct;
        questionCategory    = category;
    }
    
    public boolean isCorrectAnswer(String answer)
    {
        boolean isCorrect = (answer == possibleAnswers.get(correctAnswerIndex));

        return isCorrect;
    }
    
    
    public boolean isCorrectAnswer(int answer)
    {
        boolean isCorrect = (answer == correctAnswerIndex);

        return isCorrect;
    }
    
    
    public String getCategoryString()
    {
        String category = "";
        
        switch (questionCategory) {
            case COMPUTER_NETWORKS:
                category = "Redes";
                break;
            case COMPUTER_SCIENCE:
                category = "Ciencias de la computación";
                break;
            case GEOGRAFY:
                category = "Geografía";
                break;
            case HISTORY:
                category = "Historia";
                break;
            case SPORTS:
                category = "Deportes";
                break;
        }
        
        return category;
    }
    
    
    
    
    String getSentence() {
        return sentence;
    }
    
    
    
    
    String getPossibleAnswer(int index) {
        return possibleAnswers.get(index);
    }
    
    
    
    
    static int getNumPossibleAnswers() {
        return NUM_POSSIBLE_ANSWERS;
    }
    
    
    
    
    public int getCorrectAnswerIndex() {
        return this.correctAnswerIndex;
    }
}
